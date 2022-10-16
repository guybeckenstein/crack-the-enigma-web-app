package controllersUBoat;

import impl.models.MainModel;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AppController {
    // Main component
    static private MainModel mainModelApp;
    @FXML private BorderPane mainBorderPane; // For stylesheet purposes
    // Sub components
    @FXML private HBox headerComponent;
    @FXML private ScrollPane machineComponent;
    @FXML private ScrollPane contestComponent;
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private LoginController loginComponentController;
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private HeaderController headerComponentController;
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private MachineController machineComponentController;
    @SuppressWarnings({"UnusedDeclaration"}) @FXML private ContestController contestComponentController;

    public AppController() {
        mainModelApp = new MainModel();
    }

    @FXML
    public void initialize() {
        if (loginComponentController != null && headerComponentController != null
                && machineComponentController != null && contestComponentController != null) {
            loginComponentController.setMainController(this);
            headerComponentController.setMainController(this);
            machineComponentController.setMainController(this);
            contestComponentController.setMainController(this);
        }
        headerComponent.setMouseTransparent(true);
    }

    public static MainModel getModelMain() {
        return mainModelApp;
    }

    public void updateMachineStateAndDictionary(String currentMachineState) {
        contestComponentController.updateMachineStateAndDictionary(currentMachineState);
    }

    public void reset() {
        machineComponentController.reset();
    }
    public void resetScreens(boolean bool, Object controller) {
        contestComponentController.resetMachineStateAndEnigmaOutput(bool, controller);
    }
    public void updateMachineScreen(List<String> choiceBoxItems, String numberOfRotors, String numberOfReflectors) {
        machineComponentController.updateScreen(choiceBoxItems, numberOfRotors, numberOfReflectors);
    }
    public void initializeMachineStates(String machineStateConsoleString) {
        contestComponentController.initializeMachineStates(machineStateConsoleString);
    }

    public void updateLabelTextsToEmpty() {
        headerComponentController.updateLabelTextsToEmpty();
        machineComponentController.updateLabelTextsToEmpty();
        contestComponentController.updateLabelTextsToEmpty();
    }

    public void updateDictionary() {
        contestComponentController.updateDictionary();
    }
    public void changeToContestScreen() {
        contestComponent.toFront();
        headerComponentController.changeToContestScreen();
    }


    public Set<String> getDictionary() {
        return mainModelApp.getWordsDictionary();
    }

    public void updateStylesheet(Number num) {
        mainBorderPane.getStylesheets().remove(0);
        if (num.equals(0)) {
            mainBorderPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("main/generalStyleOne.css")).toString());
        } else if (num.equals(1)) {
            mainBorderPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("main/generalStyleTwo.css")).toString());
        } else {
            mainBorderPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("main/generalStyleThree.css")).toString());
        }
        headerComponentController.updateStylesheet(num);
        machineComponentController.updateStylesheet(num);
        contestComponentController.updateStylesheet(num);
    }

    public void updateUBoatUsername(String username) {
        headerComponentController.updateUsername(username);
    }
    public void switchToMachineScreen() {
        machineComponent.toFront();
        headerComponentController.enableScreen();
    }

    public HBox getHeaderComponent() {
        return headerComponent;
    }
}