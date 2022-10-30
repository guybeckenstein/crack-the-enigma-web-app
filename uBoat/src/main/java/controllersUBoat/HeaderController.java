package controllersUBoat;

import com.google.gson.Gson;
import jar.enigmaEngine.exceptions.*;
import jar.enigmaEngine.interfaces.Reflector;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static http.Base.BASE_URL;
import static http.Base.HTTP_CLIENT;

public class HeaderController {
    // Main
    private AppController mainController;
    @FXML private HBox headerHBox;
    // File path + response
    @FXML private Button loadButton;
    private String currXMLFilePath;
    @FXML private TextField xmlFilePathTextField;

    @FXML private Label loadXMLErrorLabel;
    // Screens buttons

    @FXML private Button machineButton;

    @FXML private Button contestButton;
    // Style
    @FXML private ChoiceBox<String> styleChoiceBox;
    // Username
    @FXML private Label usernameHeaderLabel;
    private String username;
    private String filePath;

    public HeaderController() {
        currXMLFilePath = "";
        username = "Guest";
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        machineButton.getStyleClass().add("chosen-button");

        styleChoiceBox.getItems().addAll("Style #1", "Style #2", "Style #3");
        styleChoiceBox.setValue("Style #1");
        styleChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> mainController.updateStylesheet(number2));

        usernameHeaderLabel.setText("Hello " + username + " - UBoat User");
        loadButton.setMouseTransparent(true);
    }

    /** Code when moving to contest screen **/
    public void switchToContestScreen() {
        contestButton.getStyleClass().add("chosen-button");
        machineButton.getStyleClass().remove("chosen-button");
    }

    /** Code when moving to log in screen **/
    public void switchToLoginScreen() {
        // Reset header details
        xmlFilePathTextField.setText("");
        currXMLFilePath = "";
        username = "Guest";
        usernameHeaderLabel.setText("Hello " + username + " - UBoat User");
        loadButton.setMouseTransparent(true);
        // Reset header style
        machineButton.getStyleClass().add("chosen-button");
        contestButton.getStyleClass().remove("chosen-button");
    }

    @FXML
    void loadXML() {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Pick your XML file for Ex3.");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));

            File newXMLFile = fc.showOpenDialog(null);
            filePath = newXMLFile.getAbsolutePath();
            if (filePath.equals(currXMLFilePath)) {
                throw new FileAlreadyExistsException("File given is already defined as the Enigma machine.");
            } else if (!filePath.equals("")) {

                try {
                    AppController.getModelMain().createMachineFromXMLFile(filePath);

                    uploadFileToServer();
                } catch (InvalidMachineException | JAXBException | InvalidRotorException | IOException
                         | InvalidABCException | InvalidReflectorException | InvalidDecipherException |
                         InvalidBattlefieldException e) {
                    loadXMLErrorLabel.setText(e.getLocalizedMessage());
                    mainController.reset();
                    mainController.initializeMachineStates("NaN");
                    currXMLFilePath = "";
                }
            }
        } catch (NullPointerException e) { // If a user exits XML file search
            // Continue...
        } catch (FileAlreadyExistsException | RuntimeException e) {
            loadXMLErrorLabel.setText(e.getLocalizedMessage());
        }
    }

    /** Ex3 **/
    @SuppressWarnings("SpellCheckingInspection")
    private void uploadFileToServer() {
        // Get DTO body parameter
        Gson gson = new Gson();
        String xmlDTO = gson.toJson(AppController.getModelMain().getXmlToServletDTO());
        xmlDTO = xmlDTO.replace("\\u0027", "'");

        RequestBody body = new FormBody.Builder() // Create request body
                .add("username", username)
                .add("xml", xmlDTO)
                .build();
        Request request = new Request.Builder() // Create request object
                .url(BASE_URL + "/uboat/upload-file")
                .post(body)
                .build();
        Call call = HTTP_CLIENT.newCall(request); // Create a Call object

        call.enqueue(new Callback() { // Execute a call (Asynchronous)
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> loadXMLErrorLabel.setText("ERROR: failed to upload XML file!"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (ResponseBody ignoredResponseBody = response.body()) {
                    if (response.isSuccessful()) {
                        System.out.println("Machine XML file successfully loaded."); // log
                        Platform.runLater(() -> successfullyAddedXmlFileConfiguration());
                    }
                }
            }
        });
    }

    private void successfullyAddedXmlFileConfiguration() {
        xmlFilePathTextField.setText(filePath);
        currXMLFilePath = filePath;

        // Update reflector choice box options
        List<Reflector.ReflectorID> unsortedReflectors = AppController.getModelMain().getXmlDTO().getReflectorsFromXML()
                .stream().map(Reflector.ReflectorID::valueOf).sorted().collect(Collectors.toList());
        mainController.updateMachineScreen(
                unsortedReflectors.stream().map(String::valueOf).collect(Collectors.toList()),
                Integer.toString(AppController.getModelMain().getXmlDTO().getRotorsFromXML().size()),
                Integer.toString(AppController.getModelMain().getXmlDTO().getReflectorsFromXML().size())
        );

        mainController.initializeMachineStates("NaN");
        mainController.updateDictionary();
    }
    public void emptyLabelText() {
        loadXMLErrorLabel.setText("");
    }

    public void updateStylesheet(Number num) {
        headerHBox.getStylesheets().remove(0);
        if (num.equals(0)) {
            headerHBox.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("header/headerStyleOne.css")).toString());
        } else if (num.equals(1)) {
            headerHBox.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("header/headerStyleTwo.css")).toString());
        } else {
            headerHBox.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("header/headerStyleThree.css")).toString());
        }
    }

    public void setLoadButtonMouseTransparency(boolean bool) {
        loadButton.setMouseTransparent(bool);
    }

    public void updateUsername(String username) {
        this.username = username;
        usernameHeaderLabel.setText("Hello " + username + " - UBoat User");
    }

    public String getUsername() {
        return username;
    }
}