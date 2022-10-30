package web.allies.servlets.contest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.agent.http.AgentServletUtils;
import web.agent.http.InformationForDmManager;
import web.allies.http.AlliesServletUtils;
import web.allies.http.BlockingQueueManager;
import web.uBoat.http.UBoatServletUtils;

import java.io.IOException;

@WebServlet(name = "RemoveAlliesServlet", value = "/allies/remove")
public class RemoveAlliesServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String alliesUsername = request.getParameter("team");
        String isLogout = request.getParameter("logout");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            if (isLogout != null && !isLogout.isEmpty()) {
                // Removes Allies' candidates manager item
                AlliesServletUtils.getCandidatesManager(getServletContext()).removeAlliesFromContest(alliesUsername);
                if (isLogout.equals("yes")) {
                    // Removes all Allies' agents
                    AlliesServletUtils.getAgentsMap(getServletContext()).removeAllies(alliesUsername);
                    // Remove UBoat's username
                    getServletContext().setAttribute("username", alliesUsername);
                    getServletContext().getRequestDispatcher("/username").include(request, response);
                    System.out.println("Logging out...");
                    response.getWriter().write("logout");
                    response.getWriter().flush();
                    // Update contest information
                    String uBoatUsername = request.getParameter("username");
                    if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
                        UBoatServletUtils.getBattlefieldManager(getServletContext()).getBattlefield(uBoatUsername).decreaseRegisteredTeams();
                    } else {
                        System.out.println("ERROR: Invalid UBoat username!");
                        throw new ServletException("ERROR: Invalid UBoat username!");
                    }
                }
                // Removes blocking queue manager item
                BlockingQueueManager blockingQueueManagerMap = AlliesServletUtils.getBlockingQueueManagerMap(getServletContext());
                blockingQueueManagerMap.removeBlockingQueue(alliesUsername);
                System.out.println("Successfully removed " + alliesUsername + " from contest!");
                // Removes information for Agent
                InformationForDmManager informationForDmManager = AgentServletUtils.getInformationForDmManager(getServletContext());
                informationForDmManager.removeInformation(alliesUsername);
                // Removes Allies' raw data item
                AlliesServletUtils.getContestManagerMap(getServletContext()).removeAllies(alliesUsername);
                // Removes Allies' encryption map item
                AlliesServletUtils.getEncryptionMessageManagerMap(getServletContext()).removeEncryptionMessage(alliesUsername);
                // Remove Allies' from battlefield map
                String uBoatUsername = UBoatServletUtils.getBattlefieldManager(getServletContext()).getUBoatUsername(alliesUsername);
                UBoatServletUtils.getReadyManager(getServletContext()).removeAlliesFromBattlefield(uBoatUsername, alliesUsername);
                // Remove Allies' from ready set
                AgentServletUtils.getAlliesReadySetManager(getServletContext()).removeAllies(alliesUsername);
            } else {
                String message = "ERROR: Invalid " + alliesUsername + "'s isLogout value!";
                System.out.println(message);
                throw new ServletException(message);
            }

        } else {
            System.out.println("ERROR: Invalid Allies' username!");
            throw new ServletException("ERROR: Invalid Allies' username!");
        }
    }
}
