package web.allies.servlets.dashboard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.common.rawData.battlefieldContest.AlliesData;
import web.uBoat.http.BattlefieldManager;
import web.uBoat.http.ReadyManager;
import web.uBoat.http.UBoatServletUtils;

import java.lang.reflect.Type;

@WebServlet(name = "ContestRegistrationServlet", value = "/allies/contest-registration")
public class ContestRegistrationServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final Type type = new TypeToken<AlliesData>(){}.getType();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String uBoatUsername = request.getParameter("uBoatUsername");
        String alliesUsername = request.getParameter("alliesUsername");
        String alliesData = request.getParameter("data");

        if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
            if (alliesUsername != null && !alliesUsername.isEmpty()) {
                if (alliesData != null && !alliesData.isEmpty()) {
                    BattlefieldManager battlefieldManager = UBoatServletUtils.getBattlefieldManager(getServletContext());
                    battlefieldManager.addAlliesToBattlefield(uBoatUsername, alliesUsername, gson.fromJson(alliesData, type));
                    battlefieldManager.increaseBattlefieldRegisteredTeams(uBoatUsername);

                    ReadyManager readyManager = UBoatServletUtils.getReadyManager(getServletContext());
                    readyManager.addAlliesToBattlefield(uBoatUsername, alliesUsername); // Adds Allies' Battlefield 'ready' value (false)
                } else {
                    String message = "ERROR: Invalid AlliesRawData for " + uBoatUsername.trim() + "->" + alliesUsername.trim() + "!";
                    System.out.println(message);
                    throw new ServletException(message);
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
}
