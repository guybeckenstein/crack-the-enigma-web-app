package web.allies.http;

import jar.common.rawData.agents.AlliesRawData;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContestManager {
    private final Map<String, AlliesRawData> alliesContestMap; // Map<Allies' Username, Allies Data>

    public ContestManager() {
        alliesContestMap = new ConcurrentHashMap<>();
    } // Thread safe

    /** Related to Agent **/
    public void addAllies(String alliesUsername, AlliesRawData data) {
        alliesContestMap.put(alliesUsername, data);
    }

    public void removeAllies(String alliesUsername) { // TODO - after contest ends
        alliesContestMap.remove(alliesUsername);
    }

    public AlliesRawData getAlliesRawDataByUsername(String alliesUsername) {
        return Collections.unmodifiableMap(alliesContestMap).get(alliesUsername);
    }


}
