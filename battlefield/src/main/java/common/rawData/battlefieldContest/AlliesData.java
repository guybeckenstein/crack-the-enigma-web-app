package common.rawData.battlefieldContest;

import javafx.beans.property.SimpleStringProperty;

public class AlliesData {
    private final SimpleStringProperty username; // Allies username
    private final SimpleStringProperty totalAgents;
    private final SimpleStringProperty taskSize;

    public AlliesData(String username, String totalAgents, String taskSize) {
        this.username = new SimpleStringProperty(username);
        this.totalAgents = new SimpleStringProperty(totalAgents);
        this.taskSize = new SimpleStringProperty(taskSize);
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

    public String getTotalAgents() {
        return totalAgents.get();
    }

    public SimpleStringProperty totalAgentsProperty() {
        return totalAgents;
    }

    public void setTotalAgents(String totalAgents) {
        this.totalAgents.set(totalAgents);
    }

    public String getTaskSize() {
        return taskSize.get();
    }

    public SimpleStringProperty taskSizeProperty() {
        return taskSize;
    }

    public void setTaskSize(String taskSize) {
        this.taskSize.set(taskSize);
    }
}
