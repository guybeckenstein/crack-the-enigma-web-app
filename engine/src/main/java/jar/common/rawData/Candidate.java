package jar.common.rawData;

import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings("unused")
public class Candidate {
    private final SimpleStringProperty machineConfiguration;
    private final SimpleStringProperty originalTask;
    private final SimpleStringProperty dictionaryWordsMessage;
    private final SimpleStringProperty timeElapsed;
    private final SimpleStringProperty allies;

    public Candidate(String machineConfiguration, String originalTask, String dictionaryWordsMessage, String timeElapsed, String allies) {
        this.machineConfiguration = new SimpleStringProperty(machineConfiguration);
        this.originalTask = new SimpleStringProperty(originalTask);
        this.dictionaryWordsMessage = new SimpleStringProperty(dictionaryWordsMessage);
        this.timeElapsed = new SimpleStringProperty(timeElapsed);
        this.allies = new SimpleStringProperty(allies);
    }

    public String getMachineConfiguration() {
        return machineConfiguration.get();
    }

    public SimpleStringProperty machineConfigurationProperty() {
        return machineConfiguration;
    }

    public String getOriginalTask() {
        return originalTask.get();
    }

    public SimpleStringProperty originalTaskProperty() {
        return originalTask;
    }

    public String getDictionaryWordsMessage() {
        return dictionaryWordsMessage.get();
    }

    public SimpleStringProperty dictionaryWordsMessageProperty() {
        return dictionaryWordsMessage;
    }

    public String getTimeElapsed() {
        return timeElapsed.get();
    }

    public SimpleStringProperty timeElapsedProperty() {
        return timeElapsed;
    }

    public String getAllies() {
        return allies.get();
    }

    public SimpleStringProperty alliesProperty() {
        return allies;
    }

    @Override
    public String toString() {
        return '<' + getMachineConfiguration() + '>' +
                '<' + getDictionaryWordsMessage() + '>' +
                '<' + getTimeElapsed() + '>' +
                '<' + getAllies() + '>';
    }
}
