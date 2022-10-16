package decryptionManager;

import controllersUBoat.ContestController;
import decryptionManager.agent.AgentThreadFactory;
import decryptionManager.producer.BlockingProducer;
import decryptionManager.threadPool.AgentTask;
import decryptionManager.threadPool.ThreadPool;
import engine.dto.ConfigurationDTO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.util.Pair;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class DecryptionManagerTask extends Task<Boolean> {

    private final static int BLOCKING_QUEUE_SIZE = 1000; // limit so the thread pool won't be overflowed
    private final String userOutput; // original configuration from user
    private final int agents; // num of agents
    private final long totalMissions; // total missions for the agents
    private final int missionSize; // each mission size for agent
    private final int totalReflectors; // Difficulty level: Medium-Impossible
    private final int totalRotors; // Difficulty level: Impossible
    private final Difficulty difficulty;
    private final ContestController controller; // GUI to update
    private final Queue<Candidate> candidates;
    private Instant taskStart;
    private Instant taskEnd;

    public DecryptionManagerTask(String userOutput, long limit, int agents, int missionSize, Difficulty difficulty,
                                 int totalReflectors, int totalRotors, ContestController controller, Runnable onFinish) {
        this.userOutput = userOutput;
        this.agents = agents;
        this.missionSize = missionSize;
        this.difficulty = difficulty;
        this.totalReflectors = totalReflectors;
        this.totalRotors = totalRotors;

        this.controller = controller;
        this.controller.bindTaskToUIComponents(this, onFinish);

        candidates = new ConcurrentLinkedQueue<>();
        totalMissions = agents * limit;
        updateProgress(0, totalMissions);

        Platform.setImplicitExit(false);
    }

    @Override
    protected Boolean call() throws Exception {
        BlockingQueue<ConfigurationDTO> dmBlockingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_SIZE);
        BlockingProducer dmBlockingProducer = new BlockingProducer(
                dmBlockingQueue, missionSize, totalMissions, difficulty, totalReflectors, totalRotors, controller.getTaskCurrentConfiguration().getMachineCode());
        Thread blockingProducerThread = new Thread(dmBlockingProducer, "BLOCKING -->");
        blockingProducerThread.start();

        taskStart = Instant.now();
        final AtomicLong iterations = new AtomicLong(1);

        ThreadPoolExecutor dmExecutor = new ThreadPool(agents, agents, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(agents), // thread pool's blocking queue for consumers
                new AgentThreadFactory("Agent #"), new CustomRejectionHandler());
        dmExecutor.prestartAllCoreThreads();

        List<Pair<Candidate, Double>> candidatesPairs = new CopyOnWriteArrayList<>();
        while (iterations.get() <= totalMissions) {
            ConfigurationDTO configurationDTO = dmBlockingQueue.take();
            dmExecutor.getQueue().put(new AgentTask(controller.getTaskCurrentConfiguration().deepClone(),
                    configurationDTO, userOutput, missionSize, candidatesPairs));
            Thread.sleep(1);
            try {
                for (Pair<Candidate, Double> candidate : candidatesPairs) {
                    candidates.add(candidate.getKey());
                }
                candidatesPairs.removeAll(candidatesPairs);

                updateProgress(iterations.get(), totalMissions);
                iterations.incrementAndGet();
                Platform.runLater(() -> controller.updateValues(candidates));
            } catch (RejectedExecutionException e) {
                System.err.println("task rejected " + e.getMessage());
            } catch (Exception e) { // Interrupts if there is any possible error
                e.printStackTrace();
            }
        }
        dmExecutor.shutdownNow();
        blockingProducerThread.interrupt();
        taskEnd = Instant.now();
        Platform.runLater(() -> {
            controller.updateValues(candidates);
            controller.unbindTaskFromUIComponents(getTimeElapsed(), true); // If finished we unbind tasks from UI
        });
        return true;
    }

    public String getTimeElapsed() {
        taskEnd = Instant.now();
        return Double.toString((double)Duration.between(taskStart, taskEnd).toMillis() / 1_000);
    }

    private static class CustomRejectionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) { // Logging
            System.out.println(Thread.currentThread().getName() + " is rejected.");
        }
    }
}
