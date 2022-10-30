package jar.clients.agent;

public class Agent {
    private final String username;
    private final int totalThreads;
    private final int tasksCapacity;

    public Agent(String username, int totalThreads, int tasksCapacity) {
        this.username = username;
        this.totalThreads = totalThreads;
        this.tasksCapacity = tasksCapacity;
    }

    public String getUsername() {
        return username;
    }

    public int getTotalThreads() {
        return totalThreads;
    }

    public int getTasksCapacity() {
        return tasksCapacity;
    }

    @Override
    public String toString() {
        return "<" + username + ", " + totalThreads + ", " + tasksCapacity + ">";
    }
}
