package controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import static web.uBoat.http.Configuration.BASE_URL;
import static web.uBoat.http.Configuration.HTTP_CLIENT;

public class LoginController {

    // Main controller
    private AppController mainController;
    // Username section
    @FXML private TextField usernameTextField;
    private final StringProperty usernameProperty = new SimpleStringProperty("");
    // Error section
    @FXML private Label errorMessageLabel;
    private final StringProperty errorMessageProperty = new SimpleStringProperty("");

    @FXML
    private void initialize() {
        usernameProperty.bind(usernameTextField.textProperty());
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }
    @FXML
    @SuppressWarnings("SpellCheckingInspection")
    void loginAction() {
        if (usernameProperty.get().isEmpty()) {
            errorMessageProperty.set("ERROR: No username has been inserted!");
        } else {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n\t\"first_param\":\"xxxxxx\"}");
            Request request = new Request.Builder()
                    .url(BASE_URL + "/uboat/username?username=" + usernameProperty.get())
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
                                mainController.updateUsername(usernameProperty.get());
                                mainController.switchToMachineScreen();
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
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

}