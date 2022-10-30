package jar.common.rawData.battlefieldContest;

import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings("unused")
public class AlliesData {
    private final SimpleStringProperty username; // Allies username
    private final SimpleStringProperty totalAgents;
    private final SimpleStringProperty taskSize;

    public AlliesData(String username, int totalAgents, int taskSize) {
        this.username = new SimpleStringProperty(username);
        this.totalAgents = new SimpleStringProperty(Integer.toString(totalAgents));
        this.taskSize = new SimpleStringProperty(Integer.toString(taskSize));
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
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
