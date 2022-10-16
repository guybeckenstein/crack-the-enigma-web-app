package controllersUBoat;

import common.rawData.battlefieldContest.AlliesData;
import decryptionManager.Candidate;
import decryptionManager.DecryptionManagerTask;
import decryptionManager.Difficulty;
import engine.enigmaEngine.exceptions.InvalidCharactersException;
import engine.enigmaEngine.interfaces.EnigmaEngine;
import impl.Trie;
import impl.models.MachineStateModel;
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
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;

// Third screen
public class ContestController {
    // Buttons component
    @FXML VBox contestButtonsVBox;
    @FXML public Button ready;
    @FXML public Button logout;
    // Main component
    private AppController mainController;
    // Models
    private final MachineStateModel machineStatesConsole;
    @FXML private ScrollPane mainScrollPane;  // For stylesheet purposes
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
    @FXML private TextField encryptionInputTextField;
    @FXML private TextField encryptionOutputTextField;
    @FXML private Button setEncryptionInputButton;
    // Active teams details
    @FXML private TableView<AlliesData> activeTeamsTableView;
    @FXML private TableColumn<AlliesData, String> alliesUsernameColumn;
    @FXML private TableColumn<AlliesData, String> totalAgentsColumn;
    @FXML private TableColumn<AlliesData, String> taskSizeColumn;

    // DM Operational component
    private EnigmaEngine taskCurrentConfiguration;
    private DecryptionManagerTask dmTask;
    private String timeElapsed = "";
    // DM Output - table view
    @FXML private TableView<Candidate> finalCandidatesTableView;
    @FXML private TableColumn<Candidate, String> configurationColumn;
    @FXML private TableColumn<Candidate, String> wordsColumn;
    @FXML private TableColumn<Candidate, String> timeColumn;
    @FXML private TableColumn<Candidate, String> alliesColumn;
    private final ObservableList<Candidate> candidateList;


    public ContestController() {
        machineStatesConsole = new MachineStateModel(); // Model
        candidateList = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        initKeyboardInput();

        contestButtonsVBox.setDisable(true);

        initDictionaryTrie();
        firstMachineStateLabel.textProperty().bind(machineStatesConsole.firstMachineStateProperty());
        currentMachineStateLabel.textProperty().bind(machineStatesConsole.currentMachineStateProperty());

        initActiveTeamsTableView();
        initCandidatesTableView();

    }

