package web.uBoat.servlets.contest.pre;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.clients.battlefield.Battlefield;
import web.uBoat.http.BattlefieldManager;
import web.uBoat.http.UBoatServletUtils;
import web.uBoat.http.ReadyManager;

import java.io.IOException;

@WebServlet(name = "StartContestServlet", value = "/uboat/contest")
public class StartContestServlet extends HttpServlet {

    /** This POST request is whenever an UBoat client presses on 'Ready' button **/
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String uBoatUsername = request.getParameter("username");
        String encryptionInput = request.getParameter("input");
        if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
            if (encryptionInput != null && !encryptionInput.isEmpty()) {
                ReadyManager readyManager = UBoatServletUtils.getReadyManager(getServletContext());
                readyManager.setBattlefieldBooleanValueToReady(uBoatUsername, encryptionInput);
                System.out.println(uBoatUsername + " is ready!");
            } else {
                System.out.println("ERROR: Encryption input is empty!");
                throw new ServletException("ERROR: Encryption input is empty!");
            }
        } else {
            System.out.println("ERROR: UBoat's username is empty!");
            throw new ServletException("ERROR: UBoat's username is empty!");
        }
    }

    /** This PUT request is whenever an Allies' team client presses on 'Ready' button **/
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String uBoatUsername = request.getParameter("username");
        String alliesUsername = request.getParameter("team");

        if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
            if (alliesUsername != null && !alliesUsername.isEmpty()) {
                ReadyManager readyManager = UBoatServletUtils.getReadyManager(getServletContext());
                readyManager.setAlliesInBattlefieldBooleanValueToReady(uBoatUsername, alliesUsername);
                System.out.println(uBoatUsername + "'s Allies " + alliesUsername + " is ready!");
            } else {
                System.out.println("ERROR: Allies' username is empty!");
                throw new ServletException("ERROR: Allies' username is empty!");
            }
        } else {
            System.out.println("ERROR: UBoat's username is empty!");
            throw new ServletException("ERROR: UBoat's username is empty!");
        }
    }

    /** This GET request is used when a TimerTask checks if the contest needs to start **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uBoatUsername = request.getParameter("username");
        if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
            ReadyManager readyManager = UBoatServletUtils.getReadyManager(getServletContext());
            if (readyManager.startContest(uBoatUsername)) {
                System.out.println(uBoatUsername + "'s contest is about to start!");
                updateBattlefieldStatus(uBoatUsername);
                response.getWriter().print("true");
            } else {
                response.getWriter().print("false");
            }
        } else {
            System.out.println("ERROR: UBoat's username is empty!");
            throw new ServletException("ERROR: UBoat's username is empty!");
        }
    }

    /** Contest is unavailable **/
    private void updateBattlefieldStatus(String uBoatUsername) {
        BattlefieldManager battlefieldManager = UBoatServletUtils.getBattlefieldManager(getServletContext());
        battlefieldManager.updateBattlefieldStatus(uBoatUsername, Battlefield.Status.Unavailable);
    }

}
