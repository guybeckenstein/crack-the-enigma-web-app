package web.uBoat.servlets.contest.pre;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.uBoat.http.UBoatServletUtils;
import web.uBoat.http.BattlefieldManager;

import java.io.IOException;
import java.util.Properties;

@WebServlet(name = "SetMessageServlet", urlPatterns = "/uboat/set-message")
public class SetMessageServlet extends HttpServlet {

    /** When an UBoat sends its encrypted message to all Allies' teams **/
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");

        Properties properties = new Properties();
        properties.load(request.getInputStream());

        // Gets body parameters by separating them (otherwise it doesn't work)
        String[] keys = properties.getProperty("username").split("&");

        String username = keys[0];
        String message = keys[1].substring(8);

        if (username != null && !username.isEmpty()) {
            if (!message.isEmpty()) {
                BattlefieldManager battlefieldManager = UBoatServletUtils.getBattlefieldManager(getServletContext());
                battlefieldManager.addEncryptionMessage(username, message);
                System.out.println(username + "'s encryption message is   -->   " + message);
            } else {
                System.out.println("ERROR: Invalid encryption message!");
                throw new ServletException("ERROR: Invalid encryption message!");
            }
        } else {
            System.out.println("ERROR: Invalid username!");
            throw new ServletException("ERROR: Invalid username!");
        }
    }

    /** When an Allies' team needs the current encrypted message (within TimerTask) **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uBoatUsername = request.getParameter("username");

        if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
            BattlefieldManager battlefieldManager = UBoatServletUtils.getBattlefieldManager(getServletContext());
            String message = battlefieldManager.getEncryptionMessage(uBoatUsername);
            System.out.println(uBoatUsername + "'s encryption message is   -->   " + message);
            response.getWriter().print(message);
            response.getWriter().flush();
        } else {
            System.out.println("ERROR: Invalid username!");
            throw new ServletException("ERROR: Invalid username!");
        }
    }
}