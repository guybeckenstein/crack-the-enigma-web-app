package controllersUBoat.todo;

import controllersUBoat.AppController;
import jar.common.StartPrimaryStage;
import jar.common.rawData.configuration.RotorsRawData;
import jar.enigmaEngine.exceptions.InvalidCharactersException;
import jar.enigmaEngine.exceptions.InvalidPlugBoardException;
import jar.enigmaEngine.exceptions.InvalidReflectorException;
import jar.enigmaEngine.exceptions.InvalidRotorException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.*;

@SuppressWarnings("all")
public class ConfigurationController extends Application {

    // Rotors and their order - table
    @FXML private TableView<RotorsRawData> rotorsAndTheirOrderTableView;
    private ObservableList<RotorsRawData> data;

    @FXML private ChoiceBox<String> reflectorChoiceBox;

    @FXML private Label setCodeLabel;

    @FXML private Button setCodeButton;

    private Set<Character> abc;
    private int totalRotors = -1;
    private int currentUsedRotors = 0;

    public ConfigurationController() {
    }

    public void setAbc(Set<Character> abc) {
        this.abc = abc;
    }

    public void setTotalRotors(int totalRotors) {
        this.totalRotors = totalRotors;
    }

    public void setCurrentUsedRotors(int currentUsedRotors) {
        this.currentUsedRotors = currentUsedRotors;
    }

    @FXML
    private void initialize() {
        /** Test **/
        // Remove all values
        rotorsAndTheirOrderTableView.getColumns().clear();
        data = FXCollections.observableArrayList();
        // Add abc set
        abc = new TreeSet<>();
        for (char ch : "ABCDEFGHIJKLMNOPQRSTUVWXYZ !?'".toCharArray()) {
            abc.add(ch);
        }
        // Add rotors limits
        setTotalRotors(5);
        setCurrentUsedRotors(0);
        // Add rotors to table view
        rotorsAndTheirOrderTableView.getColumns().add(createNewRotorColumn()); // Rotor #1
        rotorsAndTheirOrderTableView.getColumns().add(createNewRotorColumn()); // Rotor #2
        rotorsAndTheirOrderTableView.setItems(data);
        // Add reflectors values
        createReflectorChoiceBox();
    }

    private void createReflectorChoiceBox() {
        List<String> reflectorsList = new ArrayList<>();
        reflectorsList.add("I");
        reflectorsList.add("II");
        reflectorsList.add("III");
        reflectorChoiceBox.setItems(FXCollections.observableArrayList(FXCollections.observableArrayList(reflectorsList)));
        reflectorChoiceBox.setValue(reflectorsList.get(0));
    }

    /** Add / remove columns **/
    @FXML
    void addRotorColumnAction(ActionEvent event) {
        if (currentUsedRotors < totalRotors) {
            rotorsAndTheirOrderTableView.getColumns().add(createNewRotorColumn());
        } else {
            System.out.println("No more rotors");
        }
    }

    @NotNull
    private TableColumn<RotorsRawData, ComboBox<Character>> createNewRotorColumn() {
        if (currentUsedRotors == 0) {
            data.add(new RotorsRawData(abc));
        }
        TableColumn<RotorsRawData, ComboBox<Character>> rotorColumn = new TableColumn<>(Integer.toString(++currentUsedRotors));
        rotorColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new StringConverter<ComboBox<Character>>() {
            @Override
            public String toString(ComboBox<Character> object) {
                return "Click to make ComboBox appear";
            }

            @Override
            public ComboBox<Character> fromString(String string) {
                return null;
            }
        }, data.get(0).getAbcCharacters()));
        return rotorColumn;
    }

    @FXML
    void removeSelectedRotorColumnAction(ActionEvent event) {
        if (2 < currentUsedRotors) // Must have at least two rotors
        {
            rotorsAndTheirOrderTableView.getColumns().remove(--currentUsedRotors);
        } else {
            System.out.println("Must have at least two rotors");
        }
    }

    /** Initialize configuration **/
    @FXML
    void setConfigurationRandomly() {
        // TODO: add
    }

    @SuppressWarnings("unchecked")
    @FXML
    void getConfigurationFromUser() {
        Set<String> rotorsInput = new HashSet<>();
        final String[] startingPositions = {""};
        ObservableList<TableColumn<RotorsRawData, ?>> rotorsColumns = rotorsAndTheirOrderTableView.getColumns();
        rotorsColumns.forEach((column) -> {
            rotorsInput.add(column.getText());
            startingPositions[0] = startingPositions[0] + ((ChoiceBox<Character>)column.getCellData(0)).getValue();
        });
        String rotors = String.join(",", rotorsInput);
        String reflectorID = reflectorChoiceBox.getValue();
        try {
            AppController.getModelMain().initializeEnigmaCodeManually(rotors, startingPositions[0], "", reflectorID);
        } catch (InvalidCharactersException | InvalidPlugBoardException | InvalidRotorException | InvalidReflectorException e) {
            e.printStackTrace();
        }
    }





    /** Test functions **/

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = getClass().getClassLoader().getResource("machine/configuration/configuration.fxml"); // Get fxml file resource
        StartPrimaryStage.start(primaryStage, url);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
