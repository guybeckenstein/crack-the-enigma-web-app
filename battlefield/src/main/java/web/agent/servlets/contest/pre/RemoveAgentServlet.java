package web.agent.servlets.contest.pre;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.agent.http.AgentServletUtils;
import web.allies.http.AlliesServletUtils;

import java.io.IOException;

@WebServlet(name = "RemoveAgentServlet", value = "/agent/remove")
public class RemoveAgentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String alliesUsername = request.getParameter("team");
        String agentUsername = request.getParameter("agent");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            if (agentUsername != null && !agentUsername.isEmpty()) {
                // Removes Agent from Allies' map
                AlliesServletUtils.getAgentsMap(getServletContext()).removeAgent(alliesUsername, agentUsername);
                // Removes Agent from Allies' information
                AgentServletUtils.getInformationForAlliesManager(getServletContext()).removeAgent(alliesUsername, agentUsername);
                // Removes username from server
                getServletContext().setAttribute("username", agentUsername);
                getServletContext().getRequestDispatcher("/username").include(request, response);
                System.out.println("Logging out...");
            } else {
                System.out.println("ERROR: Invalid Agent's username!");
                throw new ServletException("ERROR: Invalid Agent's username!");
            }
        } else {
            System.out.println("ERROR: Invalid Allies' username!");
            throw new ServletException("ERROR: Invalid Allies' username!");
        }
    }
}
