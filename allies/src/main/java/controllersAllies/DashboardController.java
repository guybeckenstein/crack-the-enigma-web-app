package controllersAllies;

import com.google.gson.Gson;
import jar.clients.agent.Agent;
import jar.clients.battlefield.Battlefield;
import jar.common.rawData.agents.TeamsAgentsData;
import jar.common.rawData.battlefieldContest.AlliesData;
import jar.common.rawData.battlefieldContest.ContestRawData;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import timerTasks.dashboard.ContestsTimerTask;
import timerTasks.dashboard.MyAgentsTimerTask;

import java.io.IOException;
import java.util.*;

import static http.Base.BASE_URL;

public class DashboardController {

    // Main controller
    private AppController mainController;
    // Team's Agents Data
    @FXML private TableView<TeamsAgentsData> teamsAgentsDataTableView;

    @FXML private TableColumn<TeamsAgentsData, String> nameTableColumn;

    @FXML private TableColumn<TeamsAgentsData, String> totalThreadsTableColumn;

    @FXML private TableColumn<TeamsAgentsData, String> tasksCapacityTableColumn;
    private Timer myAgentsTimer;
    // Add agent
    @FXML private TextField usernameTextField;
    @FXML private ComboBox<Integer> totalThreadsComboBox;
    @FXML private Slider tasksWithdrawalSizeSlider;
    // Error
    @FXML private Label errorMessageLabel;
    private final SimpleStringProperty errorMessageProperty;
    // Battlefield Contest Data
    @FXML private TableView<ContestRawData> battlefieldContestDataTableView;

    @FXML private TableColumn<ContestRawData, String> titleTableColumn;

    @FXML private TableColumn<ContestRawData, String> usernameTableColumn;

    @FXML private TableColumn<ContestRawData, String> statusTableColumn;

    @FXML private TableColumn<ContestRawData, String> difficultyTableColumn;

    @FXML private TableColumn<ContestRawData, String> teamsRegisteredTableColumn;
    private Timer contestsTimer;
    // Enter Contest
    private ContestRawData contest;

    public DashboardController() {
        // Show all contests
        errorMessageProperty = new SimpleStringProperty("Crack The Enigma - Exercise 3");
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        initAgentsTableView();
        initBattlefieldTableView();
    }

    private void initAgentsTableView() {
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalThreadsTableColumn.setCellValueFactory(new PropertyValueFactory<>("totalThreads"));
        tasksCapacityTableColumn.setCellValueFactory(new PropertyValueFactory<>("tasksCapacity"));
        teamsAgentsDataTableView.setPlaceholder(new Label("No agents to display"));
    }

    @SuppressWarnings("DuplicatedCode")
    private void initBattlefieldTableView() {
        titleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        usernameTableColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        statusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        difficultyTableColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        teamsRegisteredTableColumn.setCellValueFactory(new PropertyValueFactory<>("teamsRegistered"));
        teamsAgentsDataTableView.setPlaceholder(new Label("No contests to display"));
    }

    public void initializeDashboardScreen() {
        myAgentsTimer = new Timer(true);
        MyAgentsTimerTask myAgentsTimerTask = new MyAgentsTimerTask(this); // Extends TimerTask
        myAgentsTimer.scheduleAtFixedRate(myAgentsTimerTask, 0, 500);
        contestsTimer = new Timer(true);
        ContestsTimerTask contestsTimerTask = new ContestsTimerTask(this);
        contestsTimer.scheduleAtFixedRate(contestsTimerTask, 0, 500);
    }

    public String getUsername() {
        return mainController.getAlliesUsername();
    }

    @FXML
    void enterContestAction() {
        boolean validBattlefield;
        synchronized (this) {
            contest = battlefieldContestDataTableView.getSelectionModel().getSelectedItem();
            // Get teams registered
            validBattlefield = validateEnterContest(contest);
        }
        if (validBattlefield) {
            updateContestNumParticipants(contest.getUsername(), getUsername());
        }
    }

    private void switchToContestScreen() {
        System.out.println(getUsername() + " has successfully been added to " + contest.getTitle() + " battlefield!");
        // Cancel timers
        try {
            myAgentsTimer.cancel();
            Thread.sleep(100);
            contestsTimer.cancel();
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Switch to contest screen
        mainController.switchToContestScreen(contest.getUsername(), contest.getTitle());
    }

    private void updateContestNumParticipants(String uBoatUsername, String alliesUsername) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/allies/contest-registration";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("uBoatUsername", uBoatUsername); // UBoat's username
        urlBuilder.addQueryParameter("alliesUsername", alliesUsername); // Allies' username
        urlBuilder.addQueryParameter("data", new Gson().toJson(new AlliesData(alliesUsername, 0, 1))); // Allies' data
        String finalUrl = urlBuilder.build().toString();

        try {
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("PUT", RequestBody.create("", MediaType.parse("text/plain")))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> errorMessageLabel.setText("ERROR: " + e.getLocalizedMessage() + "!"));
                }

                @SuppressWarnings("unused")
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        Platform.runLater(() -> switchToContestScreen());
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getLocalizedMessage() + "!");
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private boolean validateEnterContest(ContestRawData contest) {
        if (contest != null) {
            if (contest.getStatus().equals(Battlefield.Status.Available.toString())) {
                if (!contest.getTeamsRegistered().split("/")[0].equals(contest.getTeamsRegistered().split("/")[1])) {
                    return true;
                } else {
                    errorMessageProperty.set("ERROR: This competition is currently full!");
                }
            } else {
                errorMessageProperty.set("ERROR: This competition has already started!");
            }
        } else {
            errorMessageProperty.set("ERROR: You did not choose a competition!");
        }
        return false;
    }

    public void updateAgentsTableView(List<Agent> myAgents) {
        if (myAgents != null) {
            List<TeamsAgentsData> teamsAgentsData = new ArrayList<>();
            for (Agent agent : myAgents) {
                teamsAgentsData.add(new TeamsAgentsData(agent.getUsername(), agent.getTotalThreads(), agent.getTasksCapacity()));
            }

            teamsAgentsDataTableView.getItems().clear();
            teamsAgentsDataTableView.getItems().addAll(teamsAgentsData);
        }
    }

    public void updateBattlefieldContestDataTableView(Collection<Battlefield> allContests) {
        int selectedItem = battlefieldContestDataTableView.getSelectionModel().getSelectedIndex();
        List<ContestRawData> newBattlefields = new ArrayList<>();

        for (Battlefield battlefield : allContests) {
            ContestRawData newContest = new ContestRawData(battlefield.getTitle(), battlefield.getUsername(),
                    battlefield.getStatus().toString(), battlefield.getDifficulty().toString(), battlefield.getTeamsRegistered());
            newBattlefields.add(newContest);
        }
        battlefieldContestDataTableView.getItems().clear();
        battlefieldContestDataTableView.getItems().addAll(newBattlefields);
        battlefieldContestDataTableView.getSelectionModel().select(selectedItem);
    }

    @FXML
    void addAgentAction() {
        if (usernameTextField.getText().isEmpty()) {
            errorMessageProperty.set("ERROR: No username has been inserted!");
        } else if (totalThreadsComboBox.getValue() == null) {
            errorMessageProperty.set("ERROR: You must choose amount of available threads!");
        } else {
            ProcessBuilder processBuilder = new ProcessBuilder();

        }
    }
}