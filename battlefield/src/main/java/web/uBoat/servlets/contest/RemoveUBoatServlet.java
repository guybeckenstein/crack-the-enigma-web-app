package web.uBoat.servlets.contest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.uBoat.http.UBoatServletUtils;

import java.io.IOException;

@WebServlet(name = "RemoveUBoatServlet", urlPatterns = "/uboat/remove")
public class RemoveUBoatServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uBoatUsername = request.getParameter("username");
        if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
            // Remove battlefield, encryption message and data
            UBoatServletUtils.getBattlefieldManager(getServletContext()).removeBattlefield(uBoatUsername);
            // Remove candidates
            UBoatServletUtils.getCandidatesManager(getServletContext()).removeContest(uBoatUsername);
            // Remove XML file DTO and EnigmaEngine
            UBoatServletUtils.getEnigmaEngineAndXmlManager(getServletContext()).removeData(uBoatUsername);
            // Remove additional information (is UBoat ready)
            UBoatServletUtils.getReadyManager(getServletContext()).removeBattlefield(uBoatUsername);
            System.out.println(uBoatUsername + " is redirected to login screen, and removed from contest.");
            // Remove UBoat's username
            getServletContext().setAttribute("username", uBoatUsername);
            getServletContext().getRequestDispatcher("/username").include(request, response);
        } else {
            System.out.println("ERROR: Invalid UBoat's username!");
            throw new ServletException("ERROR: Invalid UBoat's username!");
        }
    }
}
