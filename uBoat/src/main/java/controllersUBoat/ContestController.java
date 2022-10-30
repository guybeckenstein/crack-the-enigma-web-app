package controllersUBoat;

import impl.Trie;
import impl.models.MachineStateModel;
import interfaces.TrieInterface;
import jar.common.rawData.Candidate;
import jar.common.rawData.battlefieldContest.AlliesData;
import jar.enigmaEngine.exceptions.InvalidCharactersException;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import timerTasks.contest.pre.ActiveTeamsTimerTask;
import timerTasks.contest.pre.StartContestTimerTask;
import timerTasks.contest.start.ContestFinishedTimerTask;
import timerTasks.contest.start.FinalCandidatesTimerTask;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static http.Base.BASE_URL;
import static http.Base.HTTP_CLIENT;

// Third screen
public class ContestController {
    // Buttons component
    @FXML public Button readyButton;
    @FXML public Button logoutButton;
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
    private TrieInterface dictionaryTrie;
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
    private Timer activeTeamsTimer;
    // DM Output - table view
    @FXML private TableView<Candidate> finalCandidatesTableView;
    @FXML private TableColumn<Candidate, String> configurationColumn;
    @FXML private TableColumn<Candidate, String> wordsColumn;
    @FXML private TableColumn<Candidate, String> timeColumn;
    @FXML private TableColumn<Candidate, String> alliesColumn;
    private Timer finalCandidatesTimer;
    // Contest itself
    private boolean contestStarted;
    private Timer startContestTimer;
    private Timer contestFinishedTimer;


    public ContestController() {
        machineStatesConsole = new MachineStateModel(); // Model
    }

