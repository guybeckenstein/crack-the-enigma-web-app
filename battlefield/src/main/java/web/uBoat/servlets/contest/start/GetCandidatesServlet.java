package web.uBoat.servlets.contest.start;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.common.rawData.Candidate;
import web.uBoat.http.CandidatesManager;
import web.uBoat.http.UBoatServletUtils;

import java.io.IOException;
import java.util.LinkedList;

@WebServlet(name = "GetCandidatesServlet", value = "/uboat/get-candidates")
public class GetCandidatesServlet extends HttpServlet {

    /** When an UBoat client wants to get all candidates, according to his username **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        if (username != null && !username.isEmpty()) {
            CandidatesManager candidatesManager = UBoatServletUtils.getCandidatesManager(getServletContext());
            LinkedList<Candidate> candidatesQueue = candidatesManager.getNewCandidates(username);
            String jsonCandidatesQueue = new Gson().toJson(candidatesQueue);

            response.getWriter().print(jsonCandidatesQueue);
            response.getWriter().flush();
        } else {
            System.out.println("ERROR: Invalid UBoat's username!");
            throw new ServletException("ERROR: Invalid UBoat's username!");
        }
    }

    /** When an UBoat client wants to add his contest to the map **/
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String username = request.getParameter("username");
        if (username != null && !username.isEmpty()) {
            CandidatesManager candidatesManager = UBoatServletUtils.getCandidatesManager(getServletContext());
            candidatesManager.addContest(username);
        } else {
            System.out.println("ERROR: Invalid UBoat's username!");
            throw new ServletException("ERROR: Invalid UBoat's username!");
        }
    }
}
