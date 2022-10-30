package web.agent.servlets.contest.start;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import web.uBoat.http.BattlefieldManager;
import web.uBoat.http.UBoatServletUtils;

import java.io.IOException;

@WebServlet(name = "WinningTeamServlet", value = "/agent/winning-team")
public class WinningTeamServlet extends HttpServlet {
    /** When agent found the original encryption message, he sends PUT request to server with his allies team **/
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String alliesUsername = request.getParameter("team");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            BattlefieldManager battlefieldManager = UBoatServletUtils.getBattlefieldManager(getServletContext());
            battlefieldManager.addBattlefieldWinningAllies(alliesUsername);
        } else {
            System.out.println("ERROR: Invalid Allies username!");
            throw new ServletException("ERROR: Invalid Allies username!");
        }
    }

    /** If there is a winner, server gets the Allies' team name, and if not nothing happens **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String alliesUsername = request.getParameter("team");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            BattlefieldManager battlefieldManager = UBoatServletUtils.getBattlefieldManager(getServletContext());
            String battlefieldWinningAllies = battlefieldManager.getBattlefieldWinningAllies(alliesUsername);
            response.getWriter().println(battlefieldWinningAllies);
            response.getWriter().flush();
        } else {
            System.out.println("ERROR: Invalid Allies username!");
            throw new ServletException("ERROR: Invalid Allies username!");
        }
    }
}