    private void initKeyboardInput() {
        // Only for binding the ENTER key to the input text field
        setEncryptionInputButton.setOnAction(this::setEncryptionInputActionListener);
        keyboardInputVBox.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                setEncryptionInputButton.fire();
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
                encryptionInputTextField.setText(selectedWord);
            }
        });

        searchDictionaryWordsListView.editableProperty().setValue(false);
        searchDictionaryWordsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String selectedWord = searchDictionaryWordsListView.getSelectionModel().getSelectedItem();
            if (selectedWord != null && encryptionInputTextField.getText() != null) {
                encryptionInputTextField.setText(encryptionInputTextField.getText() + selectedWord + " "); // Added " "
            }
        });
    }

    private void initActiveTeamsTableView() {
        alliesUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        totalAgentsColumn.setCellValueFactory(new PropertyValueFactory<>("totalAgents"));
        taskSizeColumn.setCellValueFactory(new PropertyValueFactory<>("taskSize"));
        activeTeamsTableView.setPlaceholder(new Label("No active teams to display"));

        // Automatically scrolls down
        finalCandidatesTableView.getItems().addListener((ListChangeListener<Candidate>) (c -> {
            c.next();
            final int size = finalCandidatesTableView.getItems().size();
            if (size > 0) {
                finalCandidatesTableView.scrollTo(size - 1);
            }
        }));
    }
    private void initCandidatesTableView() {
        configurationColumn.setCellValueFactory(new PropertyValueFactory<>("machineConfiguration"));
        wordsColumn.setCellValueFactory(new PropertyValueFactory<>("dictionaryWords"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeElapsed"));
        alliesColumn.setCellValueFactory(new PropertyValueFactory<>("agent"));
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

    @FXML
    void readyAction() {
        toggleTaskButtons(true);

        if (ready.getText().contains("Ready")) {
            // TODO: update to ex3
            long totalTasks = 450;
            int totalAgents = 2;
            int taskSize = 1;
            Difficulty difficulty = Difficulty.EASY;

            dmTask = new DecryptionManagerTask(
                    encryptionOutputTextField.getText(), totalTasks, totalAgents, taskSize, difficulty,
                    AppController.getModelMain().getXmlDTO().getReflectorsFromXML().size(),
                    AppController.getModelMain().getXmlDTO().getRotorsFromXML().size(), this, this::dmOnFinished);

            Thread th = new Thread(dmTask);
            th.start();
            System.out.println("Starting the task...");
        } else {
            new Alert(Alert.AlertType.ERROR, "Could not press 'Ready' button!").showAndWait();
        }
    }

    public EnigmaEngine getTaskCurrentConfiguration() {
        return taskCurrentConfiguration;
    }

    public synchronized void updateValues(Queue<Candidate> newCandidates) {
        if (!newCandidates.isEmpty()) {
            finalCandidatesTableView.getItems().addAll(newCandidates);
            while (!newCandidates.isEmpty()) {
                candidateList.add(newCandidates.remove());
            }
        }
    }

    private void toggleTaskButtons(boolean bool) {
        keyboardInputVBox.setDisable(bool);
        searchVBox.setDisable(bool);
        ready.setDisable(bool);
        logout.setDisable(!bool);
    }

    @FXML
    void logoutAction() {
        dmTask.cancel();
        // TODO: update to ex3 (exit screen and update other clients)
        if (dmTask != null) {
            dmOnFinished();
        }

    }

    private void updateDecryptionManagerDetails() {
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
    void setEncryptionInputActionListener(ActionEvent event) {
        try {
            String messageInput = encryptionInputTextField.getText().toUpperCase().trim();
            if (messageInput.equals("")) {
                throw new InputMismatchException("No encryption message was written.");
            }
            invalidMessageInput(messageInput);
            if (messageInput.charAt(messageInput.length() - 1) == ' ') {
                messageInput = messageInput.substring(0, messageInput.length() - 1);
            }
            String messageOutput = AppController.getModelMain().getMessageAndProcessIt(messageInput, true);
            contestButtonsVBox.setDisable(false);

            new Alert(Alert.AlertType.CONFIRMATION, "Processed message: " + messageInput + " -> " + messageOutput).show();
            encryptionOutputTextField.setText(messageOutput);
            mainController.updateMachineStateAndDictionary(AppController.getModelMain().getCurrentMachineStateAsString());
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
    void resetInputActionListener() {
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
        encryptionOutputTextField.setText("NaN");
    }

    public void updateLabelTextsToEmpty() {
        encryptionInputTextField.setText("");
    }


    public void updateStylesheet(Number num) {
        mainScrollPane.getStylesheets().remove(0);
        if (num.equals(0)) {
            mainScrollPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("contest/contestStyleOne.css")).toString());
        } else if (num.equals(1)) {
            mainScrollPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("contest/contestStyleTwo.css")).toString());
        } else {
            mainScrollPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("contest/contestStyleThree.css")).toString());
        }
    }

    public void bindTaskToUIComponents(Task<Boolean> aTask, Runnable onFinish) {
        // task cleanup upon finish
        aTask.valueProperty().addListener((observable, oldValue, newValue) -> onTaskFinished(Optional.ofNullable(onFinish)));
    }

    public void unbindTaskFromUIComponents(String timeElapsed, boolean bool) {
        toggleTaskButtons(false);
        if (bool) {
            this.timeElapsed = timeElapsed;
        }
    }

    public void onTaskFinished(Optional<Runnable> onFinish) {
        onFinish.ifPresent(Runnable::run);
    }

    private void dmOnFinished() {
        // TODO: create dynamic logout button and add it
        // TODO: implement logout button logic
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