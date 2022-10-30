package web.agent.servlets.contest.start;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jar.dto.ConfigurationDTO;
import web.allies.http.AlliesServletUtils;
import web.allies.http.BlockingQueueManager;

import java.io.IOException;
import java.util.Queue;

@WebServlet(name = "GetTasksServlet", value = "/agent/get-tasks")
public class GetTasksServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String alliesUsername = request.getParameter("team");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            int withdrawalSize;
            try {
                withdrawalSize = Integer.parseInt(request.getParameter("withdraw"));
            } catch (Exception e) {
                withdrawalSize = -1;
            }
            if (withdrawalSize != -1) {
                BlockingQueueManager blockingQueueManagerMap = AlliesServletUtils.getBlockingQueueManagerMap(getServletContext());
                Queue<ConfigurationDTO> tasksForAgent = blockingQueueManagerMap.getTasksForAgent(alliesUsername, withdrawalSize);
                String serializedTasks = new Gson().toJson(tasksForAgent);
                if (serializedTasks != null && !serializedTasks.isEmpty()) {
                    response.getWriter().println(serializedTasks);
                } else {
                    response.getWriter().println("the-end");
                }
                response.getWriter().flush();
            } else {
                System.out.println("ERROR: Invalid withdrawal size, unable to withdraw tasks to Agent from Allies " + alliesUsername.trim() + "!");
                throw new ServletException("ERROR: Invalid withdrawal size, unable to withdraw tasks to Agent from Allies " + alliesUsername.trim() + "!");
            }
        } else {
            System.out.println("ERROR: Invalid Allies' username!");
            throw new ServletException("ERROR: Invalid Allies' username!");
        }
    }
}
