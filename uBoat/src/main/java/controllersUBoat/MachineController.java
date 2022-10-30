package controllersUBoat;

import com.google.gson.Gson;
import jar.enigmaEngine.exceptions.InvalidCharactersException;
import jar.enigmaEngine.exceptions.InvalidPlugBoardException;
import jar.enigmaEngine.exceptions.InvalidReflectorException;
import jar.enigmaEngine.exceptions.InvalidRotorException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import impl.models.Specifications;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;

import static http.Base.BASE_URL;
import static http.Base.HTTP_CLIENT;

// First screen
public class MachineController {
    // Main & Parent components
    private AppController mainController;
    @FXML private ScrollPane mainScrollPane;
    @FXML private BorderPane mainBorderPane;
    // Models
    private final Specifications specs;
    // Machine configuration status and input screen controller
    @FXML private HBox topHBox;
    @FXML private Label maxRotorsInMachineLabel;
    @FXML private Label currentUsedMachineRotorsLabel;
    @FXML private Label totalReflectorsInMachineLabel;
    @FXML private Label currentSelectedMachineReflectorLabel;
    // For disabling screen partitions
    @FXML private VBox configurationVBox;
    @FXML private Label detailsLabel;
    // Screen buttons
    @FXML private Button setCodeButton; // For 'default button' purposes only
    // User configuration input section
    @FXML private TextField rotorsAndOrderTextField;
    @FXML private TextField rotorsStartingPosTextField;
    @FXML private ChoiceBox<String> reflectorChoiceBox;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public MachineController() {
        specs = new Specifications();
    }

    @FXML
    private void initialize() {
        // Only for binding the ENTER key to the input text field
        setCodeButton.setDefaultButton(true);
        setCodeButton.setOnAction(event -> getConfigurationFromUser());
        // Adding change property
        rotorsAndOrderTextField.textProperty().addListener(new ClearStatusListener());
        rotorsStartingPosTextField.textProperty().addListener(new ClearStatusListener());
        // Model
        maxRotorsInMachineLabel.textProperty().bind(specs.rotorsAmountInMachineXMLProperty());
        currentUsedMachineRotorsLabel.textProperty().bind(specs.currentRotorsInMachineProperty());
        totalReflectorsInMachineLabel.textProperty().bind(specs.reflectorsAmountInMachineXMLProperty());
        currentSelectedMachineReflectorLabel.textProperty().bind(specs.currentReflectorInMachineProperty());

        // Default values are added in certain screen places. This is called after constructor and after FXML variables are created.
        reset();
    }

    private void setConfigurationDisability(boolean bool) {
        mainBorderPane.setDisable(bool);
        topHBox.setDisable(bool);
        configurationVBox.setDisable(bool);
    }

    @FXML
    void getConfigurationFromUser() {
        if (isValidConfigurationTextFields()) { // Gets input from user and generates it to the machine
            String rotors = rotorsAndOrderTextField.getText();
            String startingPositions = rotorsStartingPosTextField.getText();
            String reflectorID = reflectorChoiceBox.getValue();

            startingPositions = new StringBuilder(startingPositions).reverse().toString();

            try {
                AppController.getModelMain().initializeEnigmaCodeManually(rotors, startingPositions, "", reflectorID);
                detailsLabel.setText("Manually initialized configuration code.");
            } catch (NumberFormatException e) {
                detailsLabel.setText("Non-numeric value was inserted in 'Rotors And Order'.");
                return;
            } catch (InvalidRotorException | InvalidReflectorException | InvalidPlugBoardException |
                     InvalidCharactersException | NullPointerException | InputMismatchException | IllegalArgumentException e) {
                detailsLabel.setText(e.getLocalizedMessage());
                return;
            }
        }
        sendConfigurationToHttpClient();
    }


