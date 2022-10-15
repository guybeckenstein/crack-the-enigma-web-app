package impl.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Specifications {
    // Machine controller specs
    private final StringProperty rotorsAmountInMachineXML;
    private final StringProperty currentRotorsInMachine;
    private final StringProperty reflectorsAmountInMachineXML;
    private final StringProperty currentReflectorInMachine;

    public Specifications() {
        this.rotorsAmountInMachineXML = new SimpleStringProperty(this, "rotorsAmountInMachineXML", "NaN");
        this.currentRotorsInMachine = new SimpleStringProperty(this, "currentRotorsInMachine", "NaN");
        this.reflectorsAmountInMachineXML = new SimpleStringProperty(this, "reflectorsAmountInMachineXML", "NaN");
        this.currentReflectorInMachine = new SimpleStringProperty(this, "currentReflectorInMachine", "NaN");
    }

    public void setRotorsAmountInMachineXML(String rotorsAmountInMachineXML) {
        this.rotorsAmountInMachineXML.set(rotorsAmountInMachineXML);
    }

    public void setCurrentRotorsInMachine(String currentRotorsInMachine) {
        this.currentRotorsInMachine.set(currentRotorsInMachine);
    }

    public void setReflectorsAmountInMachineXML(String reflectorsAmountInMachineXML) {
        this.reflectorsAmountInMachineXML.set(reflectorsAmountInMachineXML);
    }

    public void setCurrentReflectorInMachine(String currentReflectorInMachine) {
        this.currentReflectorInMachine.set(currentReflectorInMachine);
    }

    public StringProperty rotorsAmountInMachineXMLProperty() {
        return rotorsAmountInMachineXML;
    }

    public StringProperty currentRotorsInMachineProperty() {
        return currentRotorsInMachine;
    }

    public StringProperty reflectorsAmountInMachineXMLProperty() {
        return reflectorsAmountInMachineXML;
    }

    public StringProperty currentReflectorInMachineProperty() {
        return currentReflectorInMachine;
    }
}
