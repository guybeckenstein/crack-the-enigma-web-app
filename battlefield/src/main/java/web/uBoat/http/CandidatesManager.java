package web.uBoat.http;

import jar.common.rawData.Candidate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class CandidatesManager {

    private final Map<String, LinkedList<Candidate>> uBoatCandidatesMap; // Map<UBoat's username, List<Candidate>>
    private final Map<String, Integer> currentIndex; // Map<UBoat's username, Index of taken candidates> - relevant to uBoatCandidatesMap
    public CandidatesManager() {
        uBoatCandidatesMap = new HashMap<>();
        currentIndex = new HashMap<>();
    }

    /** UBoat client **/
    public synchronized void addContest(String username) {
        uBoatCandidatesMap.put(username, new LinkedList<>());
        currentIndex.put(username, 0);
    }
    public synchronized void removeContest(String username) {
        uBoatCandidatesMap.remove(username);
        currentIndex.remove(username);
    }

    /** Agent client **/
    // Each Agent sends his list of candidates
    public synchronized void addCandidate(String username, Queue<Candidate> candidates) {
        uBoatCandidatesMap.get(username).addAll(candidates);
    }
    /** UBoat client **/
    public synchronized LinkedList<Candidate> getNewCandidates(String username) {
        LinkedList<Candidate> newCandidates = new LinkedList<>();
        for (int i = currentIndex.get(username); i < uBoatCandidatesMap.get(username).size(); i++) {
            newCandidates.add(uBoatCandidatesMap.get(username).get(i));
        }
        currentIndex.put(username, uBoatCandidatesMap.size());
        return newCandidates;
    }
}
