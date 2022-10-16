package controllersAllies;

import common.rawData.agents.TeamsAgentsData;
import common.rawData.battlefieldContest.ContestRawData;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DashboardController {

    // Main controller
    private AppController mainController;
    // Team's Agents Data
    @FXML private TableView<TeamsAgentsData> teamsAgentsDataTableView; // TODO

    @FXML private TableColumn<TeamsAgentsData, String> nameTableColumn;

    @FXML private TableColumn<TeamsAgentsData, String> totalThreadsTableColumn;

    @FXML private TableColumn<TeamsAgentsData, String> tasksCapacityTableColumn;
    // Battlefield Contest Data
    @FXML private TableView<ContestRawData> battlefieldContestDataTableView; // TODO

    @FXML private TableColumn<ContestRawData, String> titleTableColumn;

    @FXML private TableColumn<ContestRawData, String> usernameTableColumn;

    @FXML private TableColumn<ContestRawData, String> statusTableColumn;

    @FXML private TableColumn<ContestRawData, String> difficultyTableColumn;

    @FXML private TableColumn<ContestRawData, String> teamsRegisteredTableColumn;
    // Enter Contest
    @FXML private Button enterContestButton;
    @FXML private Label errorMessageLabel;
    private final SimpleStringProperty errorMessageProperty;

    public DashboardController() {
        errorMessageProperty = new SimpleStringProperty("Crack The Enigma - Exercise 3");
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);

        // TODO: methods for tables
        // TODO: bind tables to server-data, so they would update each 0.5 second
    }

    @FXML
    void enterContestAction() { // May be synchronized
        boolean validBattlefield = false;
        ContestRawData contest;
        synchronized (this) {
            contest = battlefieldContestDataTableView.getSelectionModel().getSelectedItem();
            // Get teams registered
            String teamsRegisteredParts = contest.getStatus();
            if (teamsRegisteredParts.equals("Available")) {
                validBattlefield = true;
            } else {
                errorMessageProperty.set("ERROR: This competition cannot be entered!");
            }
        }
        if (validBattlefield) {
            // TODO: inform the server that you are ready
            if (true) { // If server updated successfully
                errorMessageProperty.set("You have successfully been added to " + contest.getTitle() + " battlefield!");
            }
        }
    }

    // TODO: update relevant details of this screen

}
