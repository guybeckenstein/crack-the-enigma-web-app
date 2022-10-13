package ui.controllers;

import engine.enigmaEngine.exceptions.InvalidCharactersException;
import engine.enigmaEngine.interfaces.EnigmaEngine;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ui.decryptionManager.Candidate;
import ui.decryptionManager.Difficulty;
import ui.impl.Trie;
import ui.impl.models.MachineStateModel;

import java.util.*;
import java.util.stream.Collectors;

// Third screen
public class ContestController {
    // Buttons component
    @FXML VBox dmButtonsVBox;
    @FXML public Button startResumeDM;
    @FXML public Button pauseDM;
    @FXML public Button stopDM;
    // Main component
    private AppController mainController;
    // Models
    private final MachineStateModel machineStatesConsole;
    @FXML private ScrollPane mainScrollPane;
    @FXML private VBox bruteForceVBox;
    // Machine states
    @FXML private Label firstMachineStateLabel;
    @FXML private Label currentMachineStateLabel;
    // Search for words
    @FXML private VBox searchVBox; // Only for binding the ENTER key to the input text field
    @FXML private TextField searchInputTextField;
    @FXML private ListView<String> searchDictionaryWordsListView;
    Trie dictionaryTrie;
    // Input to encrypt / decrypt
    @FXML private VBox keyboardInputVBox; // Only for binding the ENTER key to the input text field
    @FXML private TextField inputToEncryptDecryptInput;
    @FXML private TextField enigmaOutputTextField;
    @FXML private Button enterCurrentKeyboardInputButton;

    // DM Operational component
    @FXML private GridPane decryptionManagerGridPane;
    @FXML private Label totalAgentsLabel;
    @FXML private Slider agentsSliderInput;
    @FXML private ChoiceBox<String> difficultyLevelInput;
    @FXML private Label difficultyLevelLabel;
    @FXML private Label missionSizeLabel;
    @FXML private TextField missionSizeInput;
    @FXML private Label totalMissionsLabel;
    private Difficulty dmDifficultyLevel;
    // DM Output
    @FXML private ProgressBar progressBar;
    @FXML private Label progressPercentLabel;
    @FXML private Label averageTime;
    private EnigmaEngine taskCurrentConfiguration;
    private ui.decryptionManager.DecryptionManagerTask dmTask;
    private String timeElapsed = "";
    // DM Output - table view
    @FXML private TableView<Candidate> finalCandidatesTableView;
    @FXML private TableColumn<Candidate, String> configurationColumn;
    @FXML private TableColumn<Candidate, String> wordsColumn;
    @FXML private TableColumn<Candidate, String> timeColumn;
    @FXML private TableColumn<Candidate, String> agentColumn;
    private final ObservableList<Candidate> candidateList;


    public ContestController() {
        machineStatesConsole = new MachineStateModel(); // Model
        candidateList = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        initKeyboardInput();

        initOperational();

        decryptionManagerGridPane.setDisable(true);
        dmButtonsVBox.setDisable(true);

        initDictionaryTrie();
        firstMachineStateLabel.textProperty().bind(machineStatesConsole.firstMachineStateProperty());
        currentMachineStateLabel.textProperty().bind(machineStatesConsole.currentMachineStateProperty());

        initCandidatesTableView();

    }

