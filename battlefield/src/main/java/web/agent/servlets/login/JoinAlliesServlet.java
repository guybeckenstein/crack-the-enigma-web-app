package web.agent.servlets.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.clients.agent.Agent;
import web.allies.http.AgentsManager;
import web.allies.http.AlliesServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet(name = "JoinAlliesServlet", urlPatterns = "/agent/join/allies")
public class JoinAlliesServlet extends HttpServlet {

    /** Puts an agent in the Allies' team **/
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Adds Agent's username to Allies team
        String alliesUsername = request.getParameter("team");
        String agentUsername = request.getParameter("username");
        String totalThreads = request.getParameter("threads");
        String tasksWithdrawal = request.getParameter("tasks");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            if (agentUsername != null && !agentUsername.isEmpty()) {
                try {
                    int threads = Integer.parseInt(totalThreads);
                    int tasks = Integer.parseInt(tasksWithdrawal);
                    AgentsManager agentsManager = AlliesServletUtils.getAgentsMap(getServletContext());
                    agentsManager.addAgent(alliesUsername, new Agent(agentUsername, threads, tasks));
                    print(response.getWriter(), agentsManager.getMap());
                } catch (NumberFormatException e) {
                    System.out.println("ERROR: Invalid threads or tasks value!");
                    throw new ServletException("ERROR: Invalid threads or tasks value!");
                }
            } else {
                System.out.println("ERROR: Agent's username is invalid!");
                throw new ServletException("ERROR: Agent's username is invalid!");
            }
        } else {
            System.out.println("ERROR: Allies' username is invalid!");
            throw new ServletException("ERROR: Allies' username is invalid!");
        }
    }

    /** Prints all teams and their agents **/
    private void print(PrintWriter writer, Map<String, List<Agent>> map) {
        for (Map.Entry<String, List<Agent>> entry : map.entrySet()) {
            writer.println(entry.getKey() + " related agents:");
            for (Agent agent : entry.getValue()) {
                writer.println("\t" + agent);
            }
        }
    }
}
