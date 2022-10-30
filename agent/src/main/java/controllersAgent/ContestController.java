package controllersAgent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import decryptionManager.DecryptionManagerTask;
import jar.common.rawData.agents.AlliesRawData;
import jar.common.rawData.battlefieldContest.AgentCandidates;
import jar.enigmaEngine.impl.EnigmaEngineImpl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import timerTasks.contest.pre.BattlefieldContestDataTimerTask;
import timerTasks.contest.start.ContestFinishedTimerTask;
import timerTasks.contest.pre.GetEncryptedMessageTimerTask;
import timerTasks.contest.pre.StartContestTimerTask;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Queue;
import java.util.Timer;

import static http.Base.BASE_URL;

public class ContestController {
    // Main controller
    private AppController mainController; // TODO: implement Agent logout
    private String agentUsername;
    private String alliesUsername;
    private int totalThreads;
    private int tasksWithdrawalSize;
    // Logout
    @FXML private Label errorLabel;
    // Contest & Team Data
    @FXML private TableView<AlliesRawData> battlefieldContestDataTableView;
    @FXML private TableColumn<AlliesRawData, String> alliesUsernameTableColumn;
    @FXML private TableColumn<AlliesRawData, String> contestTitleTableColumn;
    @FXML private TableColumn<AlliesRawData, String> tasksToPerformTableColumn;
    private Timer battlefieldContestDataTimer;
    // Current Task
    @FXML private TextField taskStartingConfigurationTextField;
    // Agent Progress & Status
    @FXML private Label tasksWithdrawLabel;
    @FXML private Label tasksFinishedLabel;
    @FXML private Label totalCandidatesLabel;
    @FXML private Label statusLabel;
    // Current Encrypted Message
    @FXML private TextField encryptedMessageTextField;
    // Agent's Candidates
    @FXML private TableView<AgentCandidates> agentCandidatesTableView;
    @FXML private TableColumn<AgentCandidates, String> messageTableColumn;
    @FXML private TableColumn<AgentCandidates, String> taskTableColumn;
    @FXML private TableColumn<AgentCandidates, String> configurationTableColumn;
    // Has contest started?
    private Timer startContestTimer;
    private Timer encryptedMessageTimer;
    // Contest mode
    private DecryptionManagerTask dmTask;
    private Timer contestFinishedTimer;

