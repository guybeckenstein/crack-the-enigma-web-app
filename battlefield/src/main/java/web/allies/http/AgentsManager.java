package web.allies.http;

import jar.clients.agent.Agent;

import java.util.*;

public class AgentsManager {
    private final Map<String, List<Agent>> alliesAgentsMap; // Map< Allies' username, List<Agents raw data> >
    public AgentsManager() {
        alliesAgentsMap = new HashMap<>();
    }
    public synchronized Map<String, List<Agent>> getMap() {
        return Collections.unmodifiableMap(alliesAgentsMap);
    }
    /** Related to Allies **/
    public synchronized void addAllies(String username) {
        alliesAgentsMap.put(username, new ArrayList<>());
    }
    // Only if Allies quits during contest screen
    public synchronized void removeAllies(String username) {
        alliesAgentsMap.remove(username);
    }
    /** Related to Agent **/
    public synchronized void addAgent(String allies, Agent agent) {
        alliesAgentsMap.get(allies).add(agent);
    }
    // Only if Agent quits during contest
    public synchronized void removeAgent(String alliesUsername, String agentUsername) {
        List<Agent> agents = alliesAgentsMap.get(alliesUsername);
        for (Agent agent : agents) {
            if (agent.getUsername().equals(agentUsername)) {
                agents.remove(agent);
                return;
            }
        }
        throw new RuntimeException("No agents found");
    }
}
