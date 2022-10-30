package web.uBoat.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jar.dto.XmlToServletDTO;
import jar.enigmaEngine.impl.EnigmaEngineImpl;
import jar.enigmaEngine.interfaces.EnigmaEngine;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EnigmaEngineAndXmlManager {
    private final Gson deserializer;
    private final Type type;
    private final Map<String, EnigmaEngine> uBoatEnigmaEngineMap; // Map<Username, Engine>
    private final Map<String, XmlToServletDTO> uBoatXmlMap; // Map<Username, XML information>

    public EnigmaEngineAndXmlManager() {
        deserializer = new Gson();
        type = new TypeToken<EnigmaEngineImpl>(){}.getType();
        uBoatEnigmaEngineMap = new HashMap<>();
        uBoatXmlMap = new HashMap<>();
    }

    /** Configuration **/
    public synchronized void addEnigmaEngine(String username, String machine) {
        boolean bool = false;
        while (!bool) {
            try {
                EnigmaEngineImpl dto = deserializer.fromJson(machine, type);
                uBoatEnigmaEngineMap.put(username, dto);
                bool = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EnigmaEngine dto = deserializer.fromJson(machine, type);
        uBoatEnigmaEngineMap.put(username, dto);
    }

    private void removeEnigmaEngine(String username) {
        uBoatEnigmaEngineMap.remove(username);
    }

    public EnigmaEngine getEnigmaEngine(String username) {
        return Collections.unmodifiableMap(uBoatEnigmaEngineMap).get(username);
    }

    public boolean isUBoatExistsInEnigmaEngineMap(String username) {
        return uBoatEnigmaEngineMap.containsKey(username);
    }

    /** XML Information **/
    public synchronized void addXmlInformation(String username, String xmlDto) {
        XmlToServletDTO dto = new Gson().fromJson(xmlDto, new TypeToken<XmlToServletDTO>(){}.getType());
        uBoatXmlMap.put(username, dto);
    }

    private void removeXmlInformation(String username) {
        uBoatXmlMap.remove(username);
    }

    public XmlToServletDTO getXmlInformation(String username) {
        return Collections.unmodifiableMap(uBoatXmlMap).get(username);
    }

    public boolean isUBoatExistsInXmlMap(String username) {
        return uBoatXmlMap.containsKey(username);
    }

    public synchronized void removeData(String username) {
        removeEnigmaEngine(username);
        removeXmlInformation(username);
    }
}
