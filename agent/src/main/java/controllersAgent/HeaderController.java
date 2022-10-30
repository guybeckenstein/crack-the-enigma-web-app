package controllersAgent;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public class HeaderController {
    // Screens buttons
    @FXML private Button contestButton;
    // Style
    @FXML private ChoiceBox<String> styleChoiceBox;
    // Username
    @FXML private Label usernameHeaderLabel;
    private String username;

    public HeaderController() {
        username = "Guest";
    }

    @FXML
    private void initialize() {
        styleChoiceBox.getItems().setAll("Style #1");
        styleChoiceBox.setValue("Style #1");

        usernameHeaderLabel.setText("Hello " + username + " - Agent User");
    }
    public void switchToContestScreen() {
        contestButton.getStyleClass().add("chosen-button");
    }
    public void updateUsername(String username) {
        this.username = username;
        usernameHeaderLabel.setText("Hello " + username + " - Agent User");
    }

    public void switchToLoginScreen() {
        // Reset header details
        username = "Guest";
        usernameHeaderLabel.setText("Hello " + username + " - Allies User");
        // Reset header style
        contestButton.getStyleClass().remove("chosen-button");
    }
}
