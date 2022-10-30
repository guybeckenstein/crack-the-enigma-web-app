package jar.common.rawData.agents;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings("unused")
public class AlliesRawData {
    private final SimpleStringProperty username;
    private final SimpleStringProperty title;
    private final SimpleLongProperty tasks;

    public AlliesRawData(String username, String title, long tasks) {
        this.username = new SimpleStringProperty(username);
        this.title = new SimpleStringProperty(title);
        this.tasks = new SimpleLongProperty(tasks);
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public long getTasks() {
        return tasks.get();
    }

    public SimpleLongProperty tasksProperty() {
        return tasks;
    }

    public void setTasks(long tasksAvailable) {
        tasks.set(tasksAvailable);
    }

}
