package web.allies.http;

import jar.clients.battlefield.Difficulty;
import jar.dto.ConfigurationDTO;
import jar.enigmaEngine.interfaces.EnigmaEngine;
import web.allies.blockingQueue.ServerBlockingQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

public class BlockingQueueManager {
    private final Map<String, ServerBlockingQueue> serverBlockingQueueMap; // Map<Allies' username, Unique DM blocking queue (producer)>
    private final Object blockingProducerLock;
    public BlockingQueueManager() {
        serverBlockingQueueMap = new HashMap<>();
        blockingProducerLock = new Object();
    }

    /** When starting contest **/
    public synchronized void createBlockingQueue(String username, long totalTasks, int taskSize,
                                                 Difficulty difficulty, int reflectorsAmount, int rotorsAmount, EnigmaEngine engine) {
        ServerBlockingQueue serverBlockingQueue = new ServerBlockingQueue(totalTasks, taskSize,
                difficulty, reflectorsAmount, rotorsAmount, engine.getConfigurationDTO());
        serverBlockingQueueMap.put(username, serverBlockingQueue);
    }

    /** When contest is finished **/
    public synchronized void removeBlockingQueue(String username) {
        serverBlockingQueueMap.remove(username);
    } // TODO

    /** During contest (when TimerTasks gets information about Allies) **/
    public synchronized long getAvailableTasksSize(String username) {
        synchronized (blockingProducerLock) {
            return serverBlockingQueueMap.get(username).getDmBlockingProducer().getAvailableTasksSize();
        }
    }

    /** During contest (when Agent's TimerTask pulls tasks) **/
    public Queue<ConfigurationDTO> getTasksForAgent(String username, int withdrawalSize) {
        synchronized (blockingProducerLock) {
            BlockingQueue<ConfigurationDTO> tasksBlockingQueue = serverBlockingQueueMap.get(username).getDmBlockingProducer().getQueue();
            Queue<ConfigurationDTO> tasksQueue = new ConcurrentLinkedQueue<>();

            IntStream.range(0, withdrawalSize).forEach((dummyNum) -> {
                if (tasksBlockingQueue.size() > 0) { // Sometimes withdrawal size can be bigger than actual capacity of producer blocking queue
                    tasksQueue.add(tasksBlockingQueue.remove());
                }
            });
            return tasksQueue;
        }
    }

    public synchronized long getTasksGenerated(String username) {
        synchronized (blockingProducerLock) {
            return serverBlockingQueueMap.get(username).getDmBlockingProducer().getTasksGenerated();
        }
    }
}
