package controllersAgent;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

public class AppController {
    // Main component

    // Sub components
    @FXML private ScrollPane loginComponent;
    @FXML private ScrollPane contestComponent;
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private HeaderController headerComponentController;
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private LoginController loginComponentController;
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private ContestController contestComponentController;
    private String agentUsername;

    public AppController() {
    }

    @FXML
    public void initialize() {
        if (headerComponentController != null && loginComponentController != null && contestComponentController != null) {
            loginComponentController.setMainController(this);
            contestComponentController.setMainController(this);
        }
    }

    public void updateAgentUsername(String username) {
        headerComponentController.updateUsername(username);
        agentUsername = username;
    }

    public void switchToContestScreen(String alliesUsername, int totalThreads, int tasksWithdrawalSize) {
        contestComponent.toFront();
        headerComponentController.switchToContestScreen();
        contestComponentController.resetTimers(agentUsername, alliesUsername, totalThreads, tasksWithdrawalSize);
    }

    public void switchToLoginScreen() {
        // Reset header
        headerComponentController.switchToLoginScreen();
        // Reset login screen
        loginComponent.toFront();
        loginComponentController.reset();
    }
}
