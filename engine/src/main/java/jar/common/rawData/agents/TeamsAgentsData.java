package jar.common.rawData.agents;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings("unused")
public class TeamsAgentsData {
    private final SimpleStringProperty name; // UBoat username
    private final SimpleIntegerProperty totalThreads;
    private final SimpleIntegerProperty tasksCapacity;

    public TeamsAgentsData(String name, int totalThreads, int tasksCapacity) {
        this.name = new SimpleStringProperty(name);
        this.totalThreads = new SimpleIntegerProperty(totalThreads);
        this.tasksCapacity = new SimpleIntegerProperty(tasksCapacity);
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

    public int getTotalThreads() {
        return totalThreads.get();
    }

    public SimpleIntegerProperty totalThreadsProperty() {
        return totalThreads;
    }

    public void setTotalThreads(int totalThreads) {
        this.totalThreads.set(totalThreads);
    }

    public int getTasksCapacity() {
        return tasksCapacity.get();
    }

    public SimpleIntegerProperty tasksCapacityProperty() {
        return tasksCapacity;
    }

    public void setTasksCapacity(int tasksCapacity) {
        this.tasksCapacity.set(tasksCapacity);
    }
}
