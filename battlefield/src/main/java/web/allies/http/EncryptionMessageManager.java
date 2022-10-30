package web.allies.http;

import java.util.HashMap;
import java.util.Map;

public class EncryptionMessageManager {
    private final Map<String, String> encryptionMessageMap; // Map<Allies' Username, Encryption Message>

    public EncryptionMessageManager() {
        encryptionMessageMap = new HashMap<>();
    }

    /** Related to Allies **/
    public void addEncryptionMessage(String alliesUsername, String encryptionMessage) {
        encryptionMessageMap.put(alliesUsername, encryptionMessage);
    }

    public synchronized void removeEncryptionMessage(String alliesUsername) {
        encryptionMessageMap.remove(alliesUsername);
    }

    /** Related to Agent **/
    public String getEncryptionMessageByUsername(String alliesUsername) {
        return encryptionMessageMap.get(alliesUsername);
    }
}
