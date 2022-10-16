package controllersAgent;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import static web.http.Configuration.BASE_URL;
import static web.http.Configuration.HTTP_CLIENT;

public class LoginController {

    // Main controller
    private AppController mainController;
    // Username section
    @FXML private TextField usernameTextField;
    private final StringProperty usernameProperty;
    // Choices section
    @FXML private ChoiceBox<String> alliesTeamChoiceBox;
    @FXML private ChoiceBox<Integer> totalThreadsChoiceBox;
    @FXML private Slider tasksWithdrawalSizeSlider;
    // Error section
    @FXML private Label errorMessageLabel;
    private final StringProperty errorMessageProperty;

    public LoginController() {
        usernameProperty = new SimpleStringProperty("");
        errorMessageProperty = new SimpleStringProperty("Crack The Enigma - Exercise 3");
    }

    @FXML
    private void initialize() {
        usernameProperty.bind(usernameTextField.textProperty());
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        // Allies' teams (dynamic - update all, all the time)
        updateExistingAlliesTeam();
        // Total threads choice box
        totalThreadsChoiceBox.getItems().addAll(1, 2, 3, 4);
        totalThreadsChoiceBox.setValue(1);
        // Slider - accepts only integers (not rational numbers)
        tasksWithdrawalSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> tasksWithdrawalSizeSlider.setValue(newVal.intValue()));
    }

    private void updateExistingAlliesTeam() {
        // TODO: add allies team from server (those who are already registered)
        alliesTeamChoiceBox.getItems().addAll("None");
        alliesTeamChoiceBox.setValue("None");
    }

    @FXML
    @SuppressWarnings("SpellCheckingInspection")
    void loginAction() {
        if (usernameProperty.get().isEmpty()) {
            errorMessageProperty.set("ERROR: No username has been inserted!");
        } else if (alliesTeamChoiceBox.getValue().equals("None")) {
            errorMessageProperty.set("ERROR: You must choose allies team!");
        } else {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create("{\n\t\"first_param\":\"xxxxxx\"}", mediaType);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/agent/username?username=" + usernameProperty.get())
                    .put(body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Call call = HTTP_CLIENT.newCall(request); // Create a Call object
            call.enqueue(new Callback() { // Execute a call (Asynchronous)

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->
                            errorMessageProperty.set("ERROR: " + e.getLocalizedMessage())
                    );
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (response.code() != 200) {
                            String responseBodyStr = Objects.requireNonNull(responseBody).string();
                            Platform.runLater(() ->
                                    errorMessageProperty.set(responseBodyStr)
                            );
                        } else {
                            Platform.runLater(() -> {
                                mainController.updateAgentUsername(usernameProperty.get()); // TODO: add relevant fields to Agents' DM / thread pool / whatever
                                mainController.switchToContestScreen();
                            });
                        }
                    }
                }
            });
        }
    }

    @FXML
    void quitAction() {
        // TODO: respond to quit
        Platform.exit();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

}