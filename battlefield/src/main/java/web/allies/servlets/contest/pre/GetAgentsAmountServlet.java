package web.allies.servlets.contest.pre;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.allies.http.AgentsManager;
import web.allies.http.AlliesServletUtils;

import java.io.IOException;

@WebServlet(name = "ServletGetAgentsAmountServlet", value = "/allies/get-agents-amount")
public class GetAgentsAmountServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String alliesUsername = request.getParameter("team");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            AgentsManager alliesAgentsMap = AlliesServletUtils.getAgentsMap(getServletContext());
            int agentsAmount = alliesAgentsMap.getMap().get(alliesUsername).size();
            if (agentsAmount > 0) {
                response.getWriter().print(agentsAmount);
            } else {
                response.getWriter().print(-1);
            }
            response.getWriter().flush();
        } else {
            System.out.println("ERROR: Invalid Allies username!");
            throw new ServletException("ERROR: Invalid Allies username!");
        }
    }
}
