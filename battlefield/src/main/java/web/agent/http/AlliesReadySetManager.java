package web.agent.http;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class AlliesReadySetManager {
    private final Set<String> readySet;
    public AlliesReadySetManager() {
        this.readySet = new HashSet<>();
    }

    public synchronized Set<String> getSet() { return Collections.unmodifiableSet(readySet); }
    public synchronized void addAllies(String alliesUsername) {
        readySet.add(alliesUsername);
    }

    public synchronized void removeAllies(String alliesUsername) {
        readySet.remove(alliesUsername);
    }

    public boolean isAlliesInSet(String alliesUsername) {
        return readySet.contains(alliesUsername);
    }
}
