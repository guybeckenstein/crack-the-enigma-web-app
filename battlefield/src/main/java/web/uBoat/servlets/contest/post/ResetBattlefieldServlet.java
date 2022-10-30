package web.uBoat.servlets.contest.post;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.uBoat.http.UBoatServletUtils;

@WebServlet(name = "ResetBattlefieldServlet", urlPatterns = "/uboat/reset-battlefield")
public class ResetBattlefieldServlet extends HttpServlet {
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String username = request.getParameter("username");

        if (username != null && !username.isEmpty()) {
            UBoatServletUtils.getBattlefieldManager(getServletContext()).getBattlefield(username).reset();
        } else {
            System.out.println("ERROR: Invalid UBoat Username!");
            throw new ServletException("ERROR: Invalid UBoat Username!");
        }
    }
}
