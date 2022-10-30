package jar.common.rawData.battlefieldContest;

import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings("unused")
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

    public String getCandidateUsername() {
        return candidateUsername.get();
    }

    public SimpleStringProperty candidateUsernameProperty() {
        return candidateUsername;
    }


    public String getCandidateConfiguration() {
        return candidateConfiguration.get();
    }

    public SimpleStringProperty candidateConfigurationProperty() {
        return candidateConfiguration;
    }
}
