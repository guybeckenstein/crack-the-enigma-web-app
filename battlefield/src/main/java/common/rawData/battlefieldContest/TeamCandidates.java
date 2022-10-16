package common.rawData.battlefieldContest;

import javafx.beans.property.SimpleStringProperty;

public class TeamCandidates {
    private final SimpleStringProperty candidateMessage;
    private final SimpleStringProperty candidateUsername; // Agent username
    private final SimpleStringProperty candidateConfiguration;

    public TeamCandidates(String candidateMessage, String candidateUsername, String candidateConfiguration) {
        this.candidateMessage = new SimpleStringProperty(candidateMessage);
        this.candidateUsername = new SimpleStringProperty(candidateUsername);
        this.candidateConfiguration = new SimpleStringProperty(candidateConfiguration);
    }

    public String getCandidateMessage() {
        return candidateMessage.get();
    }

    public SimpleStringProperty candidateMessageProperty() {
        return candidateMessage;
    }

    public void setCandidateMessage(String candidateMessage) {
        this.candidateMessage.set(candidateMessage);
    }

    public String getCandidateUsername() {
        return candidateUsername.get();
    }

    public SimpleStringProperty candidateUsernameProperty() {
        return candidateUsername;
    }

    public void setCandidateUsername(String candidateUsername) {
        this.candidateUsername.set(candidateUsername);
    }

    public String getCandidateConfiguration() {
        return candidateConfiguration.get();
    }

    public SimpleStringProperty candidateConfigurationProperty() {
        return candidateConfiguration;
    }

    public void setCandidateConfiguration(String candidateConfiguration) {
        this.candidateConfiguration.set(candidateConfiguration);
    }
}
