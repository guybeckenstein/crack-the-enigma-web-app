package web.uBoat.servlets;

import engine.users.UserManager;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import web.http.ServletUtils;
import web.http.SessionUtils;

import java.io.IOException;

// TODO: add logout (remove username and title inputs)
@WebServlet(name = "LoadXmlFileServlet", urlPatterns = "/uboat/logout")
public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        if (usernameFromSession != null) {
            System.out.println("Clearing session for " + usernameFromSession);
            userManager.removeUser(usernameFromSession);
            SessionUtils.clearSession(request);
        }
    }
}
