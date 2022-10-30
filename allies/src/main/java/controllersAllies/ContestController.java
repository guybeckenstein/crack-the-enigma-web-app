package controllersAllies;

import jar.clients.battlefield.Battlefield;
import jar.clients.battlefield.Difficulty;
import jar.common.rawData.agents.AgentsRawData;
import jar.common.rawData.battlefieldContest.AlliesData;
import jar.common.rawData.battlefieldContest.ContestRawData;
import jar.common.rawData.battlefieldContest.TeamCandidates;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import timerTasks.contest.IsUBoatQuitsTimerTask;
import timerTasks.contest.pre.*;
import timerTasks.contest.start.ContestFinishedTimerTask;
import timerTasks.contest.start.PullInformationTimerTask;
import timerTasks.contest.start.TasksInformationTimerTask;

import java.io.IOException;
import java.util.*;

import static http.Base.BASE_URL;

public class ContestController {

    // Main controller
    private AppController mainController;
    private Timer isUBoatQuitsTimer;
    // Battlefield Contest Data
    @FXML private TableView<ContestRawData> battlefieldContestDataTableView;
    @FXML private TableColumn<ContestRawData, String> titleTableColumn;
    @FXML private TableColumn<ContestRawData, String> usernameTableColumn;
    @FXML private TableColumn<ContestRawData, String> statusTableColumn;
    @FXML private TableColumn<ContestRawData, String> difficultyTableColumn;
    @FXML private TableColumn<ContestRawData, String> teamsRegisteredTableColumn;
    private Timer battlefieldContestDataTimer;
    // Task Size
    @FXML private TextField taskSizeTextField; // Text field because we can not know what is the range
    @FXML private Label errorMessageLabel;
    private final StringProperty errorMessageProperty;
    // Contest Allies
    @FXML private TableView<AlliesData> contestAlliesTableView;
    @FXML private TableColumn<AlliesData, String> alliesUsernameTableColumn;
    @FXML private TableColumn<AlliesData, String> totalAgentsTableColumn;
    @FXML private TableColumn<AlliesData, String> taskSizeTableColumn;
    private String contestUsername;
    private String contestTitle;
    private Timer contestAlliesTimer;
    // Team's Agents Progress Data
    @FXML private TableView<AgentsRawData> progressDataTableView;
    @FXML private TableColumn<AgentsRawData, String> agentNameTableColumn;
    @FXML private TableColumn<AgentsRawData, String> tasksReceivedTableColumn;
    @FXML private TableColumn<AgentsRawData, String> tasksToPerformTableColumn;
    @FXML private TableColumn<AgentsRawData, String> candidatesCreatedTableColumn;
    private Timer teamsAgentsTimer;
    // Decryption Manager Progress
    @FXML private Label tasksAvailableLabel;
    @FXML private Label tasksGeneratedLabel;
    @FXML private Label tasksFinishedLabel;
    // Team's Candidates
    @FXML private TextField encryptedMessageTextField;
    @FXML private TableView<TeamCandidates> teamsCandidatesTableView; //TODO: add TimerTask
    @FXML private TableColumn<TeamCandidates, String> candidateMessageTableColumn;
    @FXML private TableColumn<TeamCandidates, String> candidateUsernameTableColumn;
    @FXML private TableColumn<TeamCandidates, String> candidateConfigurationTableColumn;
    private Timer encryptedMessageTimer;
    // Start contest
    @FXML private Button readyButton;
    private Timer startContestTimer;
    private Difficulty difficulty;
    // Contest mode
    private boolean contestStarted;
    private Timer updateTasksProgressTimer;
    private Timer pullInformationTimer;
    private Timer contestFinishedTimer;

    public ContestController() {
        errorMessageProperty = new SimpleStringProperty("Crack The Enigma - Exercise 3");
    }

    public String getContestUsername() {
        return contestUsername;
    }

    public String getUsername() {
        return mainController.getAlliesUsername();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        // Initialize table views
        initBattlefieldContestDataTableView();
        initContestAlliesTableView();
        initProgressDataTableView();
        initTeamsCandidatesTableView();
    }

    @SuppressWarnings("DuplicatedCode")
    private void initBattlefieldContestDataTableView() {
        titleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        usernameTableColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        statusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        difficultyTableColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        teamsRegisteredTableColumn.setCellValueFactory(new PropertyValueFactory<>("teamsRegistered"));
        contestAlliesTableView.setPlaceholder(new Label("No contest data to display"));
    }