    @FXML
    private void initialize() {
        initKeyboardInput();
        initDictionaryTrie();
        firstMachineStateLabel.textProperty().bind(machineStatesConsole.firstMachineStateProperty());
        currentMachineStateLabel.textProperty().bind(machineStatesConsole.currentMachineStateProperty());

        initActiveTeamsTableView();
        initCandidatesTableView();

        readyButton.setMouseTransparent(true);
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
        activeTeamsTableView.getItems().addListener((ListChangeListener<AlliesData>) (c -> {
            c.next();
            final int size = activeTeamsTableView.getItems().size();
            if (size > 0) {
                activeTeamsTableView.scrollTo(size - 1);
            }
        }));
    }
    private void initCandidatesTableView() {
        configurationColumn.setCellValueFactory(new PropertyValueFactory<>("machineConfiguration"));
        wordsColumn.setCellValueFactory(new PropertyValueFactory<>("dictionaryWordsMessage"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeElapsed"));
        alliesColumn.setCellValueFactory(new PropertyValueFactory<>("allies"));
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

    public void initializeContestScreen() {
        // Initialize Timers
        activeTeamsTimer = new Timer(true);
        finalCandidatesTimer = new Timer(true);
        startContestTimer = new Timer(true);
        contestFinishedTimer = new Timer(true);

        contestStarted = false;

        ActiveTeamsTimerTask myAgentsTimerTask = new ActiveTeamsTimerTask(this); // Extends TimerTask
        activeTeamsTimer.scheduleAtFixedRate(myAgentsTimerTask, 0, 500);
        StartContestTimerTask startContestTimerTask = new StartContestTimerTask(this); // Extends TimerTask
        startContestTimer.scheduleAtFixedRate(startContestTimerTask, 0, 500);
    }

    public String getUsername() {
        return mainController.getUBoatUsername();
    }

    @FXML
    void readyAction() {
        updateServerYouAreReady();
        encryptionInputTextField.setText("");
        // Update transparency settings
        keyboardInputVBox.setMouseTransparent(true);
        readyButton.setMouseTransparent(true);
        searchVBox.setMouseTransparent(true);
        searchDictionaryWordsListView.setMouseTransparent(true);
    }

    @SuppressWarnings("unused")
    private void updateServerYouAreReady() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/uboat/contest";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("username", getUsername());
        urlBuilder.addQueryParameter("input", encryptionInputTextField.getText());
        String finalUrl = urlBuilder.build().toString();

        // Request + body + response
        try {
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create("", mediaType);

            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("POST", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("ERROR: " + e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        System.out.println("Server notified, " + getUsername() + " UBoat is ready!");
                        Platform.runLater(() -> sendEncryptedMessageToAllies());
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void sendEncryptedMessageToAllies() {
        RequestBody body = new FormBody.Builder() // Create request body
                .add("username", getUsername())
                .add("message", encryptionOutputTextField.getText())
                .build();
        Request request = new Request.Builder() // Create request object
                .url(BASE_URL + "/uboat/set-message")
                .post(body)
                .build();
        Call call = HTTP_CLIENT.newCall(request); // Create a Call object

        call.enqueue(new Callback() { // Execute a call (Asynchronous)
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("ERROR: " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (ResponseBody ignoredResponseBody = response.body()) {
                    if (response.isSuccessful()) {
                        System.out.println("Added UBoat encryption message to server"); // log
                    }
                }
            }
        });
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

            new Alert(Alert.AlertType.CONFIRMATION, "Processed message: " + messageInput + " -> " + messageOutput).show();
            readyButton.setMouseTransparent(false);
            encryptionOutputTextField.setText(messageOutput);
            machineStatesConsole.setCurrentMachineState(AppController.getModelMain().getCurrentMachineStateAsString());
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
        encryptionInputTextField.setText("");
        searchDictionaryWordsListView.getSelectionModel().clearSelection();
        readyButton.setMouseTransparent(true);

        new Alert(Alert.AlertType.INFORMATION, "Machine state has been successfully reset.").show();
        machineStatesConsole.setCurrentMachineState(machineStatesConsole.getFirstMachineState());
        encryptionOutputTextField.setText("NaN");
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

    public void updateActiveTeamsTableView(Collection<AlliesData> allCurrent) {
        if (allCurrent != null) {
            activeTeamsTableView.getItems().clear();
            activeTeamsTableView.getItems().addAll(allCurrent);
        }
    }

    public void startContestMode() {
        try {
            activeTeamsTimer.cancel();
            Thread.sleep(100);
            startContestTimer.cancel();
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        contestStarted = true;
        encryptionInputTextField.setText("");
        System.out.println("Start contest mode...");

        addContestToServer();
        initializeContestTimerTasks();
    }

    @SuppressWarnings("unused")
    private void addContestToServer() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/uboat/get-candidates";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("username", getUsername());
        String finalUrl = urlBuilder.build().toString();

        // Request + body + response
        try {
            RequestBody body = new FormBody.Builder() // Create request body
                    .build();
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("PUT", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        System.out.println("Successfully created template for FinalCandidatesTableView in server!");
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    // Updates candidatesTableView, according to the Allies' teams
    private void initializeContestTimerTasks() {
        FinalCandidatesTimerTask finalCandidatesTimerTask = new FinalCandidatesTimerTask(this); // Extends TimerTask
        finalCandidatesTimer.scheduleAtFixedRate(finalCandidatesTimerTask, 0, 500);
        ContestFinishedTimerTask contestFinishedTimerTask = new ContestFinishedTimerTask(this); // Extends TimerTask
        contestFinishedTimer.scheduleAtFixedRate(contestFinishedTimerTask, 0, 500);
    }

    public void updateFinalCandidatesTableView(Queue<Candidate> currentCandidates) {
        Candidate selectedItem = finalCandidatesTableView.getSelectionModel().getSelectedItem();
        finalCandidatesTableView.getItems().clear();
        finalCandidatesTableView.getItems().addAll(currentCandidates);
        finalCandidatesTableView.getSelectionModel().select(selectedItem);
    }

    // Stopped when an Allies' team finds the original message ONLY (using List.equals() method)
    public void stopContest(String winningAllies) {
        finalCandidatesTimer.cancel();
        contestFinishedTimer.cancel();
        if (!winningAllies.trim().isEmpty()) {
            resetBattlefield();
            // Show message to UBoat client
            new Alert(Alert.AlertType.INFORMATION, "Contest has finished.\n" +
                    "The winner is " + winningAllies.trim()).showAndWait();
        }
        emptyBattlefieldContenders();
        resetNodes();
    }

    // Removes Allies from contest
    private void emptyBattlefieldContenders() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/uboat/get-allies";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("username", getUsername());
        String finalUrl = urlBuilder.build().toString();

        // Request + body + response
        try {
            RequestBody body = new FormBody.Builder() // Create request body
                    .build();
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("POST", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Unable to remove allies from " + getUsername() + "'s battlefield.");
                    System.out.println(e.getLocalizedMessage());
                }

                @SuppressWarnings("unused")
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        System.out.println("Successfully removed allies from " + getUsername() + "'s battlefield.");
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void resetNodes() {
        searchDictionaryWordsListView.getSelectionModel().select(0);
        encryptionInputTextField.setText("");
        encryptionOutputTextField.setText("");
        // Update transparency settings
        keyboardInputVBox.setMouseTransparent(false);
        readyButton.setMouseTransparent(true);
        searchVBox.setMouseTransparent(false);
        searchDictionaryWordsListView.setMouseTransparent(false);
        // Empty TableViews
        activeTeamsTableView.getItems().clear();
        finalCandidatesTableView.getItems().clear();
    }

    @SuppressWarnings("unused")
    private void resetBattlefield() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/uboat/reset-battlefield";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("username", getUsername());
        String finalUrl = urlBuilder.build().toString();

        // Request + body + response
        try {
            RequestBody body = new FormBody.Builder() // Create request body
                    .build();
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("PUT", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Unable to reset battlefield's details.");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        System.out.println("Successfully reset battlefield's details!");
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void logoutAction() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/uboat/remove";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("username", getUsername());
        String finalUrl = urlBuilder.build().toString();

        // Request + body + response
        try {
            RequestBody body = new FormBody.Builder() // Create request body
                    .build();

            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("POST", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("ERROR: Failing to logout user " + getUsername() + "!");
                    System.out.println(e.getLocalizedMessage());
                }

                @SuppressWarnings("unused")
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        if (contestStarted) {
                            System.out.println(getUsername() + " has logged out during contest, will be redirected to login screen.");
                            Platform.runLater(() -> stopContest(""));
                        } else {
                            System.out.println(getUsername() + " has logged out before contest, will be redirected to login screen.");
                            Platform.runLater(() -> {
                                if (contestStarted) {
                                    finalCandidatesTimer.cancel();
                                    contestFinishedTimer.cancel();
                                } else {
                                    activeTeamsTimer.cancel();
                                    startContestTimer.cancel();
                                }
                                resetNodes();
                                mainController.switchToLoginScreen();
                            });
                        }
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}