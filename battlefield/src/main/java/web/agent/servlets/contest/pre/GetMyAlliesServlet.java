package web.agent.servlets.contest.pre;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.common.rawData.agents.AlliesRawData;
import web.agent.http.AgentServletUtils;
import web.allies.http.AlliesServletUtils;
import web.allies.http.ContestManager;

import java.io.IOException;

@WebServlet(name = "GetMyAlliesServlet", value = "/agent/get-my-allies")
public class GetMyAlliesServlet extends HttpServlet {
    /** Called when an Agent needs information about its parent client (Allies) **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String alliesUsername = request.getParameter("team");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            ContestManager contestManager = AlliesServletUtils.getContestManagerMap(getServletContext());
            AlliesRawData alliesRawData = contestManager.getAlliesRawDataByUsername(alliesUsername);
            String jsonAlliesRawData = new Gson().toJson(alliesRawData);
            response.getWriter().print(jsonAlliesRawData);
            response.getWriter().flush();
        } else {
            System.out.println("ERROR: Invalid Allies username!");
            throw new ServletException("ERROR: Invalid Allies username!");
        }
    }

    /** Called when an Allies' client initializes his contest mode - total tasks is set to be 0 **/
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String alliesUsername = request.getParameter("team");
        String contestTitle = request.getParameter("title");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            if (contestTitle != null && !contestTitle.isEmpty()) {
                AlliesRawData alliesRawData = new AlliesRawData(alliesUsername, contestTitle, 0);
                AlliesServletUtils.getContestManagerMap(getServletContext()).addAllies(alliesUsername, alliesRawData);
                AlliesServletUtils.getCandidatesManager(getServletContext()).addAlliesToContest(alliesUsername);
                AgentServletUtils.getInformationForAlliesManager(getServletContext()).addAllies(alliesUsername);
                System.out.println("Initialized " + alliesUsername + "'s contest data in server!");
            } else {
                System.out.println("ERROR: Invalid contest title!");
                throw new ServletException("ERROR: Invalid contest title!");
            }
        } else {
            System.out.println("ERROR: Invalid Allies username!");
            throw new ServletException("ERROR: Invalid Allies username!");
        }
    }
}
