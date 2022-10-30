package web.allies.servlets.dashboard;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.clients.battlefield.Battlefield;
import web.uBoat.http.BattlefieldManager;
import web.uBoat.http.UBoatServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "GetBattlefieldDataServlet", urlPatterns = "/allies/get-battlefield-data")
public class GetBattlefieldDataServlet extends HttpServlet {
    private final Gson gson = new Gson();

    /** When an Allies' team needs its Battlefield information (given through TimerTask) **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String uBoatUsername = request.getParameter("username");

        if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
            BattlefieldManager battlefieldManager = UBoatServletUtils.getBattlefieldManager(getServletContext());
            Battlefield battlefield = battlefieldManager.getBattlefield(uBoatUsername);
            String contest = gson.toJson(battlefield);

            try (PrintWriter print = response.getWriter()) {
                print.println(contest);
                print.flush();
            }
        } else {
            System.out.println("ERROR: Invalid UBoat username!");
            throw new ServletException("ERROR: Invalid UBoat username!");
        }
    }

}