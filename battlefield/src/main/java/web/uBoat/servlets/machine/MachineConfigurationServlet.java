package web.uBoat.servlets.machine;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.uBoat.http.EnigmaEngineAndXmlManager;
import web.uBoat.http.ReadyManager;
import web.uBoat.http.UBoatServletUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@WebServlet(name = "MachineConfigurationServlet", urlPatterns = "/uboat/machine-configuration")
public class MachineConfigurationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        Properties properties = new Properties();
        properties.load(request.getInputStream());

        String[] keys = properties.getProperty("username").split("&");

        String username = keys[0];
        String machine = keys[1].substring(8); // Engine
        int requiredAllies = Integer.parseInt(keys[2].substring(9));
        if (!machine.isEmpty()) {
            if (!username.isEmpty()) {
                if (requiredAllies >= 1) {
                    try {
                        EnigmaEngineAndXmlManager enigmaEngineAndXmlManager = UBoatServletUtils.getEnigmaEngineAndXmlManager(getServletContext());
                        ReadyManager readyManager = UBoatServletUtils.getReadyManager(getServletContext());
                        if (!enigmaEngineAndXmlManager.isUBoatExistsInEnigmaEngineMap(username)) {
                            machine = java.net.URLDecoder.decode(machine, StandardCharsets.UTF_8.name());
                            enigmaEngineAndXmlManager.addEnigmaEngine(username, machine); // Adds Battlefield configuration value
                            readyManager.addBattlefield(username, requiredAllies); // Adds Battlefield 'ready' value (false)
                            response.getWriter().println("Successfully added [" + username + "'s] configuration: " + machine + "!");
                        } else {
                            response.getWriter().println("ERROR: Username already exists!");
                            throw new ServletException("ERROR: Username already exists!");
                        }
                    } catch (UnsupportedEncodingException e) {
                        response.getWriter().println("ERROR: UnsupportedEncodingException thrown!");
                        throw new ServletException("ERROR: UnsupportedEncodingException thrown!");
                    }
                } else {
                    response.getWriter().println("ERROR: Invalid required Allies size!");
                    throw new ServletException("ERROR: Invalid required Allies size!");
                }
            } else {
                response.getWriter().println("ERROR: Invalid username!");
                throw new ServletException("ERROR: Invalid username!");
            }
        } else {
            response.getWriter().println("ERROR: Invalid configuration!");
            throw new ServletException("ERROR: Invalid configuration!");
        }
    }
}
