package web.agent.servlets.contest.start;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jar.enigmaEngine.interfaces.EnigmaEngine;
import javafx.util.Pair;
import web.agent.http.AgentServletUtils;
import web.agent.http.InformationForDmManager;

import java.io.IOException;

@WebServlet(name = "ContestInformationServlet", value = "/agent/contest-information")
public class ContestInformationServlet extends HttpServlet {
    private final Gson serializer = new Gson();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String alliesUsername = request.getParameter("team");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            InformationForDmManager informationForDmManager = AgentServletUtils.getInformationForDmManager(getServletContext());
            Pair<Pair<Integer, EnigmaEngine>, String> taskSizeEnigmaEngineAndEncryptionInput = informationForDmManager.getInformation(alliesUsername);
            response.getWriter().print(serializer.toJson(taskSizeEnigmaEngineAndEncryptionInput));
            response.getWriter().flush();
        } else {
            System.out.println("ERROR: Invalid Allies username!");
            throw new ServletException("ERROR: Invalid Allies username!");
        }
    }
}
