package web.allies.servlets.contest.start;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.common.rawData.agents.AlliesRawData;
import web.allies.http.AlliesServletUtils;
import web.allies.http.ContestManager;
import web.allies.http.BlockingQueueManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@WebServlet(name = "TasksInformationServlet", value = "/allies/tasks-information")
public class TasksInformationServlet extends HttpServlet {
    /** Whenever Allies' TimerTask (in contest screen) needs real-time information about it's created tasks, it makes GET request to the server **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String alliesUsername = request.getParameter("team");
        if (alliesUsername != null && !alliesUsername.isEmpty()) {
            BlockingQueueManager serverBlockingQueueMap = AlliesServletUtils.getBlockingQueueManagerMap(getServletContext());
            // Send information to Allies client
            long tasksAvailable = serverBlockingQueueMap.getAvailableTasksSize(alliesUsername);
            long tasksGenerated = serverBlockingQueueMap.getTasksGenerated(alliesUsername);
            long tasksFinished = getTasksFinished(alliesUsername);
            List<Long> information = new ArrayList<>();
            information.add(tasksAvailable);
            information.add(tasksGenerated);
            information.add(tasksFinished);
            response.getWriter().println(new Gson().toJson(information));
            // Send information to Agent client
            ContestManager contestManager = AlliesServletUtils.getContestManagerMap(getServletContext());
            AlliesRawData alliesRawData = contestManager.getAlliesRawDataByUsername(alliesUsername);
            alliesRawData.setTasks(tasksAvailable);
        } else {
            System.out.println("ERROR: Invalid Allies' username!");
            throw new ServletException("ERROR: Invalid Allies' username!");
        }
    }

    private long getTasksFinished(String alliesUsername) {
        return 0; // TODO: get all values for all Agents of current Allies
    }
}
