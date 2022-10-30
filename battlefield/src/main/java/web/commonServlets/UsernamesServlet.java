package web.commonServlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "UsernamesServlet", urlPatterns = "/username")
public class UsernamesServlet extends HttpServlet {
    private Map<String, String> usernamesMap; // Map<Username, Type of {UBoat, Allies, Agent}>
    private final List<String> types = Arrays.asList("UBOAT", "ALLIES", "AGENT");
    private static final String USERNAME_MAP_PROPERTY = "usernamesMap";

    // Shows names list
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String> usernamesByType = new ArrayList<>();
        String type = request.getParameter("type");
        Set<Map.Entry<String, String>> usernamesMapValues = getUsernamesMap().entrySet();
        for (Map.Entry<String, String> value : usernamesMapValues) {
            if (value.getValue().equals(type)) {
                usernamesByType.add(value.getKey());
            }
        }
        Gson serializeJson = new Gson();
        String res = serializeJson.toJson(usernamesByType);
        response.getWriter().println(res);
    }

    // Add username
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username");
        String type = request.getParameter("type");

        if (username != null && !username.isEmpty()) {
            synchronized (this) {
                Map<String, String> usernamesMap = getUsernamesMap();
                if (!usernamesMap.containsKey(username)) { // Checks if username already exists
                    if (types.contains(type.toUpperCase())) {// Update servlet fields
                        usernamesMap.put(username, type);
                        // Add session attributes
                        request.getSession(true).setAttribute("username", username);
                        // Print
                        try (PrintWriter writer = response.getWriter()) {
                            writer.println("Username [" + username + "] of type [" + type + "] was added successfully to the list");
                        }
                    } else {
                        response.getWriter().println("ERROR: Username type [" + type + "] does not exist!");
                        throw new ServletException("ERROR: Username type [" + type + "] does not exist!");
                    }
                } else {
                    response.getWriter().println("ERROR: Username [" + username + "] already exists!");
                    throw new ServletException("ERROR: Username [" + username + "] already exists!");
                }
            }
        } else {
            response.getWriter().println("ERROR: Invalid username!");
            throw new ServletException("ERROR: Invalid username!");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getUsernamesMap() {
        usernamesMap = (Map<String, String>) getServletContext().getAttribute(USERNAME_MAP_PROPERTY);
        if (usernamesMap == null) {
            usernamesMap = new HashMap<>();

            getServletContext().setAttribute(USERNAME_MAP_PROPERTY, usernamesMap);
        }

        return usernamesMap;
    }

    // Used for removing username from lists
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String usernameOfUnknownClient = (String) getServletContext().getAttribute("username"); // Username given from other servlet
        if (usernameOfUnknownClient != null && !usernameOfUnknownClient.isEmpty()) {
            synchronized (this) {
                getUsernamesMap().remove(usernameOfUnknownClient);
                System.out.println("Successfully removed username " + usernameOfUnknownClient + " from usernames list!");
            }
        } else {
            System.out.println("ERROR: Invalid username!");
            throw new ServletException("ERROR: Invalid username!");
        }
    }
}