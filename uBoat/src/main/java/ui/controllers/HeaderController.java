package ui.controllers;

import engine.enigmaEngine.exceptions.*;
import engine.enigmaEngine.interfaces.Reflector;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class HeaderController {
    private AppController mainController;
    @FXML private HBox headerHBox;
    private String currXMLFilePath;
    @FXML private TextField xmlFilePathTextField;

    @FXML private Label loadXMLErrorLabel;

    @FXML private ChoiceBox<String> styleChoiceBox;

    @FXML private Button machineButton;

    @FXML private Button contestButton;

    private File recordsDir;


    public HeaderController() {
        currXMLFilePath = "";
        recordsDir = null;
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
    }

    @FXML
    void machineButtonActionListener() {
        machineButton.getStyleClass().add("chosen-button");
        contestButton.getStyleClass().remove("chosen-button");
        mainController.changeToMachineScreen();
    }

    @FXML
    void contestButtonActionListener() {
        machineButton.getStyleClass().remove("chosen-button");
        contestButton.getStyleClass().add("chosen-button");
        mainController.changeToContestScreen();
    }

    @FXML
    void loadXML() {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Pick your XML file for Ex3.");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));

            if (!currXMLFilePath.equals("")) {
                updateFileChooserDirectory(fc);
            }

            File newXMLFile = fc.showOpenDialog(null);
            String filePath = newXMLFile.getAbsolutePath();
            if (filePath.equals(currXMLFilePath)) {
                throw new FileAlreadyExistsException("File given is already defined as the Enigma machine.");
            } else if (!filePath.equals("")) {

                /*String url = "http://localhost:8080/upload-file";
                String charset = java.nio.charset.StandardCharsets.UTF_8.name();

                String query = String.format("param1=%s", URLEncoder.encode(filePath, charset));

                URLConnection connection = new URL(url + "?" + query).openConnection();
                connection.setRequestProperty("Accept-Charset", charset);
                InputStream response = connection.getInputStream();

                try (Scanner scanner = new Scanner(response)) {
                    String responseBody = scanner.useDelimiter("\\A").next();
                    System.out.println(responseBody);
                }
*/
                AppController.getModelMain().readMachineFromXMLFile(filePath);
                xmlFilePathTextField.setText(filePath);
                currXMLFilePath = filePath;

                // Update reflector choice box options
                List<Reflector.ReflectorID> unsortedReflectors = AppController.getModelMain().getXmlDTO().getReflectorsFromXML()
                        .stream().map(Reflector.ReflectorID::valueOf).sorted().collect(Collectors.toList());
                mainController.updateMachineScreen(
                        unsortedReflectors.stream().map(String::valueOf).collect(Collectors.toList()),
                        Integer.toString(AppController.getModelMain().getXmlDTO().getRotorsFromXML().size()),
                        Integer.toString(AppController.getModelMain().getXmlDTO().getRotorsFromXML().size())
                );
                mainController.initializeMachineStates("NaN");
                mainController.updateScreensDisability(true);
                mainController.updateDictionary();

                loadXMLErrorLabel.setText("Machine XML file successfully loaded.");

            }
        } catch (NullPointerException e) { // If a user exits XML file search
            // Continue...
        } catch (FileAlreadyExistsException e) {
            loadXMLErrorLabel.setText(e.getLocalizedMessage());
        } catch (InvalidMachineException | JAXBException | InvalidRotorException | IOException
                 | InvalidABCException | InvalidReflectorException | InvalidDecipherException e) {
            loadXMLErrorLabel.setText(e.getLocalizedMessage());
            mainController.reset();
            mainController.initializeMachineStates("NaN");
            mainController.updateScreensDisability(true);
            currXMLFilePath = "";
        }
    }

    private void updateFileChooserDirectory(FileChooser fc) {
        StringBuilder newFilePath = new StringBuilder();
        // Fix string to java-wise string
        for (Character ch : currXMLFilePath.toCharArray()) {
            if (ch.equals('\\')) {
                newFilePath.append("/");
            } else {
                newFilePath.append(ch);
            }
        }

        String[] parts = newFilePath.toString().split("/");
        newFilePath = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) {
                newFilePath.append("/");
            }
            newFilePath.append(parts[i]);
        }

        recordsDir = new File(newFilePath.toString());
        if (!recordsDir.exists()) {
            recordsDir.mkdirs();
        }
        fc.setInitialDirectory(recordsDir);
    }

    public void updateLabelTextsToEmpty() {
        loadXMLErrorLabel.setText("");
    }



    public void updateStylesheet(Number num) {
        headerHBox.getStylesheets().remove(0);
        if (num.equals(0)) {
            headerHBox.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("ui/header/headerStyleOne.css")).toString());
        } else if (num.equals(1)) {
            headerHBox.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("ui/header/headerStyleTwo.css")).toString());
        } else {
            headerHBox.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("ui/header/headerStyleThree.css")).toString());
        }
    }
}