    private void initOperational() {
        // Updates total agents
        totalAgentsLabel.setText(Integer.toString((int)agentsSliderInput.getValue()));
        setContestDisability(true);
        agentsSliderInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            totalAgentsLabel.setText(Integer.toString((int)agentsSliderInput.getValue()));
            if (!missionSizeLabel.getText().equals("")) {
                updateMissionsLabel();
            }
        });

        // Updates difficulty level
        difficultyLevelInput.getItems().addAll(Arrays.stream(Difficulty.values()).map(Enum::name).toArray(String[]::new));
        difficultyLevelInput.setValue("EASY");
        dmDifficultyLevel = Difficulty.EASY;
        difficultyLevelInput.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                dmDifficultyLevel = Difficulty.EASY;
            } else if (newValue.intValue() == 1) {
                dmDifficultyLevel = Difficulty.MEDIUM;
            } else if (newValue.intValue() == 2) {
                dmDifficultyLevel = Difficulty.HARD;
            } else if (newValue.intValue() == 3) {
                dmDifficultyLevel = Difficulty.IMPOSSIBLE;
            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid difficulty level");
            }
            updateMissionsLabel();
            String difficultyLevel = dmDifficultyLevel.toString().toLowerCase();
            difficultyLevelLabel.setText(difficultyLevel.substring(0, 1).toUpperCase() + difficultyLevel.substring(1));
        });

        // Updates mission size
        missionSizeInput.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                missionSizeLabel.setText(newValue);
                updateMissionsLabel();
            } catch (NumberFormatException e) {
                totalMissionsLabel.setText("NaN");
                missionSizeLabel.setText("Invalid input");
            }
        });
    }

    private void initKeyboardInput() {
        // Only for binding the ENTER key to the input text field
        enterCurrentKeyboardInputButton.setOnAction(this::enterCurrentKeyboardInputButtonActionListener);
        keyboardInputVBox.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                enterCurrentKeyboardInputButton.fire();
                ev.consume();
            }
        });
    }

    private void initDictionaryTrie() {
        searchInputTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDictionaryWordsListView.getItems().remove(0, searchDictionaryWordsListView.getItems().size());

            List<String> results = dictionaryTrie.getWordsWithPrefix(newValue.toUpperCase());
            if (results != null) {
                searchDictionaryWordsListView.getItems().addAll(results);
            }
        });

        searchDictionaryWordsListView.onMousePressedProperty().addListener((observable, oldValue, newValue) -> {
            String selectedWord = searchDictionaryWordsListView.getSelectionModel().getSelectedItem();
            if (selectedWord != null) {
                inputToEncryptDecryptInput.setText(selectedWord);
            }
        });

        searchDictionaryWordsListView.editableProperty().setValue(false);
        searchDictionaryWordsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String selectedWord = searchDictionaryWordsListView.getSelectionModel().getSelectedItem();
            if (selectedWord != null && inputToEncryptDecryptInput.getText() != null) {
                inputToEncryptDecryptInput.setText(inputToEncryptDecryptInput.getText() + selectedWord + " "); // Added " "
            }
        });
    }

    private void initCandidatesTableView() {
        configurationColumn.setCellValueFactory(new PropertyValueFactory<>("machineConfiguration"));
        wordsColumn.setCellValueFactory(new PropertyValueFactory<>("dictionaryWords"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeElapsed"));
        agentColumn.setCellValueFactory(new PropertyValueFactory<>("agent"));
        finalCandidatesTableView.setPlaceholder(new Label("No candidates to display"));

        // Automatically scrolls down
        finalCandidatesTableView.getItems().addListener((ListChangeListener<Candidate>) (c -> {
            c.next();
            final int size = finalCandidatesTableView.getItems().size();
            if (size > 0) {
                finalCandidatesTableView.scrollTo(size - 1);
            }
        }));
    }

    private void updateMissionsLabel() {
        long missionSize;
        try {
            missionSize = Long.parseLong(missionSizeInput.getText());
            if (missionSize <= 0) {
                throw new ArithmeticException();
            }
        } catch (NumberFormatException | ArithmeticException e) {
            totalMissionsLabel.setText("NaN");
            missionSizeLabel.setText("Invalid input");
            return;
        }
        EnigmaEngine enigmaEngine = AppController.getModelMain().getEngine();
        totalMissionsLabel.setText(Long.toString(
                Difficulty.translateDifficultyLevelToMissions(dmDifficultyLevel, enigmaEngine.getEngineDTO(),
                        AppController.getModelMain().getXmlDTO().getReflectorsFromXML().size(), enigmaEngine.getABCSize())
                        / (missionSize
                        * (long)agentsSliderInput.getValue())));
    }

    @FXML
    void StartResumeDMActionListener() {
        toggleTaskButtons(true);

        if (startResumeDM.getText().contains("Resume")) {
            synchronized (dmTask) {
                dmTask.setPaused(false);
                dmTask.notifyAll();
            }
        } else if (startResumeDM.getText().contains("Start")) {
            dmTask = new ui.decryptionManager.DecryptionManagerTask(
                    enigmaOutputTextField.getText(), Long.parseLong(totalMissionsLabel.getText()),
                    Integer.parseInt(totalAgentsLabel.getText()), Integer.parseInt(missionSizeLabel.getText()),
                    dmDifficultyLevel, AppController.getModelMain().getXmlDTO().getReflectorsFromXML().size(),
                    AppController.getModelMain().getXmlDTO().getRotorsFromXML().size(), this, this::dmOnFinished);

            Thread th = new Thread(dmTask);
            th.start();
            System.out.println("Starting the task...");
        } else {
            throw new RuntimeException();
        }

        if (startResumeDM.getText().contains("Start")) {
            startResumeDM.setText(startResumeDM.getText().replace("Start", "Resume"));
        }
    }

    public EnigmaEngine getTaskCurrentConfiguration() {
        return taskCurrentConfiguration;
    }

    public synchronized void updateValues(Queue<Candidate> newCandidates, String averageElapsedTime) {
        if (!newCandidates.isEmpty()) {
            finalCandidatesTableView.getItems().addAll(newCandidates);
            while (!newCandidates.isEmpty()) {
                candidateList.add(newCandidates.remove());
            }
        }
        averageTime.setText(averageElapsedTime);
    }

    private void toggleTaskButtons(boolean bool) {
        keyboardInputVBox.setDisable(bool);
        searchVBox.setDisable(bool);
        decryptionManagerGridPane.setDisable(bool);
        startResumeDM.setDisable(bool);
        stopDM.setDisable(!bool);
        pauseDM.setDisable(!bool);
    }

    @FXML
    void PauseDMActionListener() {
        dmTask.setPaused(true);
        startResumeDM.setDisable(false);
        pauseDM.setDisable(true);
    }

    @FXML
    void StopDMActionListener() {
        startResumeDM.setText(startResumeDM.getText().replace("Resume", "Start"));
        toggleTaskButtons(false);
        if (dmTask != null) {
            dmOnFinished();
        }

    }

    @FXML
    void setDMPropertiesActionListener() {
        try {
            if (missionSizeLabel.getText().equals("NaN") || missionSizeLabel.getText().equals("Invalid input")) {
                throw new InputMismatchException("There is no valid mission size input.");
            } else if (missionSizeLabel.getText().equals("0")) {
                throw new InputMismatchException("Each agent must perform at least 1 mission (task).");
            }
            dmButtonsVBox.setDisable(false);
            startResumeDM.setDisable(false);
            pauseDM.setDisable(true);
            stopDM.setDisable(true);
        } catch (InputMismatchException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid input");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void setContestDisability(boolean bool) {
        // enable brute force top screen
        bruteForceVBox.setDisable(bool);
        // disable or remove brute force history
        if (dmTask != null) {
            dmTask.cancel();
            unbindTaskFromUIComponents("0", false);

            decryptionManagerGridPane.setDisable(!bool);
            dmButtonsVBox.setDisable(!bool);

            updateDecryptionManagerDetails();
        }

    }

    private void updateDecryptionManagerDetails() {
        startResumeDM.setText(startResumeDM.getText().replace("Resume", "Start"));

        progressBar.setProgress(0);
        progressPercentLabel.setText("0 %");
        averageTime.setText("0.000000");
        timeElapsed = "0";
        finalCandidatesTableView.getItems().removeAll(candidateList);
        candidateList.removeAll(candidateList);
    }

    public void updateDictionary() {

        List<String> allWords = mainController.getDictionary().stream().map(String::trim) // Sorted dictionary list
                .sorted().collect(Collectors.toList());
        searchDictionaryWordsListView.getItems().addAll(allWords); // Sorted dictionary list view

        System.out.println("Dictionary trie created");
        dictionaryTrie = new Trie();
        allWords.forEach(dictionaryTrie::insert);
    }

    @FXML
    void enterCurrentKeyboardInputButtonActionListener(ActionEvent event) {
        try {
            String messageInput = inputToEncryptDecryptInput.getText().toUpperCase().trim();
            if (messageInput.equals("")) {
                throw new InputMismatchException("No encryption message was written.");
            }
            invalidMessageInput(messageInput);
            if (messageInput.charAt(messageInput.length() - 1) == ' ') {
                messageInput = messageInput.substring(0, messageInput.length() - 1);
            }
            String messageOutput = AppController.getModelMain().getMessageAndProcessIt(messageInput, true);

            new Alert(Alert.AlertType.CONFIRMATION, "Processed message: " + messageInput + " -> " + messageOutput).show();
            enigmaOutputTextField.setText(messageOutput);
            mainController.updateScreens(AppController.getModelMain().getCurrentMachineStateAsString());
            decryptionManagerGridPane.setDisable(false);
            mainController.updateLabelTextsToEmpty();
            taskCurrentConfiguration = AppController.getModelMain().getEngine().deepClone();
        } catch (InvalidCharactersException | InputMismatchException e) {
            new Alert(Alert.AlertType.ERROR, e.getLocalizedMessage()).show();
        }
    }

    private void invalidMessageInput(String messageInput) {
        Set<String> dictionaryWords = AppController.getModelMain().getEngine().getWordsDictionary().getWords();
        new ArrayList<>(Arrays.asList(AppController.getModelMain().getXmlDTO().getExcludedCharacters().split(""))).forEach((ch) -> {
            if (messageInput.contains(ch)) {throw new InputMismatchException("The encryption message \" " + messageInput
                        + " \" contains at least one dictionary's illegal characters: \" "
                        + AppController.getModelMain().getXmlDTO().getExcludedCharacters() + " \"");
            }});
        for (String splitStr : messageInput.split(" ")) {
            if (!dictionaryWords.contains(splitStr)) {
                throw new InputMismatchException("The encryption message must contain only dictionary words.");
            }
        }
    }

    public void initializeMachineStates(String machineStateConsoleString) {
        machineStatesConsole.setFirstMachineState(machineStateConsoleString);
        machineStatesConsole.setCurrentMachineState(machineStateConsoleString);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void resetMachineStateButtonActionListener() {
        AppController.getModelMain().resetMachine();
        mainController.resetScreens(true, null);
    }

    public void updateMachineStateAndDictionary(String currentMachineState) {
        machineStatesConsole.setCurrentMachineState(currentMachineState);
    }

    public void resetMachineStateAndEnigmaOutput(boolean bool, Object controller) {
        if (bool && controller == null) {
            new Alert(Alert.AlertType.INFORMATION, "Machine state has been successfully reset.").show();
        }
        machineStatesConsole.setCurrentMachineState(machineStatesConsole.getFirstMachineState());
        enigmaOutputTextField.setText("NaN");
    }

    public void updateLabelTextsToEmpty() {
        inputToEncryptDecryptInput.setText("");
        averageTime.setText("");
    }


    public void updateStylesheet(Number num) {
        mainScrollPane.getStylesheets().remove(0);
        if (num.equals(0)) {
            mainScrollPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("ui/contest/contestStyleOne.css")).toString());
        } else if (num.equals(1)) {
            mainScrollPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("ui/contest/contestStyleTwo.css")).toString());
        } else {
            mainScrollPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("ui/contest/contestStyleThree.css")).toString());
        }
    }

    public void bindTaskToUIComponents(Task<Boolean> aTask, Runnable onFinish) {
        // task progress bar
        progressBar.progressProperty().bind(aTask.progressProperty());

        // task percent label
        progressPercentLabel.textProperty().bind(
                Bindings.concat(
                        Bindings.format(
                                "%.0f",
                                Bindings.multiply(
                                        aTask.progressProperty(),
                                        100)),
                        " %"));

        // task cleanup upon finish
        aTask.valueProperty().addListener((observable, oldValue, newValue) -> onTaskFinished(Optional.ofNullable(onFinish)));
    }

    public void unbindTaskFromUIComponents(String timeElapsed, boolean bool) {
        progressBar.progressProperty().unbind();
        progressPercentLabel.textProperty().unbind();
        toggleTaskButtons(false);
        if (bool) {
            this.timeElapsed = timeElapsed;
        }
    }

    public void onTaskFinished(Optional<Runnable> onFinish) {
        progressBar.progressProperty().unbind();
        progressPercentLabel.textProperty().unbind();
        onFinish.ifPresent(Runnable::run);
    }

    private void dmOnFinished() {
        timeElapsed = dmTask.getTimeElapsed();
        dmTask.cancel();
        dmTask = null;
        unbindTaskFromUIComponents(timeElapsed, true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "This took " + timeElapsed + " seconds.");
        alert.setTitle("Done!");
        alert.setHeaderText("Done!");
        alert.showAndWait();

        updateDecryptionManagerDetails();
    }
}