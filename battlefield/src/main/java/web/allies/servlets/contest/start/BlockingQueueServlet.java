package web.allies.servlets.contest.start;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jar.clients.battlefield.Difficulty;
import jar.dto.XmlToServletDTO;
import jar.enigmaEngine.interfaces.EnigmaEngine;
import web.agent.http.AgentServletUtils;
import web.agent.http.InformationForDmManager;
import web.allies.http.BlockingQueueManager;
import web.allies.http.AlliesServletUtils;
import web.uBoat.http.EnigmaEngineAndXmlManager;
import web.uBoat.http.UBoatServletUtils;

@WebServlet(name = "BlockingQueueServlet", value = "/allies/blocking-queue")
public class BlockingQueueServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String uBoatUsername = request.getParameter("username");
        String alliesUsername = request.getParameter("team");
        if (uBoatUsername != null && !uBoatUsername.isEmpty()) {
            if (alliesUsername != null && !alliesUsername.isEmpty()) {
                int taskSize;
                try {
                    taskSize = Integer.parseInt(request.getParameter("taskSize"));
                } catch (Exception e) {
                    taskSize = -1;
                }
                if (taskSize != -1) {
                    Difficulty difficulty = Difficulty.valueOf(request.getParameter("difficulty"));
                    // Create blocking queue manager
                    EnigmaEngineAndXmlManager enigmaEngineAndXmlManager = UBoatServletUtils.getEnigmaEngineAndXmlManager(getServletContext());
                    XmlToServletDTO xmlDTO = enigmaEngineAndXmlManager.getXmlInformation(uBoatUsername);
                    EnigmaEngine enigmaEngine = enigmaEngineAndXmlManager.getEnigmaEngine(uBoatUsername);
                    long totalTasks = Difficulty.translateDifficultyLevelToTasks(difficulty, enigmaEngine.getEngineDTO(),
                            xmlDTO.getReflectorsFromXML().size(), xmlDTO.getAbcFromXML().size());

                    BlockingQueueManager blockingQueueManagerMap = AlliesServletUtils.getBlockingQueueManagerMap(getServletContext());
                    blockingQueueManagerMap.createBlockingQueue(alliesUsername, totalTasks, taskSize,
                            difficulty, xmlDTO.getReflectorsFromXML().size(), xmlDTO.getRotorsFromXML().size(), enigmaEngine);
                    // Add information for Agent
                    InformationForDmManager informationForDmManager = AgentServletUtils.getInformationForDmManager(getServletContext());
                    String encryptionInput = UBoatServletUtils.getReadyManager(getServletContext()).getUBoatEncryptionInputMap(uBoatUsername);
                    informationForDmManager.addInformation(alliesUsername, enigmaEngine, taskSize, encryptionInput);
                } else {
                    String message = "ERROR: Invalid withdrawal size, unable to send task size from Allies "
                            + alliesUsername.trim() + " to UBoat " + uBoatUsername.trim() + "!";
                    System.out.println(message);
                    throw new ServletException(message);
                }
            } else {
                System.out.println("ERROR: Invalid Agent's username!");
                throw new ServletException("ERROR: Invalid Agent's username!");
            }
        } else {
            System.out.println("ERROR: Invalid UBoat's username!");
            throw new ServletException("ERROR: Invalid Agent's username!");
        }
    }
}
