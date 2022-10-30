package jar.clients.battlefield;

@SuppressWarnings("unused")
public class Battlefield {
    public enum Status {
        Available, Unavailable
    }
    private final String title;
    private final String username;
    private Status status;
    private final Difficulty difficulty;
    private String teamsRegistered;
    private int currentRegisteredTeams;
    private final int maximumTeams;

    public Battlefield(String title, String username, Status status, Difficulty difficulty, int maximumTeams) {
        this.title = title;
        this.username = username;
        this.status = status;
        this.difficulty = difficulty;
        this.maximumTeams = maximumTeams;
        currentRegisteredTeams = 0;
        this.teamsRegistered = getFormattedRegisteredTeams(currentRegisteredTeams);
    }

    private String getFormattedRegisteredTeams(int i) {
        return i + "/" + maximumTeams;
    }

    public synchronized void increaseRegisteredTeams() {
        this.teamsRegistered = getFormattedRegisteredTeams(++currentRegisteredTeams);
    }

    public synchronized void decreaseRegisteredTeams() {
        this.teamsRegistered = getFormattedRegisteredTeams(--currentRegisteredTeams);
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) { this.status = status; }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public String getTeamsRegistered() {
        return teamsRegistered;
    }

    public int getCurrentRegisteredTeams() {
        return currentRegisteredTeams;
    }

    public int getMaximumTeams() {
        return maximumTeams;
    }

    public void reset() {
        setStatus(Status.Available);
        currentRegisteredTeams = 0;
        this.teamsRegistered = getFormattedRegisteredTeams(currentRegisteredTeams);
    }

    @Override
    public String toString() {
        return "<" + title + ", " + username + ", " + status + ", " + difficulty + ", " + teamsRegistered + ">";
    }
}
