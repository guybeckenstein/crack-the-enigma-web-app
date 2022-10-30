package web.uBoat.http;

import java.util.HashMap;
import java.util.Map;

public class ReadyManager {

    private final Map<String, Boolean> uBoatReadyMap; // Map<UBoat Username, Is UBoat ready>
    private final Map<String, Integer> uBoatAlliesRequiredMap; // Map<UBoat's Username, Amount of Allies required>
    private final Map<String, String> uBoatEncryptionInputMap; // Map<UBoat Username, Encryption input>
    private final Map<String, Map<String, Boolean>> uBoatAlliesReadyMap; // Map<UBoat's Username, Map<Allies' Username, Is Allies' team ready>>
    public ReadyManager() {
        uBoatReadyMap = new HashMap<>();
        uBoatEncryptionInputMap = new HashMap<>();
        uBoatAlliesReadyMap = new HashMap<>();
        uBoatAlliesRequiredMap = new HashMap<>();
    }

    /** Battlefield - UBoat **/
    // When creating a new Battlefield contest
    public synchronized void addBattlefield(String username, int requiredSize) {
        uBoatReadyMap.put(username, false);
        uBoatAlliesRequiredMap.put(username, requiredSize);
    }

    // When a UBoat quits (it means his participants automatically quits too)
    public synchronized void removeBattlefield(String username) { // TODO
        uBoatReadyMap.remove(username);
        uBoatEncryptionInputMap.remove(username);
        uBoatAlliesReadyMap.remove(username);
        uBoatAlliesRequiredMap.remove(username);
    }

    // When a certain UBoat presses on 'Ready' button
    public synchronized void setBattlefieldBooleanValueToReady(String username, String encryptionInput) {
        uBoatReadyMap.put(username, true);
        uBoatEncryptionInputMap.put(username, encryptionInput);
    }

    /** Battlefield - Allies **/
    // When adding an Allies' team to a UBoat's contest
    public synchronized void addAlliesToBattlefield(String uBoatUsername, String alliesUsername) {
        if (!uBoatAlliesReadyMap.containsKey(uBoatUsername)) {
            uBoatAlliesReadyMap.put(uBoatUsername, new HashMap<>());
        }
        uBoatAlliesReadyMap.get(uBoatUsername).put(alliesUsername, false);
    }

    // When an Allies' team quits
    public synchronized void removeAlliesFromBattlefield(String uBoatUsername, String alliesUsername) {
        uBoatAlliesReadyMap.get(uBoatUsername).remove(alliesUsername); // TODO
    }

    // When a certain Allies' team presses on 'Ready' button
    public synchronized void setAlliesInBattlefieldBooleanValueToReady(String uBoatUsername, String alliesUsername) {
        uBoatAlliesReadyMap.get(uBoatUsername).put(alliesUsername, true);
    }

    /** Battlefield - general **/
    public boolean startContest(String uBoatUsername) {
        return uBoatReadyMap.get(uBoatUsername) && allAlliesTeamsReady(uBoatUsername); // UBoat client is ready & all Allies are ready
    }

    // Checks whether all teams are ready
    private boolean allAlliesTeamsReady(String uBoatUsername) {
        if (uBoatAlliesReadyMap.get(uBoatUsername) == null || uBoatAlliesReadyMap.get(uBoatUsername).size() < uBoatAlliesRequiredMap.get(uBoatUsername)) {
            return false; // Contest must have Allies' teams to start
        }
        for (Boolean value : uBoatAlliesReadyMap.get(uBoatUsername).values()) {
            if (!value) {
                return false; // Not all teams are ready
            }
        }
        return true; // All teams are ready
    }

    public String getUBoatEncryptionInputMap(String uBoatUsername) {
        return uBoatEncryptionInputMap.get(uBoatUsername);
    }
}
