package web.abstractServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static common.InputCheck.isStringUnique;

public abstract class UsernamesServlet extends HttpServlet {
    public static final String USERNAME_PARAMETER = "username";
    protected static final String LIST_PROPERTY_USERNAME = "usernamesList";
    protected List<String> usernamesList;

    // Shows names list
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter(USERNAME_PARAMETER);

        if (username != null && !username.isEmpty()) {
            List<String> usernamesList = getUsernamesList();
            if (isStringUnique(usernamesList, username)) { // Checks if username already exists
                // Update servlet fields
                usernamesList.add(username);
                // Add session attributes
                request.getSession(true).setAttribute(USERNAME_PARAMETER, username);
                // Print
                try (PrintWriter writer = response.getWriter()) {
                    writer.println("Username [" + username + "] was added successfully to the list");
                    print(writer);
                }
            } else {
                response.getWriter().println("ERROR: Username [" + username + "] already exists!");
                throw new ServletException("ERROR: Username [" + username + "] already exists!");
            }
        } else {
            response.getWriter().println("ERROR: Invalid username!");
            throw new ServletException("ERROR: Invalid username!");
        }
    }

    protected abstract void print(PrintWriter out);

    protected List<String> getUsernamesList() {
        usernamesList = (List<String>) getServletContext().getAttribute(LIST_PROPERTY_USERNAME);
        if (usernamesList == null) {
            usernamesList = new ArrayList<>();

            getServletContext().setAttribute(LIST_PROPERTY_USERNAME, usernamesList);
        }

        return usernamesList;
    }

    // Add username
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain;charset=UTF-8");
        doGet(request, response);
    }
}