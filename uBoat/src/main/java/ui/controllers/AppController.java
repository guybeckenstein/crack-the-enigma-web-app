package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import ui.impl.models.ModelMain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AppController {
    // Main component
    static private ModelMain modelMainApp;
    @FXML private BorderPane mainBorderPane;
    // Sub components
    @FXML private ScrollPane machineComponent;
    @FXML private ScrollPane contestComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private MachineController machineComponentController;
    @FXML private ContestController contestComponentController;

    public AppController() {
        modelMainApp = new ModelMain();
    }

    @FXML
    public void initialize() {
        if (headerComponentController != null && machineComponentController != null && contestComponentController != null) {
            headerComponentController.setMainController(this);
            machineComponentController.setMainController(this);
            contestComponentController.setMainController(this);
        }
    }

    public static ModelMain getModelMain() {
        return modelMainApp;
    }

    public void updateScreens(String currentMachineState) {
        machineComponentController.updateMachineStateAndStatus(currentMachineState);
        contestComponentController.updateMachineStateAndDictionary(currentMachineState);
    }

    public void reset() {
        machineComponentController.reset();
    }
    public void resetScreens(boolean bool, Object controller) {
        machineComponentController.resetMachineStateAndStatus();
        contestComponentController.resetMachineStateAndEnigmaOutput(bool, controller);
    }
    public void updateMachineScreen(List<String> choiceBoxItems, String numberOfRotors, String numberOfReflectors) {
        machineComponentController.updateScreen(choiceBoxItems, numberOfRotors, numberOfReflectors);
    }
    public void initializeMachineStates(String machineStateConsoleString) {
        contestComponentController.initializeMachineStates(machineStateConsoleString);
    }

    public void updateScreensDisability(boolean bool) {
        contestComponentController.setContestDisability(bool);
    }

    public void updateLabelTextsToEmpty() {
        headerComponentController.updateLabelTextsToEmpty();
        machineComponentController.updateLabelTextsToEmpty();
        contestComponentController.updateLabelTextsToEmpty();
    }

    public void updateDictionary() {
        contestComponentController.updateDictionary();
    }

    // Swap screens
    public void changeToMachineScreen() {
        machineComponent.toFront();
    }
    public void changeToContestScreen() {
        contestComponent.toFront();
    }


    public Set<String> getDictionary() {
        return modelMainApp.getWordsDictionary();
    }

    public void updateStylesheet(Number num) {
        mainBorderPane.getStylesheets().remove(0);
        if (num.equals(0)) {
            mainBorderPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("ui/main/generalStyleOne.css")).toString());
        } else if (num.equals(1)) {
            mainBorderPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("ui/main/generalStyleTwo.css")).toString());
        } else {
            mainBorderPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("ui/main/generalStyleThree.css")).toString());
        }
        headerComponentController.updateStylesheet(num);
        machineComponentController.updateStylesheet(num);
        contestComponentController.updateStylesheet(num);
    }
}