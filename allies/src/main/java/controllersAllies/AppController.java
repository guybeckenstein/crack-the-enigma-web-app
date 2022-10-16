package controllersAllies;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

public class AppController {
    // Main component

    // Sub components
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private HeaderController headerComponentController;
    @FXML private ScrollPane dashboardComponent;
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private LoginController loginComponentController;
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private DashboardController dashboardComponentController;
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private ContestController contestComponentController;

    public AppController() {
    }

    @FXML
    public void initialize() {
        if (headerComponentController != null && loginComponentController != null
                && dashboardComponentController != null && contestComponentController != null) {
            loginComponentController.setMainController(this);
            dashboardComponentController.setMainController(this);
            contestComponentController.setMainController(this);
        }
    }

    public void updateAlliesUsername(String username) {
        headerComponentController.updateUsername(username);
    }

    public void switchToDashboardScreen() {
        dashboardComponent.toFront();
        headerComponentController.switchToDashboardScreen();
        // TODO: add allies to server, make it possible to Agents, etc...
    }
}