    private void initContestAlliesTableView() {
        alliesUsernameTableColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        totalAgentsTableColumn.setCellValueFactory(new PropertyValueFactory<>("totalAgents"));
        taskSizeTableColumn.setCellValueFactory(new PropertyValueFactory<>("taskSize"));
        contestAlliesTableView.setPlaceholder(new Label("No active teams to display"));

        // Automatically scrolls down
        contestAlliesTableView.getItems().addListener((ListChangeListener<AlliesData>) (c -> {
            c.next();
            final int size = contestAlliesTableView.getItems().size();
            if (size > 0) {
                contestAlliesTableView.scrollTo(size - 1);
            }
        }));
    }

    private void initProgressDataTableView() {
        agentNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("agentName"));
        tasksReceivedTableColumn.setCellValueFactory(new PropertyValueFactory<>("tasksFinished"));
        tasksToPerformTableColumn.setCellValueFactory(new PropertyValueFactory<>("tasksToPerform"));
        candidatesCreatedTableColumn.setCellValueFactory(new PropertyValueFactory<>("candidatesCreated"));
        progressDataTableView.setPlaceholder(new Label("No agents' progress data to display"));
    }

    private void initTeamsCandidatesTableView() {
        candidateMessageTableColumn.setCellValueFactory(new PropertyValueFactory<>("candidateMessage"));
        candidateUsernameTableColumn.setCellValueFactory(new PropertyValueFactory<>("candidateUsername"));
        candidateConfigurationTableColumn.setCellValueFactory(new PropertyValueFactory<>("candidateConfiguration"));
        teamsCandidatesTableView.setPlaceholder(new Label("No candidates to display"));

        // Automatically scrolls down
        teamsCandidatesTableView.getItems().addListener((ListChangeListener<TeamCandidates>) (c -> {
            c.next();
            final int size = teamsCandidatesTableView.getItems().size();
            if (size > 0) {
                teamsCandidatesTableView.scrollTo(size - 1);
            }
        }));
    }

    public void initializeContestScreen(String contestUsername, String contestTitle) {
        contestStarted = false;
        // Initialize timers
        isUBoatQuitsTimer = new Timer(true);
        battlefieldContestDataTimer = new Timer(true);
        contestAlliesTimer = new Timer(true);
        teamsAgentsTimer = new Timer(true);
        encryptedMessageTimer = new Timer(true);
        startContestTimer = new Timer(true);
        updateTasksProgressTimer = new Timer(true);
        pullInformationTimer = new Timer(true);
        contestFinishedTimer = new Timer(true);

        this.contestUsername = contestUsername;
        this.contestTitle = contestTitle;
        addAlliesToContest();

        IsUBoatQuitsTimerTask isUBoatQuitsTimerTask = new IsUBoatQuitsTimerTask(this);
        isUBoatQuitsTimer.scheduleAtFixedRate(isUBoatQuitsTimerTask, 0, 500);
        BattlefieldContestDataTimerTask battlefieldContestDataTimerTask = new BattlefieldContestDataTimerTask(this); // Extends TimerTask
        battlefieldContestDataTimer.scheduleAtFixedRate(battlefieldContestDataTimerTask, 0, 500);
        ContestAlliesTimerTask contestAlliesTimerTask = new ContestAlliesTimerTask(this); // Extends TimerTask
        contestAlliesTimer.scheduleAtFixedRate(contestAlliesTimerTask, 0, 500);
        TeamsAgentsTimerTask teamsAgentsTimerTask = new TeamsAgentsTimerTask(this); // Extends TimerTask
        teamsAgentsTimer.scheduleAtFixedRate(teamsAgentsTimerTask, 0, 500);
        EncryptedMessageTimerTask encryptedMessageTimerTask = new EncryptedMessageTimerTask(this); // Extends TimerTask
        encryptedMessageTimer.scheduleAtFixedRate(encryptedMessageTimerTask, 0, 500);
        StartContestTimerTask startContestTimerTask = new StartContestTimerTask(this); // Extends TimerTask
        startContestTimer.scheduleAtFixedRate(startContestTimerTask, 0, 500);
    }

    @SuppressWarnings("unused")
    private void addAlliesToContest() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/agent/get-my-allies";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("team", getUsername());
        urlBuilder.addQueryParameter("title", contestTitle);
        String finalUrl = urlBuilder.build().toString();

        // Request + body + response
        try {
            RequestBody body = new FormBody.Builder() // Create request body
                    .build();
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("PUT", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        System.out.println("Added Allies information to server's contest");
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void readyAction() {
        if (taskSizeTextField.getText() == null || taskSizeTextField.getText().isEmpty()) {
            errorMessageProperty.set("ERROR: You must choose task size!");
            return;
        } else {
            try {
                if (Integer.parseInt(taskSizeTextField.getText()) <= 0) {
                    errorMessageProperty.set("ERROR: Your task size must be at least 1!");
                    return;
                }
            } catch (InputMismatchException e) {
                errorMessageProperty.set("ERROR: Your task size must be of integers!");
                return;
            }
        }
        alliesAgentsAmountIsPositive();
        // TODO: create tasks in DM, then add tasks to server -> so agents would consume them.
    }

    private void alliesAgentsAmountIsPositive() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/allies/get-agents-amount";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("team", getUsername());
        String finalUrl = urlBuilder.build().toString();

        // Request + body + response
        try {
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("GET", null)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        if (Integer.parseInt(Objects.requireNonNull(body).string()) > 0) {
                            Platform.runLater(() -> {
                                updateAlliesTaskSize();
                                readyButton.setMouseTransparent(true);
                            });
                        } else {
                            Platform.runLater(() -> errorMessageProperty.set("ERROR: You must have agents!"));
                        }
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unused", "SpellCheckingInspection"})
    private void updateAlliesTaskSize() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/uboat/get-allies";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("username", getContestUsername());
        urlBuilder.addQueryParameter("team", getUsername());
        urlBuilder.addQueryParameter("size", taskSizeTextField.getText());
        String finalUrl = urlBuilder.build().toString();

        // Request + body + response
        try {
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create("", mediaType);

            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("PUT", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        System.out.println("Notifying UBoat");
                        Platform.runLater(() -> updateServerYouAreReady());
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unused", "SpellCheckingInspection"})
    private void updateServerYouAreReady() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/uboat/contest";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("username", getContestUsername());
        urlBuilder.addQueryParameter("team", getUsername());
        String finalUrl = urlBuilder.build().toString();

        // Request + body + response
        try {
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create("", mediaType);

            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("PUT", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Failed to send 'Ready' message to server about Allies - " + getUsername() + ".");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        System.out.println("Server notified, " + getUsername() + " Allies is ready!");
                        Platform.runLater(() -> errorMessageProperty.set("Successfully notified UBoat client!"));
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    public void updateContestAlliesTableView(Collection<AlliesData> allCurrent) {
        if (allCurrent != null) {
            contestAlliesTableView.getItems().clear();
            contestAlliesTableView.getItems().addAll(allCurrent);
        }
    }

    public void updateBattlefieldContestDataTableView(Battlefield battlefield) {
        if (battlefield != null) {
            battlefieldContestDataTableView.getItems().clear();
            battlefieldContestDataTableView.getItems().add(0, new ContestRawData(battlefield));
            difficulty = battlefield.getDifficulty();
        }
    }

    public void updateEncryptionMessage(String encryptionMessage) {
        encryptedMessageTextField.setText(encryptionMessage);
    }

    public void updateTeamsAgentsTableView(List<AgentsRawData> agentsRawData) {
        progressDataTableView.getItems().clear();
        progressDataTableView.getItems().addAll(agentsRawData);
    }

    public void startContestMode() {
        try {
            contestAlliesTimer.cancel();
            Thread.sleep(100);
            encryptedMessageTimer.cancel();
            Thread.sleep(100);
            battlefieldContestDataTimer.cancel();
            Thread.sleep(100);
            startContestTimer.cancel();
            Thread.sleep(100);
            teamsAgentsTimer.cancel();
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        contestStarted = true;
        System.out.println("Start " + getContestUsername() + "'s contest mode for " + getUsername() + "...");

        initializeServerBlockingQueue();
    }

    @SuppressWarnings("unused")
    private void initializeServerBlockingQueue() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/allies/blocking-queue";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("username", getContestUsername());
        urlBuilder.addQueryParameter("team", getUsername());
        urlBuilder.addQueryParameter("taskSize", taskSizeTextField.getText());
        urlBuilder.addQueryParameter("difficulty", difficulty.toString());
        String finalUrl = urlBuilder.build().toString();

        // Request + body + response
        try {
            RequestBody body = new FormBody.Builder() // Create request body
                    .build();
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("PUT", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Failed to initialize " + getUsername() + "'s blocking queue in contest.");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        System.out.println("Successfully created " + getUsername() + "'s for " + getContestUsername() + "'s contest blocking queue");
                        // Initialize server's blocking queue TimerTask
                        Platform.runLater(() -> initializeTimerTasks());
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void initializeTimerTasks() {
        initializeUpdateTasksProgressTimerTask();
        initializePullInformationTimerTask();
        initializeContestFinishedTimerTask();
    }

    private void initializeUpdateTasksProgressTimerTask() {
        TasksInformationTimerTask tasksInformationTimerTask = new TasksInformationTimerTask(this);
        updateTasksProgressTimer.scheduleAtFixedRate(tasksInformationTimerTask, 0, 500);
    }

    private void initializePullInformationTimerTask() {
        PullInformationTimerTask pullInformationTimerTask = new PullInformationTimerTask(this);
        pullInformationTimer.scheduleAtFixedRate(pullInformationTimerTask, 0, 500);
    }

    private void initializeContestFinishedTimerTask() {
        ContestFinishedTimerTask contestFinishedTimerTask = new ContestFinishedTimerTask(this);
        contestFinishedTimer.scheduleAtFixedRate(contestFinishedTimerTask, 0, 500);
    }

    public void updateContestTableViews(Pair<LinkedList<TeamCandidates>, List<AgentsRawData>> result) {
        // Add all information of all Agents
        progressDataTableView.getItems().clear();
        progressDataTableView.getItems().addAll(result.getValue());
        // Add only new candidates
        teamsCandidatesTableView.getItems().addAll(result.getKey());
    }

    public void updateValues(long availableTasks, long tasksGenerated, long finishedTasks) {
        tasksAvailableLabel.setText(String.valueOf(availableTasks));
        tasksGeneratedLabel.setText(String.valueOf(tasksGenerated));
        tasksFinishedLabel.setText(String.valueOf(finishedTasks));
    }

    public void stopContest(String winningAllies) {
        // Timers
        updateTasksProgressTimer.cancel();
        pullInformationTimer.cancel();
        contestFinishedTimer.cancel();
        new Alert(Alert.AlertType.INFORMATION, "Contest has finished.\n" +
                "The winner is " + winningAllies).showAndWait();
        removeAlliesFromContest("no"); // Contest ended, Allies did not quit
    }

    private void resetScreen() {
        // Battlefield Contest Data
        battlefieldContestDataTableView.getItems().clear();
        // Enter Task Size
        taskSizeTextField.setText("");
        readyButton.setMouseTransparent(false);
        errorMessageProperty.set("Crack The Enigma - Exercise 3");
        // Contest Allies
        contestAlliesTableView.getItems().clear();
        // Team's Agents Progress Data & DM Progress
        progressDataTableView.getItems().clear();
        tasksAvailableLabel.setText("0");
        tasksGeneratedLabel.setText("0");
        tasksFinishedLabel.setText("0");
        // Team's Candidates
        encryptedMessageTextField.setText("");
        teamsCandidatesTableView.getItems().clear();
    }

    @FXML
    void logoutAction() {
        resetContestMethods();
        removeAlliesFromContest("yes"); // Allies quit
    }

    private void resetContestMethods() {
        isUBoatQuitsTimer.cancel();
        if (contestStarted) { // During contest
            updateTasksProgressTimer.cancel();
            pullInformationTimer.cancel();
            contestFinishedTimer.cancel();
        } else { // Not during contest
            contestAlliesTimer.cancel();
            encryptedMessageTimer.cancel();
            battlefieldContestDataTimer.cancel();
            startContestTimer.cancel();
            teamsAgentsTimer.cancel();
        }
    }

    private void removeAlliesFromContest(String isLogout) {
        // Get winning Allies
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/allies/remove";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("username", getContestUsername());
        urlBuilder.addQueryParameter("team", getUsername());
        urlBuilder.addQueryParameter("logout", isLogout);
        String finalUrl = urlBuilder.build().toString();

        // Request + body + response
        try {
            RequestBody body = new FormBody.Builder() // Create request body
                    .build();

            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("POST", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("ERROR: Failing to remove " + getUsername() + " from finished contest!");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        Platform.runLater(() -> resetScreen());
                        String winningAllies = Objects.requireNonNull(body).string();
                        if (!winningAllies.trim().equals("logout")) {
                            System.out.println("Moving " + getUsername() + " to dashboard screen.");
                            Platform.runLater(mainController::switchToDashboardScreen);
                        } else {
                            System.out.println("Moving " + getUsername() + " to login screen.");
                            Platform.runLater(mainController::switchToLoginScreen);
                        }
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /** Called only if UBoat client quit **/
    public void contestStopped() {
        new Alert(Alert.AlertType.INFORMATION, getContestUsername() + " has decided to quit.\n" +
                "You are being transferred to dashboard screen again.").showAndWait();
        resetContestMethods();
        removeAlliesFromContest("no"); // UBoat quits, Allies did not quit
    }
}
