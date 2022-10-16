package controllersAgent;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ContestController {
    // Main controller
    private AppController mainController;
    // Contest & Team Data
    @FXML private TableView<?> battlefieldContestDataTableView; // TODO
    @FXML private TableColumn<?, String> alliesUsernameTableColumn;
    @FXML private TableColumn<?, String> contestTitleTableColumn;
    @FXML private TableColumn<?, String> tasksToPerformTableColumn;
    // Current Task
    @FXML private TextField taskStartingConfigurationTextField;
    // Agent Progress & Status
    @FXML private Label tasksWithdrewLabel;
    @FXML private Label tasksFinishedLabel;
    @FXML private Label totalCandidatesLabel;
    @FXML private Label statusLabel;
    // Agent's Candidates
    @FXML private TextField encryptedMessageTextField;
    @FXML private TableView<?> agentCandidatesTableView; // TODO
    @FXML private TableColumn<?, String> messageTableColumn;
    @FXML private TableColumn<?, String> taskTableColumn;
    @FXML private TableColumn<?, String> configurationTableColumn;

    public ContestController() {
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        // TODO: methods for tables
    }

    // TODO: update tables, update current task, update progress & status
}

