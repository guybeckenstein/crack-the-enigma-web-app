package web.allies.blockingQueue;

import jar.clients.battlefield.Difficulty;
import jar.dto.ConfigurationDTO;
import web.allies.blockingQueue.producer.BlockingProducer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ServerBlockingQueue {
    private final static int BLOCKING_QUEUE_SIZE = 1000; // Limited, so the thread pool won't be overflowed
    private final BlockingProducer dmBlockingProducer;

    /**
     * @param  totalTasksWithoutDivision
     *         // Total tasks to perform (given parameter) without division, for all agents
     * @param  taskSize
     *         // Each task size for agent
     * @param  difficulty
     *         // Given from XML file
     * @param  totalReflectors
     *         // Difficulty level: Medium-Impossible
     * @param  totalRotors
     *         // Difficulty level: Impossible
     * @param  configurationDTO
     *         // Current engine's configuration DTO
     * **/
    public ServerBlockingQueue(long totalTasksWithoutDivision, int taskSize, Difficulty difficulty, int totalReflectors,
                               int totalRotors, ConfigurationDTO configurationDTO) {
        long totalTasks = totalTasksWithoutDivision / taskSize; // Total tasks to perform (given parameter), for all agents


        BlockingQueue<ConfigurationDTO> dmBlockingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_SIZE);
        dmBlockingProducer = new BlockingProducer(dmBlockingQueue, taskSize, totalTasks, difficulty,
                totalReflectors, totalRotors, configurationDTO);
        start();
    }

    private void start() {
        Thread blockingProducerThread = new Thread(dmBlockingProducer, "BLOCKING -->");
        blockingProducerThread.start();
    }

    public BlockingProducer getDmBlockingProducer() {
        return dmBlockingProducer;
    }


}
