package web.allies.servlets.contest.pre;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import web.agent.http.AgentServletUtils;
import web.agent.http.AlliesReadySetManager;

import java.io.IOException;

@WebServlet(name = "SetContestStartedServlet", value = "/allies/is-contest-started")
public class IsContestStartedServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String alliesUsername = request.getParameter("team");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            AlliesReadySetManager alliesReadyMap = AgentServletUtils.getAlliesReadySetManager(getServletContext());
            if (alliesReadyMap.isAlliesInSet(alliesUsername)) {
                response.getWriter().print("true");
            } else {
                response.getWriter().print("false");
            }
            response.getWriter().flush();
        } else {
            System.out.println("ERROR: Invalid Allies username!");
            throw new ServletException("ERROR: Invalid Allies username!");
        }
    }

    /** Allies client sends the server his username saying contest has started **/
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String alliesUsername = request.getParameter("team");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            // Define Allies as ready
            AlliesReadySetManager alliesReadySet = AgentServletUtils.getAlliesReadySetManager(getServletContext());
            alliesReadySet.addAllies(alliesUsername);
            System.out.println("Successfully added " + alliesUsername + " to ready set!");
        } else {
            System.out.println("ERROR: Invalid Allies username!");
            throw new ServletException("ERROR: Invalid Allies username!");
        }
    }
}
