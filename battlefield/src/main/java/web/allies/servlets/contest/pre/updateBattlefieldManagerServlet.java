package web.allies.servlets.contest.pre;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.clients.agent.Agent;
import jar.common.rawData.battlefieldContest.AlliesData;
import web.allies.http.AgentsManager;
import web.allies.http.AlliesServletUtils;
import web.uBoat.http.BattlefieldManager;
import web.uBoat.http.UBoatServletUtils;

import java.util.List;
import java.util.Map;

@WebServlet(name = "updateBattlefieldManagerServlet", value = "/allies/update-battlefield-manager")
public class updateBattlefieldManagerServlet extends HttpServlet {

    /** Whenever UBoat needs an updated value of his Allies' agents, this servlet is used (within TimerTask)
     * No return value **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String username = request.getParameter("username");
        if (username != null && !username.isEmpty()) {
            BattlefieldManager battlefieldManager = UBoatServletUtils.getBattlefieldManager(getServletContext());
            Map<String, Map<String, AlliesData>> alliesBattlefieldMap = battlefieldManager.getAlliesBattlefieldMap();
            AgentsManager agentsManager = AlliesServletUtils.getAgentsMap(getServletContext());
            updateAgentsAmountInBattlefield(alliesBattlefieldMap.get(username), agentsManager.getMap());
        } else {
            System.out.println("ERROR: UBoat's username is invalid!");
            throw new ServletException("ERROR: UBoat's username is invalid!");
        }
    }

    /** Updates amount of agents that is relevant to a specific Allies in a specific Battlefield **/
    private void updateAgentsAmountInBattlefield(Map<String, AlliesData> alliesBattlefieldMap, Map<String, List<Agent>> alliesManager) {
        for (Map.Entry<String, AlliesData> entry : alliesBattlefieldMap.entrySet()) {
            String alliesUsername = entry.getKey();
            int amountAgents = alliesManager.get(alliesUsername).size();
            AlliesData rawData = entry.getValue();
            rawData.setTotalAgents(Integer.toString(amountAgents)); // Updates amount of agents that is relevant to a specific Allies in a specific Battlefield
        }
    }
}
