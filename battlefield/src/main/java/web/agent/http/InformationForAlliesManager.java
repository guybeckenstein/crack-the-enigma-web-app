package web.agent.http;

import jar.common.rawData.agents.AgentsRawData;

import java.util.HashMap;
import java.util.Map;

public class InformationForAlliesManager {
    private final Map<String, Map<String, AgentsRawData>> agentsOfAlliesDataMap; // Map< Allies' Username, Map< Agent's Username, Agents' data> >
    public InformationForAlliesManager() {
        agentsOfAlliesDataMap = new HashMap<>();
    }
    /** When an Allies client is registered to a contest **/
    public synchronized void addAllies(String alliesUsername) {
        agentsOfAlliesDataMap.put(alliesUsername, new HashMap<>());
    }
    /** When an Agent client updates his information **/
    public synchronized void addAgentData(String alliesUsername, String agentUsername, AgentsRawData data) {
        agentsOfAlliesDataMap.get(alliesUsername).put(agentUsername, data);
    }
    /** When an Allies client gets all information (using TimerTask) **/
    public Map<String, AgentsRawData> getAgentsDataMap(String alliesUsername) {
        return agentsOfAlliesDataMap.get(alliesUsername);
    }
    /** When an Agent client quits **/
    public synchronized void removeAgent(String alliesUsername, String agentUsername) {
        if (agentsOfAlliesDataMap.containsKey(alliesUsername)) {
            agentsOfAlliesDataMap.get(alliesUsername).remove(agentUsername);
        }
    }
}