    public ContestController() {
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public String getAlliesUsername() {
        return alliesUsername;
    }

    public long getCandidatesCreatedSize() {
        return agentCandidatesTableView.getItems().size();
    }

    public DecryptionManagerTask getDmTask() {
        return dmTask;
    }

    @FXML
    private void initialize() {
        initBattlefieldContestDataTableView();
        initAgentCandidatesTableView();

        errorLabel.setText("");
    }

    private void initBattlefieldContestDataTableView() {
        alliesUsernameTableColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        contestTitleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        tasksToPerformTableColumn.setCellValueFactory(new PropertyValueFactory<>("tasks"));
        battlefieldContestDataTableView.setPlaceholder(new Label("No Allies' team to display"));
    }

    private void initAgentCandidatesTableView() {
        messageTableColumn.setCellValueFactory(new PropertyValueFactory<>("candidateMessage"));
        taskTableColumn.setCellValueFactory(new PropertyValueFactory<>("candidateTask"));
        configurationTableColumn.setCellValueFactory(new PropertyValueFactory<>("candidateConfiguration"));
        agentCandidatesTableView.setPlaceholder(new Label("No candidates to display"));
    }

    public void resetTimers(String agentUsername, String alliesUsername, int totalThreads, int tasksWithdrawalSize) {
        this.agentUsername = agentUsername;
        this.alliesUsername = alliesUsername;
        this.totalThreads = totalThreads;
        this.tasksWithdrawalSize = tasksWithdrawalSize;
        // Update TableView details
        AlliesRawData initializedData = new AlliesRawData(getAlliesUsername(), "Idle", 0);
        battlefieldContestDataTableView.getItems().add(initializedData);
        // Initialize timers
        resetTimers();
    }

    private void resetTimers() {
        startContestTimer = new Timer(true);
        battlefieldContestDataTimer = new Timer(true);
        encryptedMessageTimer = new Timer(true);

        StartContestTimerTask startContestTimerTask = new StartContestTimerTask(this);
        startContestTimer.scheduleAtFixedRate(startContestTimerTask, 0, 500);
        BattlefieldContestDataTimerTask battlefieldContestDataTimerTask = new BattlefieldContestDataTimerTask(this);
        battlefieldContestDataTimer.scheduleAtFixedRate(battlefieldContestDataTimerTask, 0, 500);
        GetEncryptedMessageTimerTask getEncryptedMessageTimerTask = new GetEncryptedMessageTimerTask(this);
        encryptedMessageTimer.scheduleAtFixedRate(getEncryptedMessageTimerTask, 0, 500);
    }

    public void updateBattlefieldContestDataTableView(AlliesRawData alliesTeamData) {
        if (alliesTeamData!= null) {
            battlefieldContestDataTableView.getItems().remove(0);
            battlefieldContestDataTableView.getItems().addAll(alliesTeamData);
        }
    }

    public void updateEncryptedMessageTextField(String encryptionMessage) {
        encryptedMessageTextField.setText(encryptionMessage);
    }

    public void startContestMode() {
        try {
            startContestTimer.cancel();
            Thread.sleep(100);
            encryptedMessageTimer.cancel();
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        statusLabel.setText("Active");
        getAlliesTaskSizeAndEnigmaEngineAndEncryptionInput();
    }

    private void getAlliesTaskSizeAndEnigmaEngineAndEncryptionInput() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/agent/contest-information";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("team", alliesUsername);
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
                    System.out.println("ERROR: Failing to get task size and Enigma engine from server!");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        String output = Objects.requireNonNull(body).string();
                        Type type = new TypeToken<Pair<Pair<Integer, EnigmaEngineImpl>, String>>(){}.getType();
                        Pair<Pair<Integer, EnigmaEngineImpl>, String> taskSizeAndEnigmaEngine = new Gson().fromJson(output, type);
                        if (taskSizeAndEnigmaEngine != null && taskSizeAndEnigmaEngine.getKey() != null && taskSizeAndEnigmaEngine.getKey().getKey() != null) {
                            int taskSize = taskSizeAndEnigmaEngine.getKey().getKey();
                            EnigmaEngineImpl enigmaEngine = taskSizeAndEnigmaEngine.getKey().getValue();
                            String encryptionInput = taskSizeAndEnigmaEngine.getValue();
                            Platform.runLater(() -> initializeContest(taskSize, enigmaEngine, encryptionInput));
                        }
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void initializeContest(int taskSize, EnigmaEngineImpl enigmaEngine, String encryptionInput) {
        dmTask = new DecryptionManagerTask(this, encryptedMessageTextField.getText(),
                totalThreads, taskSize, tasksWithdrawalSize, enigmaEngine, agentUsername, alliesUsername, encryptionInput);
        new Thread(dmTask, agentUsername).start();

        contestFinishedTimer = new Timer(true);
        ContestFinishedTimerTask contestFinishedTimerTask = new ContestFinishedTimerTask(this);
        contestFinishedTimer.scheduleAtFixedRate(contestFinishedTimerTask, 0, 500);
    }

    public void updateTaskStartingConfigurationTextField(String configuration) {
        taskStartingConfigurationTextField.setText(configuration);
    }
    public void updateCandidatesTableView(Queue<AgentCandidates> candidates) {
        // Update AgentCandidatesTableView
        agentCandidatesTableView.getItems().addAll(candidates);
    }
    public void updateProgress(int tasksWithdraw, long iterations) {
        // Update labels
        tasksWithdrawLabel.setText(String.valueOf(tasksWithdraw));
        tasksFinishedLabel.setText(String.valueOf(iterations));
        totalCandidatesLabel.setText(String.valueOf(agentCandidatesTableView.getItems().size()));
    }

    public void contestFinished(String timeElapsed) {
        // Get winning Allies
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/agent/winning-team";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("team", alliesUsername);
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
                    System.out.println("ERROR: Failing to get task size and Enigma engine from server!");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        String winningAllies = Objects.requireNonNull(body).string().trim();
                        AlliesRawData initializedData = new AlliesRawData(getAlliesUsername(), "Idle", 0);
                        Platform.runLater(() -> {
                            battlefieldContestDataTimer.cancel();
                            contestFinishedTimer.cancel();
                            // Show message to Allies client
                            new Alert(Alert.AlertType.INFORMATION, "Contest has finished after" + timeElapsed + " seconds.\n" +
                                    "The winner is " + winningAllies).showAndWait();
                            reset(initializedData);
                        });
                    } catch (IllegalStateException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void reset(AlliesRawData initializedData) {
        // Reset timers
        resetTimers();
        // Contest & Team Data
        battlefieldContestDataTableView.getItems().clear();
        battlefieldContestDataTableView.getItems().add(initializedData);
        // Current Task
        taskStartingConfigurationTextField.setText("");
        // Agent Progress & Status
        tasksWithdrawLabel.setText("0");
        tasksFinishedLabel.setText("0");
        totalCandidatesLabel.setText("0");
        statusLabel.setText("Not Active");
        // Agent's Candidates
        encryptedMessageTextField.setText("");
        agentCandidatesTableView.getItems().clear();
    }

    @FXML
    void logoutAction() {
        if (statusLabel.getText().equals("Active")) {
            errorLabel.setText("ERROR: can't logout during contest!");
        } else {
            removeAgentFromServer();
        }
    }

    private void removeAgentFromServer() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/agent/remove";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("team", alliesUsername);
        urlBuilder.addQueryParameter("agent", agentUsername);
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
                    System.out.println("ERROR: Failing to logout agent " + agentUsername + " from " + alliesUsername + "'s Allies team!");
                    System.out.println(e.getLocalizedMessage());
                }

                @SuppressWarnings("unused")
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        System.out.println("Successfully logout agent " + agentUsername + " from " + alliesUsername + "'s Allies team!");
                        Platform.runLater(() -> {
                            battlefieldContestDataTimer.cancel();
                            startContestTimer.cancel();
                            encryptedMessageTimer.cancel();
                            errorLabel.setText("");
                            mainController.switchToLoginScreen();
                        });
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}

