package web.agent.servlets.contest.pre;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.agent.http.AgentServletUtils;
import web.agent.http.AlliesReadySetManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ReadyAlliesServlet", value = "/agent/remove-ready-allies")
public class RemoveReadyAlliesServlet extends HttpServlet {
    private final Gson serializer = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AlliesReadySetManager alliesReadySetManager = AgentServletUtils.getAlliesReadySetManager(getServletContext());
        List<String> readyList = new ArrayList<>(alliesReadySetManager.getSet());

        response.getWriter().print(serializer.toJson(readyList));
    }
}
