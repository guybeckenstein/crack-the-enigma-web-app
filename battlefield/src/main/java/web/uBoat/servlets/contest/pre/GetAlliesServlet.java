package web.uBoat.servlets.contest.pre;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.common.rawData.battlefieldContest.AlliesData;
import web.uBoat.http.UBoatServletUtils;
import web.uBoat.http.BattlefieldManager;

import java.io.IOException;
import java.util.Collection;

@WebServlet(name = "GetAlliesServlet", value = "/uboat/get-allies")
public class GetAlliesServlet extends HttpServlet {
    private final Gson gson = new Gson();

    /** Shows all registered Allies teams in a certain Battlefield (of an UBoat client) **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uBoatUsername = request.getParameter("username");

        if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
            BattlefieldManager battlefieldManager = UBoatServletUtils.getBattlefieldManager(getServletContext());
            String alliesRawData = gson.toJson(battlefieldManager.getAlliesTeamsBattlefieldWithinCollection(uBoatUsername)); // Can be null
            response.getWriter().print(alliesRawData);
        } else {
            System.out.println("ERROR: UBoat's username is empty!");
            throw new ServletException("ERROR: UBoat's username is empty!");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String uBoatUsername = request.getParameter("username");
        String alliesUsername = request.getParameter("team");
        String taskSize = request.getParameter("size");
        String totalAgents = request.getParameter("agents");

        if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
            if (alliesUsername != null && !alliesUsername.isEmpty()) {
                BattlefieldManager battlefieldManager = UBoatServletUtils.getBattlefieldManager(getServletContext());
                Collection<AlliesData> alliesDataCollection = battlefieldManager.getAlliesTeamsBattlefieldWithinCollection(uBoatUsername);
                AlliesData alliesData = getAlliesDataFromCollection(alliesUsername, alliesDataCollection);
                if (alliesData != null) {
                    if (taskSize != null) { // Update task size
                        System.out.println(uBoatUsername + "'s Allies team " + alliesUsername + " have updated their task size to " + taskSize + "!");
                        alliesData.setTaskSize(taskSize);
                    }
                    if (totalAgents != null) { // Update size of total agents
                        System.out.println(uBoatUsername + "'s Allies team " + alliesUsername + " have updated agents amount to " + totalAgents + "!");
                        alliesData.setTotalAgents(totalAgents);
                    }
                } else {
                    System.out.println("ERROR: Allies' username does not exist in BattlefieldManager!");
                    throw new ServletException("ERROR: Allies' username does not exist in BattlefieldManager!");
                }
            } else {
                System.out.println("ERROR: Allies' username is empty!");
                throw new ServletException("ERROR: Allies' username is empty!");
            }
        } else {
            System.out.println("ERROR: UBoat's username is empty!");
            throw new ServletException("ERROR: UBoat's username is empty!");
        }
    }

    private AlliesData getAlliesDataFromCollection(String alliesUsername, Collection<AlliesData> alliesDataCollection) {
        for (AlliesData alliesData : alliesDataCollection) {
            if (alliesData.getUsername().equals(alliesUsername)) {
                return alliesData;
            }
        }
        return null;
    }

    /** Making contest empty from clients **/
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String uBoatUsername = request.getParameter("username");

        if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
            UBoatServletUtils.getBattlefieldManager(getServletContext()).resetAlliesBattlefieldMap(uBoatUsername);
            System.out.println(uBoatUsername + "'s battlefield has been reset!");
        } else {
            System.out.println("ERROR: UBoat's username is empty!");
            throw new ServletException("ERROR: UBoat's username is empty!");
        }
    }
}
