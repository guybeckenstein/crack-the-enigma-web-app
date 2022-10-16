package controllersAllies;

import common.rawData.agents.AgentsRawData;
import common.rawData.battlefieldContest.AlliesData;
import common.rawData.battlefieldContest.ContestRawData;
import common.rawData.battlefieldContest.TeamCandidates;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.InputMismatchException;

public class ContestController {

    // Main controller
    private AppController mainController;
    // Battlefield Contest Data
    @FXML private TableView<ContestRawData> battlefieldContestDataTableView; // TODO
    @FXML private TableColumn<ContestRawData, String> titleTableColumn;
    @FXML private TableColumn<ContestRawData, String> usernameTableColumn;
    @FXML private TableColumn<ContestRawData, String> statusTableColumn;
    @FXML private TableColumn<ContestRawData, String> difficultyTableColumn;
    @FXML private TableColumn<ContestRawData, String> teamsRegisteredTableColumn;
    // Task Size
    @FXML private TextField taskSizeTextField; // Text field because we can not know what is the range
    @FXML private Label errorMessageLabel;
    private final StringProperty errorMessageProperty;
    // Ready
    @FXML private Button readyButton;
    // Contest Allies
    @FXML private TableView<AlliesData> contestAlliesTableView; // TODO
    @FXML private TableColumn<AlliesData, String> alliesUsernameTableColumn;
    @FXML private TableColumn<AlliesData, String> totalAgentsTableColumn;
    @FXML private TableColumn<AlliesData, String> taskSizeTableColumn;
    // Team's Agents Progress Data
    @FXML private TableView<AgentsRawData> progressDataTableView; // TODO
    @FXML private TableColumn<AgentsRawData, String> agentNameTableColumn;
    @FXML private TableColumn<AgentsRawData, String> tasksReceivedTableColumn;
    @FXML private TableColumn<AgentsRawData, String> tasksToPerformTableColumn;
    @FXML private TableColumn<AgentsRawData, String> candidatesCreatedTableColumn;
    // Decryption Manager Progress
    @FXML private Label tasksAvailableLabel;
    @FXML private Label tasksGeneratedLabel;
    @FXML private Label tasksFinishedLabel;
    // Team's Candidates
    @FXML private TextField encryptedMessageTextField; // TODO: get from UBoat (by server)
    @FXML private TableView<TeamCandidates> teamsCandidatesTableView; // TODO
    @FXML private TableColumn<TeamCandidates, String> candidateMessageTableColumn;
    @FXML private TableColumn<TeamCandidates, String> candidateUsernameTableColumn;
    @FXML private TableColumn<TeamCandidates, String> candidateConfigurationTableColumn;

    public ContestController() {
        errorMessageProperty = new SimpleStringProperty("Crack The Enigma - Exercise 3");
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);

        // TODO: methods for tables
        // TODO: bind tables to server-data, so they would update each 0.5 second (update new components style)
        // TODO: for Team's Agents Progress Data & DM Progress, style is update all, all the time
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
        // TODO: create tasks in DM, then add tasks to server -> so agents would consume them.
    }

}
