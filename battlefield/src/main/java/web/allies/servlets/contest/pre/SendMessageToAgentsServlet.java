package web.allies.servlets.contest.pre;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import web.allies.http.AlliesServletUtils;
import web.allies.http.EncryptionMessageManager;

import java.io.IOException;

@WebServlet(name = "SendMessageToAgentsServlet", value = "/allies/set-message")
public class SendMessageToAgentsServlet extends HttpServlet {
    /** Whenever an Agent client needs the encryption message (in contest screen), he makes a GET request (using TimerTask) **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String alliesUsername = request.getParameter("team");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            EncryptionMessageManager encryptionMessageManagerMap = AlliesServletUtils.getEncryptionMessageManagerMap(getServletContext());
            String encryptionMessage = encryptionMessageManagerMap.getEncryptionMessageByUsername(alliesUsername);
            response.getWriter().print(encryptionMessage);
            response.getWriter().flush();
        } else {
            System.out.println("ERROR: Invalid Allies username!");
            throw new ServletException("ERROR: Invalid Allies username!");
        }
    }

    /** Whenever an Allies client gets an encryption message (in contest screen), he makes a PUT request (using TimerTask) **/
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String alliesUsername = request.getParameter("team");
        String encryptionMessage = request.getParameter("message");

        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            if (encryptionMessage != null && !encryptionMessage.isEmpty()) {
                EncryptionMessageManager encryptionMessageManagerMap = AlliesServletUtils.getEncryptionMessageManagerMap(getServletContext());
                encryptionMessageManagerMap.addEncryptionMessage(alliesUsername, encryptionMessage);
            }
        } else {
            System.out.println("ERROR: Invalid Allies username!");
            throw new ServletException("ERROR: Invalid Allies username!");
        }
    }
}
