package web.uBoat.http;

import jar.clients.battlefield.Battlefield;
import jar.common.rawData.battlefieldContest.AlliesData;

import java.util.*;

public class BattlefieldManager {
    private final Map<String, Battlefield> uBoatBattlefieldMap; // Map<UBoat Username, Battlefield>
    private final Map<String, String> uBoatEncryptionMessageMap; // Map<UBoat Username, Encryption Message>
    private final Map<String, Map<String, AlliesData>> alliesBattlefieldMap; // Map<UBoat Username, Map<Allies Username, Allies Data>>
    private final Map<String, String> alliesToUBoatMap; // Map<Allies Username, UBoat Username>
    private final Map<String, String> battlefieldWinnerMap; // Map<UBoat Username, Allies Winner Username>
    public BattlefieldManager() {
        uBoatBattlefieldMap = new HashMap<>();
        uBoatEncryptionMessageMap = new HashMap<>();
        alliesBattlefieldMap = new HashMap<>();
        alliesToUBoatMap = new HashMap<>();
        battlefieldWinnerMap = new HashMap<>();
    }

    /** UBoat Battlefield **/
    public synchronized void addBattlefield(String username, Battlefield battlefield) {
        uBoatBattlefieldMap.put(username, battlefield);
        battlefieldWinnerMap.put(username, "");
    }

    public synchronized void updateBattlefieldStatus(String username, Battlefield.Status status) {
        uBoatBattlefieldMap.get(username).setStatus(status);
    }

    public boolean isUBoatUsernameExists(String username) {
        return uBoatBattlefieldMap.containsKey(username);
    }

    public synchronized void removeBattlefield(String username) {
        uBoatBattlefieldMap.remove(username); // Remove Battlefield from Battlefields' list (related to UBoat)
        uBoatEncryptionMessageMap.remove(username); // Remove Battlefield from Battlefields' list (related to UBoat's encryption message)
        Map<String, AlliesData> alliesDataMap = alliesBattlefieldMap.remove(username);// Remove Battlefield from Battlefields' list (related to Allies)
        if (alliesDataMap != null) {
            // Removes all Allies that are related to UBoat client from map
            Collection<AlliesData> alliesData = alliesDataMap.values();
            for (AlliesData allyData : alliesData) {
                alliesToUBoatMap.remove(allyData.getUsername());
            }
        }
        battlefieldWinnerMap.remove(username);
    }

    // Shown in Allies' dashboard screen
    public synchronized Map<String, Battlefield> getBattlefields() {
        return Collections.unmodifiableMap(uBoatBattlefieldMap);
    }

    public synchronized Battlefield getBattlefield(String username) {
        return Collections.unmodifiableMap(uBoatBattlefieldMap).get(username);
    }

    public boolean isBattlefieldExists(String battlefieldTitle) {
        for (Battlefield battlefield : uBoatBattlefieldMap.values()) {
            if (battlefield.getTitle().equals(battlefieldTitle)) {
                return true;
            }
        }
        return false;
    }

    /** UBoat Encryption Message **/
    public synchronized void addEncryptionMessage(String username, String message) {
        uBoatEncryptionMessageMap.put(username, message);
    }

    // When an Allies' needs the encryption message
    public synchronized String getEncryptionMessage(String username) {
        return Collections.unmodifiableMap(uBoatEncryptionMessageMap).get(username);
    }

    /** Allies Battlefield **/
    // When an Agent joins an existing Allies' team
    public synchronized Map<String, Map<String, AlliesData>> getAlliesBattlefieldMap() {
        return alliesBattlefieldMap;
    }

    // Shown in UBoat's contest screen & When Allies' team update their details
    public synchronized Collection<AlliesData> getAlliesTeamsBattlefieldWithinCollection(String uBoatUsername) {
        if (alliesBattlefieldMap.get(uBoatUsername) == null) {
            return null;
        } else {
            return alliesBattlefieldMap.get(uBoatUsername).values();
        }
    }

    // When an Allies team joins to a Battlefield, it is automatically been added to it
    public synchronized void addAlliesToBattlefield(String uBoatUsername, String alliesUsername, AlliesData data) {
        if (!alliesBattlefieldMap.containsKey(uBoatUsername)) { // Add UBoat's Battlefield new registered Allies map
            alliesBattlefieldMap.put(uBoatUsername, new HashMap<>());
        }
        alliesBattlefieldMap.get(uBoatUsername).put(alliesUsername, data); // Add UBoat's Battlefield new teams
        alliesToUBoatMap.put(alliesUsername, uBoatUsername);
    }

    // Making contest empty from clients, after it ends
    public synchronized void resetAlliesBattlefieldMap(String uBoatUsername) {
        alliesBattlefieldMap.put(uBoatUsername, new HashMap<>());
    }

    // Shown in Allies' dashboard screen, after Allies' team has registered
    public synchronized void increaseBattlefieldRegisteredTeams(String username) {
        Collections.unmodifiableMap(uBoatBattlefieldMap).get(username).increaseRegisteredTeams();
        System.out.println(username + "'s contest has increased its amount of registered Allies' teams!");
    }

    /** During contest **/
    public synchronized String getUBoatUsername(String alliesUsername) {
        return alliesToUBoatMap.get(alliesUsername);
    }

    public synchronized void addBattlefieldWinningAllies(String alliesUsername) {
        battlefieldWinnerMap.put(getUBoatUsername(alliesUsername), alliesUsername);
    }

    public synchronized String getBattlefieldWinningAllies(String alliesUsername) {
        return battlefieldWinnerMap.get(getUBoatUsername(alliesUsername));
    }
    public synchronized String getBattlefieldWinningAlliesUsingUsername(String uBoatUsername) {
        return battlefieldWinnerMap.get(uBoatUsername);
    }
}
