package web.agent.servlets.contest.start;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.common.rawData.Candidate;
import jar.common.rawData.agents.AgentsRawData;
import jar.common.rawData.battlefieldContest.TeamCandidates;
import web.agent.http.AgentServletUtils;
import web.allies.http.AlliesServletUtils;
import web.uBoat.http.UBoatServletUtils;

import java.lang.reflect.Type;
import java.util.LinkedList;

@WebServlet(name = "FinalCandidatesServlet", value = "/agent/final-candidates")
public class FinalCandidatesServlet extends HttpServlet {
    private final Gson deserializer = new Gson();
    private final Type uBoatType = new TypeToken<LinkedList<Candidate>>(){}.getType();
    private final Type alliesType = new TypeToken<LinkedList<TeamCandidates>>(){}.getType();
    private final Type agentsRawDataType = new TypeToken<AgentsRawData>(){}.getType();
    /** Agent gets his final candidates -> send it to server **/
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String alliesUsername = request.getParameter("team");
        String uBoatCandidatesJson = request.getParameter("uboatCandidates");
        String alliesCandidatesJson = request.getParameter("alliesCandidates");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            if (uBoatCandidatesJson != null && !uBoatCandidatesJson.isEmpty()) {
                if (alliesCandidatesJson != null && !alliesCandidatesJson.isEmpty()) {
                    // Deserialize UBoat
                    LinkedList<Candidate> uBoatCandidatesQueue = deserializer.fromJson(uBoatCandidatesJson, uBoatType);
                    String uBoatUsername = UBoatServletUtils.getBattlefieldManager(getServletContext()).getUBoatUsername(alliesUsername);
                    UBoatServletUtils.getCandidatesManager(getServletContext()).addCandidate(uBoatUsername, uBoatCandidatesQueue);
                    // Deserialize Allies
                    LinkedList<TeamCandidates> alliesCandidatesQueue = deserializer.fromJson(alliesCandidatesJson, alliesType);
                    AlliesServletUtils.getCandidatesManager(getServletContext()).addCandidates(alliesUsername, alliesCandidatesQueue);
                } else {
                    System.out.println("ERROR: Allies' candidates list is empty!");
                    throw new ServletException("ERROR: Allies' candidates list is empty!");
                }
            } else {
                System.out.println("ERROR: UBoat's candidates list is empty!");
                throw new ServletException("ERROR: UBoat's candidates list is empty!");
            }
        } else {
            System.out.println("ERROR: Invalid Allies' username!");
            throw new ServletException("ERROR: Invalid Allies' username!");
        }
    }

    /** Allies gets current information -> send it to server **/
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String alliesUsername = request.getParameter("team");
        String agentsRawDataJson = request.getParameter("agentRawData");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            if (agentsRawDataJson != null && !agentsRawDataJson.isEmpty()) {
                // Deserialize Agent's raw data
                AgentsRawData agentsRawData = deserializer.fromJson(agentsRawDataJson, agentsRawDataType);
                AgentServletUtils.getInformationForAlliesManager(getServletContext()).addAgentData(alliesUsername,
                        agentsRawData.getAgentName(), agentsRawData);
            } else {
                System.out.println("ERROR: Agent's raw data is empty!");
                throw new ServletException("ERROR: Agent's raw data is empty!");
            }
        } else {
            System.out.println("ERROR: Invalid Allies' username!");
            throw new ServletException("ERROR: Invalid Allies' username!");
        }
    }
}
