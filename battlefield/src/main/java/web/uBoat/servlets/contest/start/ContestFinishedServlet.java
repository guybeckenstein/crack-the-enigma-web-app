package web.uBoat.servlets.contest.start;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import web.uBoat.http.BattlefieldManager;
import web.uBoat.http.UBoatServletUtils;

import java.io.IOException;

@WebServlet(name = "ContestEndedServlet", value = "/uboat/contest-finished")
public class ContestFinishedServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uBoatUsername = request.getParameter("username");
        if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
            BattlefieldManager battlefieldManager = UBoatServletUtils.getBattlefieldManager(getServletContext());
            String battlefieldWinningAllies = battlefieldManager.getBattlefieldWinningAlliesUsingUsername(uBoatUsername);
            response.getWriter().println(battlefieldWinningAllies);
            response.getWriter().flush();
        } else {
            System.out.println("ERROR: Invalid UBoat's username!");
            throw new ServletException("ERROR: Invalid UBoat's username!");
        }
    }
}
