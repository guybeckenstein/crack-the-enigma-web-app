package common.rawData.agents;

import javafx.beans.property.SimpleStringProperty;

public class AgentsRawData {
    private final SimpleStringProperty agentName; // Agent username
    private final SimpleStringProperty tasksReceived;
    private final SimpleStringProperty tasksToPerform;
    private final SimpleStringProperty candidatesCreated;

    public AgentsRawData(String agentName, String tasksReceived, String tasksToPerform, String candidatesCreated) {
        this.agentName = new SimpleStringProperty(agentName);
        this.tasksReceived = new SimpleStringProperty(tasksReceived);
        this.tasksToPerform = new SimpleStringProperty(tasksToPerform);
        this.candidatesCreated = new SimpleStringProperty(candidatesCreated);
    }

    public String getAgentName() {
        return agentName.get();
    }

    public SimpleStringProperty agentNameProperty() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName.set(agentName);
    }

    public String getTasksReceived() {
        return tasksReceived.get();
    }

    public SimpleStringProperty tasksReceivedProperty() {
        return tasksReceived;
    }

    public void setTasksReceived(String tasksReceived) {
        this.tasksReceived.set(tasksReceived);
    }

    public String getTasksToPerform() {
        return tasksToPerform.get();
    }

    public SimpleStringProperty tasksToPerformProperty() {
        return tasksToPerform;
    }

    public void setTasksToPerform(String tasksToPerform) {
        this.tasksToPerform.set(tasksToPerform);
    }

    public String getCandidatesCreated() {
        return candidatesCreated.get();
    }

    public SimpleStringProperty candidatesCreatedProperty() {
        return candidatesCreated;
    }

    public void setCandidatesCreated(String candidatesCreated) {
        this.candidatesCreated.set(candidatesCreated);
    }
}
