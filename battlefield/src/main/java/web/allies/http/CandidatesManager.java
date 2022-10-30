package web.allies.http;

import jar.common.rawData.battlefieldContest.TeamCandidates;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CandidatesManager {
    private final Map<String, LinkedList<TeamCandidates>> alliesCandidatesMap; // Map<Allies' Username, List of Candidates>
    private final Map<String, Integer> currentIndex; // Map<Allies' username, Index of taken candidates>
    public CandidatesManager() {
        alliesCandidatesMap = new ConcurrentHashMap<>();
        currentIndex = new ConcurrentHashMap<>();
    }
    public void addAlliesToContest(String alliesUsername) {
        alliesCandidatesMap.put(alliesUsername, new LinkedList<>());
        currentIndex.put(alliesUsername, 0);
    }

    public void removeAlliesFromContest(String alliesUsername) {
        alliesCandidatesMap.remove(alliesUsername);
        currentIndex.remove(alliesUsername);
    }
    /** Agent client **/
    // Each Agent sends his list of candidates
    public synchronized void addCandidates(String alliesUsername, LinkedList<TeamCandidates> alliesCandidatesQueue) {
        alliesCandidatesMap.get(alliesUsername).addAll(alliesCandidatesQueue);
    }
    /** Allies client **/
    public LinkedList<TeamCandidates> getNewCandidates(String alliesUsername) {
        LinkedList<TeamCandidates> newCandidates = new LinkedList<>();
        for (int i = currentIndex.get(alliesUsername); i < alliesCandidatesMap.get(alliesUsername).size(); i++) {
            newCandidates.add(alliesCandidatesMap.get(alliesUsername).get(i));
        }
        currentIndex.put(alliesUsername, alliesCandidatesMap.size());
        return newCandidates;
    }
}
