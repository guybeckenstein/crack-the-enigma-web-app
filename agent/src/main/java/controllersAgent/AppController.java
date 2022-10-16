package controllersAgent;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

public class AppController {
    // Main component

    // Sub components
    @FXML private ScrollPane contestComponent;
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private HeaderController headerComponentController;
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private LoginController loginComponentController;
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private ContestController contestComponentController;

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
    }

    public void switchToContestScreen() {
        contestComponent.toFront();
        headerComponentController.switchToContestScreen();
        // TODO: add agent to Allies, make thread pool relevant, take tasks, etc...
    }
}
