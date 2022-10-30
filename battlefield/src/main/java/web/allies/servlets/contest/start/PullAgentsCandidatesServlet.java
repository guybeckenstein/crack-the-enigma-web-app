package web.allies.servlets.contest.start;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.common.rawData.agents.AgentsRawData;
import jar.common.rawData.battlefieldContest.TeamCandidates;
import javafx.util.Pair;
import web.agent.http.AgentServletUtils;
import web.allies.http.AlliesServletUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@WebServlet(name = "PullAgentsCandidatesServlet", value = "/allies/final-candidates")
public class PullAgentsCandidatesServlet extends HttpServlet {
    private final Gson serializer = new Gson();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String alliesUsername = request.getParameter("team");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            // Get new candidates
            LinkedList<TeamCandidates> newCandidates = AlliesServletUtils.getCandidatesManager(getServletContext()).getNewCandidates(alliesUsername);
            // Get Agents information
            List<AgentsRawData> agentsRawDataList = new ArrayList<>(AgentServletUtils.getInformationForAlliesManager(getServletContext())
                    .getAgentsDataMap(alliesUsername).values());
            // Create pair
            Pair<LinkedList<TeamCandidates>, List<AgentsRawData>> responsePair = new Pair<>(newCandidates, agentsRawDataList);
            String responsePairJson = serializer.toJson(responsePair);
            response.getWriter().println(responsePairJson);
            response.getWriter().flush();
        } else {
            System.out.println("ERROR: Invalid Allies' username!");
            throw new ServletException("ERROR: Invalid Allies' username!");
        }
    }
}
