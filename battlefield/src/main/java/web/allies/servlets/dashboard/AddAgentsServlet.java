package web.allies.servlets.dashboard;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.clients.agent.Agent;
import web.allies.http.AgentsManager;
import web.allies.http.AlliesServletUtils;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AgentsServlet", urlPatterns = "/allies/add-agent")
public class AddAgentsServlet extends HttpServlet {

    private final Gson gson = new Gson();

    /** Returns all the agents of the Allies' team **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String alliesUsername = request.getParameter("team");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            AgentsManager agentsManager = AlliesServletUtils.getAgentsMap(getServletContext());
            List<Agent> alliesTeamAllAgents = agentsManager.getMap().get(alliesUsername);
            String res = gson.toJson(alliesTeamAllAgents);
            response.getWriter().println(res);
        } else {
            System.out.println("Invalid Allies' username!");
            throw new ServletException("Invalid Allies' username!");
        }
    }

    /** Creates a list for an Allies **/
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String alliesUsername = request.getParameter("username");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            AgentsManager agentsManager = AlliesServletUtils.getAgentsMap(getServletContext());
            agentsManager.addAllies(alliesUsername);
        } else {
            System.out.println("Invalid Allies' username!");
            throw new ServletException("Invalid Allies' username!");
        }
    }


}
