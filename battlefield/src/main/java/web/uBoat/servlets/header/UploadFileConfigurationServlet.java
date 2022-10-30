package web.uBoat.servlets.header;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.clients.battlefield.Battlefield;
import jar.dto.XmlToServletDTO;
import org.jetbrains.annotations.NotNull;
import web.uBoat.http.UBoatServletUtils;
import web.uBoat.http.BattlefieldManager;
import web.uBoat.http.EnigmaEngineAndXmlManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@WebServlet(name = "UploadFileConfigurationServlet", urlPatterns = "/uboat/upload-file")
public class UploadFileConfigurationServlet extends HttpServlet {
    private final Gson gson = new Gson();

    /** When an Allies' team needs all Battlefield contests, they send a GET request. Only Battlefields with configuration are returned **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Battlefield> allBattlefields = getBattlefieldsWithAssignedConfiguration(); // Returned value to the Allies' client

        String allContests = gson.toJson(allBattlefields);

        try (PrintWriter print = response.getWriter()) {
            print.println(allContests);
            print.flush();
        }
    }

    @NotNull
    private List<Battlefield> getBattlefieldsWithAssignedConfiguration() {
        List<Battlefield> finalBattlefieldValues = new ArrayList<>(); // Returned value
        EnigmaEngineAndXmlManager enigmaEngineAndXmlManager = UBoatServletUtils.getEnigmaEngineAndXmlManager(getServletContext());
        BattlefieldManager battlefieldManager = UBoatServletUtils.getBattlefieldManager(getServletContext());
        Set<Map.Entry<String, Battlefield>> battlefieldEntries = battlefieldManager.getBattlefields().entrySet();

        for (Map.Entry<String, Battlefield> entry : battlefieldEntries) {
            if (enigmaEngineAndXmlManager.isUBoatExistsInXmlMap(entry.getKey())) {
                finalBattlefieldValues.add(entry.getValue());
            }
        }
        return finalBattlefieldValues;
    }

    /** When an UBoat uploads its XML configuration to the server, it is uploaded by this servlet (POST request) **/
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");

        Properties properties = new Properties();
        properties.load(request.getInputStream());

        // Gets body parameters by separating them (otherwise it doesn't work)
        String[] keys = properties.getProperty("username").split("&");

        String username = keys[0];
        String xmlFile = keys[1].substring(4);

        if (username != null && !username.isEmpty()) {
            try {
                if (!xmlFile.isEmpty()) {// Get fixed body properties
                    xmlFile = java.net.URLDecoder.decode(xmlFile, StandardCharsets.UTF_8.name());
                    // Add battlefield
                    BattlefieldManager battlefieldManager = UBoatServletUtils.getBattlefieldManager(getServletContext());
                    Battlefield battlefield = createBattlefield(username, xmlFile, battlefieldManager);
                    // Add XML information
                    EnigmaEngineAndXmlManager enigmaEngineAndXmlManager = UBoatServletUtils.getEnigmaEngineAndXmlManager(getServletContext());
                    enigmaEngineAndXmlManager.addXmlInformation(username, xmlFile);

                    if (battlefield != null) {
                        battlefieldManager.addBattlefield(username, battlefield);
                        System.out.println("Successfully added [" + username + "'s] battlefield: " + battlefield + "!");
                    } else {
                        System.out.println("ERROR: This XML file's Battlefield was already given!");
                        throw new ServletException("ERROR: This XML file was Battlefield already given!");
                    }
                } else {
                    System.out.println("ERROR: Invalid XML DTO!");
                    throw new ServletException("ERROR: Invalid XML DTO!");
                }
            } catch (UnsupportedEncodingException e) {
                // not going to happen - value came from JDK's own StandardCharsets
            }
        } else {
            System.out.println("ERROR: Invalid username!");
            throw new ServletException("ERROR: Invalid username!");
        }
    }

    private Battlefield createBattlefield(String username, String xmlFile, BattlefieldManager battlefieldManager) {
        XmlToServletDTO dto = gson.fromJson(xmlFile, new TypeToken<XmlToServletDTO>(){}.getType());
        if (dto.getBattlefieldTitle() != null && !battlefieldManager.isBattlefieldExists(dto.getBattlefieldTitle())) {
            return new Battlefield(dto.getBattlefieldTitle(), username, Battlefield.Status.Available, dto.getDifficulty(), dto.getNumAllies());
        } else {
            return null;
        }
    }
}