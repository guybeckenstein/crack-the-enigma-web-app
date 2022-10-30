package jar.common.rawData.agents;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings("unused")
public class AgentsRawData {
    private final SimpleStringProperty agentName; // Agent username - From Dm task
    private final SimpleLongProperty tasksFinished; // From contest controller
    private final SimpleIntegerProperty tasksToPerform; // From Dm Task
    private final SimpleLongProperty candidatesCreated; // From contest controller

    public AgentsRawData(String agentName, long tasksFinished, int tasksToPerform, long candidatesCreated) {
        this.agentName = new SimpleStringProperty(agentName);
        this.tasksFinished = new SimpleLongProperty(tasksFinished);
        this.tasksToPerform = new SimpleIntegerProperty(tasksToPerform);
        this.candidatesCreated = new SimpleLongProperty(candidatesCreated);
    }

    public String getAgentName() {
        return agentName.get();
    }

    public SimpleStringProperty agentNameProperty() {
        return agentName;
    }

    public long getTasksFinished() {
        return tasksFinished.get();
    }

    public SimpleLongProperty tasksFinishedProperty() {
        return tasksFinished;
    }

    public int getTasksToPerform() {
        return tasksToPerform.get();
    }

    public SimpleIntegerProperty tasksToPerformProperty() {
        return tasksToPerform;
    }

    public long getCandidatesCreated() {
        return candidatesCreated.get();
    }

    public SimpleLongProperty candidatesCreatedProperty() {
        return candidatesCreated;
    }
}
