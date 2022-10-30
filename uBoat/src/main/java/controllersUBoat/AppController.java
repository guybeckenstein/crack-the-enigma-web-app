package controllersUBoat;

import impl.models.MainModel;
import interfaces.Input;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AppController {
    // Main component
    static private Input mainModelApp;
    @FXML private BorderPane mainBorderPane; // For stylesheet purposes
    // Sub components
    @FXML private ScrollPane loginComponent;
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
    }

    public static Input getModelMain() {
        return mainModelApp;
    }

    public void reset() {
        machineComponentController.reset();
    }
    public void updateMachineScreen(List<String> choiceBoxItems, String numberOfRotors, String numberOfReflectors) {
        machineComponentController.updateScreen(choiceBoxItems, numberOfRotors, numberOfReflectors);
    }
    public void initializeMachineStates(String machineStateConsoleString) {
        contestComponentController.initializeMachineStates(machineStateConsoleString);
    }

    public void emptyLabelText() {
        headerComponentController.emptyLabelText();
        machineComponentController.emptyDetailsLabelText();
    }

    public void updateDictionary() {
        contestComponentController.updateDictionary();
    }
    public void switchToContestScreen() {
        contestComponent.toFront();
        contestComponentController.initializeContestScreen();
        headerComponentController.switchToContestScreen();
        headerComponentController.setLoadButtonMouseTransparency(true);
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
    public String getUBoatUsername() {
        return headerComponentController.getUsername();}
    public void switchToMachineScreen() {
        machineComponent.toFront();
        machineComponentController.reset();
        headerComponentController.setLoadButtonMouseTransparency(false);
    }

    // When UBoat quits
    public void switchToLoginScreen() {
        // Reset header
        headerComponentController.switchToLoginScreen();
        // Reset login screen
        loginComponent.toFront();
        loginComponentController.reset();
    }
}