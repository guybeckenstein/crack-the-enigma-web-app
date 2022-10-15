package impl.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MachineStateModel {
    // Screen 3
    private final StringProperty firstMachineState;
    private final StringProperty currentMachineState;

    public MachineStateModel() {
        this.firstMachineState = new SimpleStringProperty(this, "firstMachineState", "NaN");
        this.currentMachineState = new SimpleStringProperty(this, "currentMachineState", "NaN");
    }

    public String getFirstMachineState() {
        return firstMachineState.get();
    }

    public StringProperty firstMachineStateProperty() {
        return firstMachineState;
    }

    public void setFirstMachineState(String firstMachineState) {
        this.firstMachineState.set(firstMachineState);
    }

    public StringProperty currentMachineStateProperty() {
        return currentMachineState;
    }

    public void setCurrentMachineState(String currentMachineState) {
        this.currentMachineState.set(currentMachineState);
    }

}
