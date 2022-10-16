package common.rawData.battlefieldContest;

import javafx.beans.property.SimpleStringProperty;

public class ContestRawData {
    private final SimpleStringProperty title; // Battlefield name
    private final SimpleStringProperty username; // UBoat username
    private final SimpleStringProperty status; // "Available" or "Unavailable"
    private final SimpleStringProperty difficulty;
    private final SimpleStringProperty teamsRegistered;

    public ContestRawData(String title, String username, String status, String difficulty, String teamsRegistered) {
        this.title = new SimpleStringProperty(title);
        this.username = new SimpleStringProperty(username);
        this.status = new SimpleStringProperty(status);
        this.difficulty = new SimpleStringProperty(difficulty);
        this.teamsRegistered = new SimpleStringProperty(teamsRegistered);
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getDifficulty() {
        return difficulty.get();
    }

    public SimpleStringProperty difficultyProperty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty.set(difficulty);
    }

    public String getTeamsRegistered() {
        return teamsRegistered.get();
    }

    public SimpleStringProperty teamsRegisteredProperty() {
        return teamsRegistered;
    }

    public void setTeamsRegistered(String teamsRegistered) {
        this.teamsRegistered.set(teamsRegistered);
    }
}
