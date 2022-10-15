package web.uBoat.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static common.InputCheck.isStringUnique;

/** Adds each battlefield title and its username **/
@WebServlet(name = "BattlefieldTitleServlet", urlPatterns = "/uboat/title")
public class BattlefieldTitleServlet extends HttpServlet {
    private static final String TITLE_PARAMETER = "title";
    private static final String USERNAME_PARAMETER = "username";
    private static final String LIST_PROPERTY_TITLE_USERNAME = "titlesAndUsernamesList";
    private Map<String, String> titlesAndUsernamesMap;
    @Override
    public void init() {
        titlesAndUsernamesMap = new HashMap<>();
        getServletContext().setAttribute(LIST_PROPERTY_TITLE_USERNAME, titlesAndUsernamesMap);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String title = (String) request.getAttribute(TITLE_PARAMETER);
        String username = (String) request.getAttribute(USERNAME_PARAMETER);

        if (title != null && !title.isEmpty() && username != null && !username.isEmpty()) {
            Map<String, String> titlesAndUsernamesMap = getHashMap();
            if (isStringUnique(new ArrayList<>(titlesAndUsernamesMap.keySet()), title)) { // Checks if title already exists
                if (isStringUnique(new ArrayList<>(titlesAndUsernamesMap.values()), username)) { // Checks if username already exists
                    // Update servlet fields
                    titlesAndUsernamesMap.put(title, username);
                    // Add session attributes
                    request.getSession(true).setAttribute(TITLE_PARAMETER, title);
                    request.getSession(true).setAttribute(USERNAME_PARAMETER, username);
                    // Print
                    try (PrintWriter writer = response.getWriter()) {
                        writer.println("Title [" + title + "] and username [" + username + "] were added successfully to the list");
                        print(writer);
                    }
                } else {
                    throw new ServletException("ERROR: Username [" + username + "] already exists!");
                }
            } else {
                throw new ServletException("ERROR: Title [" + title + "] already exists!");
            }
        } else {
            throw new ServletException("ERROR: Invalid title or username!");
        }
    }

    private void print(PrintWriter out) {
        out.println("UBoat battlefields' <title, username> list so far:");
        Set<Map.Entry<String, String>> entrySet = getHashMap().entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entrySet) {
            out.println( "#" + (++i) + ": <" + entry.getKey() + ", " + entry.getValue() + ">" );
        }
    }

    protected Map<String, String> getHashMap() {
        titlesAndUsernamesMap = (Map<String, String>) getServletContext().getAttribute(LIST_PROPERTY_TITLE_USERNAME);
        if (titlesAndUsernamesMap == null) {
            titlesAndUsernamesMap = new HashMap<>();

            getServletContext().setAttribute(LIST_PROPERTY_TITLE_USERNAME, titlesAndUsernamesMap);
        }

        return titlesAndUsernamesMap;
    }

    // Add title and username
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain;charset=UTF-8");
        doGet(request, response);
    }
}