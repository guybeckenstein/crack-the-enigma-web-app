package common.rawData.agents;

import javafx.beans.property.SimpleStringProperty;

public class TeamsAgentsData {
    private final SimpleStringProperty name; // UBoat username
    private final SimpleStringProperty totalThreads;
    private final SimpleStringProperty tasksCapacity;

    public TeamsAgentsData(String name, String totalThreads, String tasksCapacity) {
        this.name = new SimpleStringProperty(name);
        this.totalThreads = new SimpleStringProperty(totalThreads);
        this.tasksCapacity = new SimpleStringProperty(tasksCapacity);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getTotalThreads() {
        return totalThreads.get();
    }

    public SimpleStringProperty totalThreadsProperty() {
        return totalThreads;
    }

    public void setTotalThreads(String totalThreads) {
        this.totalThreads.set(totalThreads);
    }

    public String getTasksCapacity() {
        return tasksCapacity.get();
    }

    public SimpleStringProperty tasksCapacityProperty() {
        return tasksCapacity;
    }

    public void setTasksCapacity(String tasksCapacity) {
        this.tasksCapacity.set(tasksCapacity);
    }
}
