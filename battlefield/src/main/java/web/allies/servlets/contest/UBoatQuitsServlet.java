package web.allies.servlets.contest;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import web.uBoat.http.UBoatServletUtils;

import java.io.IOException;

@WebServlet(name = "UBoatQuitsServlet", value = "/allies/is-uboat-quit")
public class UBoatQuitsServlet extends HttpServlet {
    /** Returns true if UBoat did not quit, false otherwise **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uBoatUsername = request.getParameter("username");
        if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
            // Checks if UBoat exists
            boolean result = UBoatServletUtils.getBattlefieldManager(getServletContext()).isUBoatUsernameExists(uBoatUsername);
            if (result) {
                response.getWriter().println("true");
            } else {
                response.getWriter().println("false");
            }
            response.getWriter().flush();
        } else {
            System.out.println("ERROR: Invalid UBoat's username!");
            throw new ServletException("ERROR: Invalid UBoat's username!");
        }
    }
}
