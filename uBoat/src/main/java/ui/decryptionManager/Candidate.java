package ui.decryptionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Candidate {
    private final SimpleStringProperty machineConfiguration;
    private final SimpleStringProperty dictionaryWords;
    private final SimpleStringProperty timeElapsed;
    private final SimpleStringProperty agent;

    public Candidate(String machineConfiguration, String dictionaryWords, String timeElapsed, String agent) {
        this.machineConfiguration = new SimpleStringProperty(machineConfiguration);
        this.dictionaryWords = new SimpleStringProperty(dictionaryWords);
        this.timeElapsed = new SimpleStringProperty(timeElapsed);
        this.agent = new SimpleStringProperty(agent);
    }

    public String getMachineConfiguration() {
        return machineConfiguration.get();
    }

    public SimpleStringProperty machineConfigurationProperty() {
        return machineConfiguration;
    }

    public void setMachineConfiguration(String machineConfiguration) {
        this.machineConfiguration.set(machineConfiguration);
    }

    public String getDictionaryWords() {
        return dictionaryWords.get();
    }

    public SimpleStringProperty dictionaryWordsProperty() {
        return dictionaryWords;
    }

    public void setDictionaryWords(String dictionaryWords) {
        this.dictionaryWords.set(dictionaryWords);
    }

    public String getTimeElapsed() {
        return timeElapsed.get();
    }

    public SimpleStringProperty timeElapsedProperty() {
        return timeElapsed;
    }

    public void setTimeElapsed(String timeElapsed) {
        this.timeElapsed.set(timeElapsed);
    }

    public String getAgent() {
        return agent.get();
    }

    public SimpleStringProperty agentProperty() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent.set(agent);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('<').append(getMachineConfiguration()).append('>');
        sb.append('<').append(getDictionaryWords()).append('>');
        sb.append('<').append(getTimeElapsed()).append('>');
        sb.append('<').append(getAgent()).append('>');

        return sb.toString();
    }
}
