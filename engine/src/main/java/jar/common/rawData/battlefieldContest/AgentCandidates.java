package jar.common.rawData.battlefieldContest;

import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings("unused")
public class AgentCandidates {
    private final SimpleStringProperty candidateMessage;
    private final SimpleStringProperty candidateTask;
    private final SimpleStringProperty candidateConfiguration;

    public AgentCandidates(String candidateMessage, String candidateTask, String candidateConfiguration) {
        this.candidateMessage = new SimpleStringProperty(candidateMessage);
        this.candidateTask = new SimpleStringProperty(candidateTask);
        this.candidateConfiguration = new SimpleStringProperty(candidateConfiguration);
    }

    public String getCandidateMessage() {
        return candidateMessage.get();
    }

    public SimpleStringProperty candidateMessageProperty() {
        return candidateMessage;
    }

    public String getCandidateTask() {
        return candidateTask.get();
    }

    public SimpleStringProperty candidateTaskProperty() {
        return candidateTask;
    }

    public String getCandidateConfiguration() {
        return candidateConfiguration.get();
    }

    public SimpleStringProperty candidateConfigurationProperty() {
        return candidateConfiguration;
    }
}
