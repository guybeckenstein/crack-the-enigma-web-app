package controllersAgent;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import timerTasks.login.AvailableTeamsTimerTask;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static http.Base.BASE_URL;
import static http.Base.HTTP_CLIENT;

public class LoginController {

    // Main controller
    private AppController mainController;
    // Username section
    @FXML private TextField usernameTextField;
    private final StringProperty usernameProperty;
    // Choices section - available allies teams
    @FXML private ComboBox<String> alliesTeamComboBox;
    private Timer availableTeamsTimer;
    // Choices section - other
    @FXML private ComboBox<Integer> totalThreadsComboBox;
    @FXML private Slider tasksWithdrawalSizeSlider;
    @FXML private ComboBox<Integer> tasksWithdrawalSizeComboBox;
    // Error section
    @FXML private Label errorMessageLabel;
    private final StringProperty errorMessageProperty;

    public LoginController() {
        // Username section
        usernameProperty = new SimpleStringProperty("");
        // Choices section - available allies teams
        availableTeamsTimer = new Timer(true);
        AvailableTeamsTimerTask availableTeamsTimerTask = new AvailableTeamsTimerTask(this); // Extends TimerTask
        availableTeamsTimer.scheduleAtFixedRate(availableTeamsTimerTask, 0, 500);
        // Choices section - other
        errorMessageProperty = new SimpleStringProperty("Crack The Enigma - Exercise 3");
    }

    @FXML
    private void initialize() {
        usernameProperty.bind(usernameTextField.textProperty());
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        // Allies' teams (dynamic - update all, all the time) - 'updateExistingAlliesTeams(Set<String> currentTeams)'

        // Total threads choice box
        totalThreadsComboBox.getItems().addAll(1, 2, 3, 4);
        // Slider - accepts only integers (not rational numbers)
        tasksWithdrawalSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> tasksWithdrawalSizeSlider.setValue(newVal.intValue()));
        // Choice box - values from 1 to 1_000
        tasksWithdrawalSizeComboBox.getItems().addAll(IntStream.range(1, 1_000 + 1).boxed().collect(Collectors.toList()));
        tasksWithdrawalSizeComboBox.setValue(1);
        // Bind choice box and slider bidirectional
        tasksWithdrawalSizeSlider.valueProperty().addListener(
                (options, oldValue, newValue) -> tasksWithdrawalSizeComboBox.setValue(newValue.intValue()));
    }

    public void updateExistingAlliesTeams(List<String> currentTeams) {
        String priorSelectedValue = alliesTeamComboBox.getValue();
        alliesTeamComboBox.getItems().clear();
        alliesTeamComboBox.getItems().setAll(currentTeams);
        alliesTeamComboBox.setValue(priorSelectedValue);
    }

    @FXML
    void loginAction() {
        if (usernameProperty.get().isEmpty()) {
            errorMessageProperty.set("ERROR: No username has been inserted!");
        } else if (alliesTeamComboBox.getValue() == null) {
            errorMessageProperty.set("ERROR: You must choose allies team!");
        } else if (totalThreadsComboBox.getValue() == null) {
            errorMessageProperty.set("ERROR: You must choose amount of available threads!");
        } else {
            String RESOURCE = "/agent/login";
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
            urlBuilder.addQueryParameter("username", usernameProperty.get()); // Username value
            urlBuilder.addQueryParameter("type", "Agent"); // Username type
            urlBuilder.addQueryParameter("team", alliesTeamComboBox.getValue()); // Chosen allies team
            urlBuilder.addQueryParameter("threads", totalThreadsComboBox.getValue().toString()); // Total threads
            urlBuilder.addQueryParameter("tasks", String.valueOf(tasksWithdrawalSizeComboBox.getValue())); // Total threads
            String finalUrl = urlBuilder.build().toString();

            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create("", mediaType);

            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("PUT", body)
                    .build();

            Call call = HTTP_CLIENT.newCall(request); // Create a Call object
            call.enqueue(new Callback() { // Execute a call (Asynchronous)

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->
                            errorMessageProperty.set(e.getLocalizedMessage())
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
                                try {
                                    availableTeamsTimer.cancel(); // Does not update the ComboBox anymore
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                mainController.updateAgentUsername(usernameProperty.get());
                                mainController.switchToContestScreen(alliesTeamComboBox.getValue(), totalThreadsComboBox.getValue(),
                                        tasksWithdrawalSizeComboBox.getValue());
                            });
                        }
                    }
                }
            });
        }
    }

    @FXML
    void quitAction() {
        Platform.exit();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void reset() {
        // Choices section - available allies teams
        availableTeamsTimer = new Timer(true);
        AvailableTeamsTimerTask availableTeamsTimerTask = new AvailableTeamsTimerTask(this); // Extends TimerTask
        availableTeamsTimer.scheduleAtFixedRate(availableTeamsTimerTask, 0, 500);
        // Reset data
        usernameTextField.setText("");
        errorMessageProperty.set("Crack The Enigma - Exercise 3");
        alliesTeamComboBox.getSelectionModel().clearSelection(); // Allies' teams
        totalThreadsComboBox.getSelectionModel().clearSelection(); // Total threads
        tasksWithdrawalSizeSlider.setValue(1.0); // Tasks withdrawal
        tasksWithdrawalSizeComboBox.setValue(1); // Tasks withdrawal
    }
}