    private boolean isValidConfigurationTextFields() {
        if (rotorsAndOrderTextField.getText().equals("")) {
            detailsLabel.setText("You did not add your rotors' IDs and their order.");
            return false;
        }
        else if (rotorsStartingPosTextField.getText().equals("")) {
            detailsLabel.setText("You did not add your rotors' starting positions.");
            return false;
        }
        return true;
    }

    @FXML
    void setConfigurationRandomly() {
        AppController.getModelMain().initializeEnigmaCodeAutomatically();
        detailsLabel.setText("Automatically initialized configuration code.");
        sendConfigurationToHttpClient();
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void sendConfigurationToHttpClient() {
        Gson gson = new Gson();
        String enigmaMachine = gson.toJson(AppController.getModelMain().getEngine()); // Not interface, but Class

        RequestBody body = // Create request body
                new FormBody.Builder()
                        .add("username", mainController.getUBoatUsername())
                        .add("machine", enigmaMachine)
                        .add("required", Integer.toString(AppController.getModelMain().getXmlToServletDTO().getNumAllies()))
                        .build();
        Request request = new Request.Builder() // Create request object
                .url(BASE_URL + "/uboat/machine-configuration")
                .post(body)
                .build();
        Call call = HTTP_CLIENT.newCall(request); // Create a Call object

        call.enqueue(new Callback() { // Execute a call (Asynchronous)
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> detailsLabel.setText("ERROR: Failed to set machine configuration, got 4xx-5xx response!"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (ResponseBody ignoredResponseBody = response.body()) {
                    System.out.println("Successfully set UBoat's machine configuration!");
                    Platform.runLater(() -> {
                        // JavaFX...
                        mainController.initializeMachineStates(AppController.getModelMain().getMachineHistoryStates().getCurrentMachineCode());
                        specs.setCurrentRotorsInMachine(Integer.toString(AppController.getModelMain().getEngine().getEngineDTO().getSelectedRotors().size()));
                        specs.setCurrentReflectorInMachine(AppController.getModelMain().getEngine().getEngineDTO().getSelectedReflector());
                        updateConfigurationsAndScreens();
                        try { // Make user understand what happened
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        mainController.switchToContestScreen();
                    });
                }
            }
        });
    }

    public void updateConfigurationsAndScreens() {
        rotorsAndOrderTextField.setText("");
        rotorsStartingPosTextField.setText("");
        reflectorChoiceBox.setValue(reflectorChoiceBox.getItems().get(0));
    }

    public void updateScreen(List<String> choiceBoxItems, String numberOfRotors, String numberOfReflectors) {
        reflectorChoiceBox.setItems(FXCollections.observableArrayList(choiceBoxItems));
        reflectorChoiceBox.setValue(reflectorChoiceBox.getItems().get(0));

        specs.setRotorsAmountInMachineXML(numberOfRotors);
        specs.setReflectorsAmountInMachineXML(numberOfReflectors);

        setConfigurationDisability(false);
    }
    public void reset() {
        setConfigurationDisability(true);

        // Specifications
        specs.setRotorsAmountInMachineXML("NaN");
        specs.setCurrentRotorsInMachine("NaN");
        specs.setReflectorsAmountInMachineXML("NaN");
        specs.setCurrentReflectorInMachine("NaN");

        // Configuration
        rotorsAndOrderTextField.setText("");
        rotorsStartingPosTextField.setText("");
        reflectorChoiceBox.getItems().clear();

        // Details label
        emptyDetailsLabelText();
    }

    class ClearStatusListener implements ChangeListener<String> {
        @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            mainController.emptyLabelText();
        }
    }
    public void emptyDetailsLabelText() {
        detailsLabel.setText("");
    }



    public void updateStylesheet(Number num) {
        mainScrollPane.getStylesheets().remove(0);
        if (num.equals(0)) {
            mainScrollPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("machine/machineStyleOne.css")).toString());
        } else if (num.equals(1)) {
            mainScrollPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("machine/machineStyleTwo.css")).toString());
        } else {
            mainScrollPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("machine/machineStyleThree.css")).toString());
        }
    }
}