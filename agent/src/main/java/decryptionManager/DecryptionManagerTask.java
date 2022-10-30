package decryptionManager;

import controllersAgent.ContestController;
import jar.common.rawData.Candidate;
import jar.common.rawData.battlefieldContest.AgentCandidates;
import jar.common.rawData.battlefieldContest.TeamCandidates;
import jar.dto.ConfigurationDTO;
import jar.enigmaEngine.impl.EnigmaEngineImpl;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.util.Pair;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import timerTasks.contest.start.decryptionManager.GetBlockingQueueTimerTask;
import timerTasks.contest.start.decryptionManager.SendAgentRawDataTimerTask;
import timerTasks.contest.start.decryptionManager.SendCandidatesTimerTask;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static http.Base.BASE_URL;

public class DecryptionManagerTask extends Task<Boolean> {
    private final ContestController contestController;
    // Client information
    private final String encryptedMessage; // original configuration from user (given by UBoat client)
    private final int totalThreads; // num of threads (given by Agent client)
    private final int taskSize; // each task size for agent (given by Allies client)
    private final int tasksWithdrawalSize; // How many tasks does an agent withdraw to his threads, per full-task (given by Agent client)
    private final EnigmaEngineImpl enigmaEngine; // (given by UBoat client)
    private final Queue<Candidate> candidates; // produced by Agent client
    private final String agentUsername; // given by Agent client
    private final String alliesUsername; // given by Allies client
    // Task information
    private final Queue<ConfigurationDTO> currentTasksInThreadPool;
    private final List<String> originalEncryptionInput;

    private Instant taskStart;
    private Instant taskEnd;
    private final AtomicLong iterations;
    private final Object candidatesLock;
    private boolean stopDM;
    // Send information
    private final Timer sendCandidatesTimer;
    private final Timer getBlockingQueueTimer;
    private final Timer sendAgentRawDataTimer;

    public DecryptionManagerTask(ContestController contestController, String encryptedMessage, int totalThreads, int taskSize, int tasksWithdrawalSize,
                                 EnigmaEngineImpl enigmaEngine, String agentUsername, String alliesUsername, String originalEncryptionInput) {

        this.contestController = contestController;
        this.encryptedMessage = encryptedMessage;
        this.totalThreads = totalThreads;
        this.taskSize = taskSize;
        this.tasksWithdrawalSize = tasksWithdrawalSize;
        this.enigmaEngine = enigmaEngine;

        candidates = new ConcurrentLinkedQueue<>();

        this.agentUsername = agentUsername;
        this.alliesUsername = alliesUsername;
        this.originalEncryptionInput = Arrays.asList(originalEncryptionInput.split(" "));
        currentTasksInThreadPool = new ConcurrentLinkedQueue<>();

        iterations = new AtomicLong(1);
        candidatesLock = new Object();
        stopDM = false;

        sendCandidatesTimer = new Timer(true);
        getBlockingQueueTimer = new Timer(true);
        sendAgentRawDataTimer = new Timer(true);

        Platform.setImplicitExit(false);
    }

    /** Getters for GetBlockingQueueTimerTask **/

    public int getTasksWithdrawalSize() {
        return tasksWithdrawalSize;
    }

    public String getAgentUsername() {
        return agentUsername;
    }

    public String getAlliesUsername() {
        return alliesUsername;
    }

    public Queue<ConfigurationDTO> getCurrentTasksInThreadPool() {
        return currentTasksInThreadPool;
    }

    public synchronized void setStopDM(boolean stopDM) {
        this.stopDM = stopDM;
        sendCandidatesTimer.cancel(); // Sending candidates to server stops
        getBlockingQueueTimer.cancel(); // Pulling tasks from server stops
        sendAgentRawDataTimer.cancel(); // Sending Agent's information stops
    }

    @Override
    public Boolean call() {
        taskStart = Instant.now();

        ScheduledExecutorService dmExecutor = Executors.newScheduledThreadPool(totalThreads); // Thread pool for each agent
        List<Pair<Candidate, Double>> candidatesPairs = new CopyOnWriteArrayList<>();

        Timer getBlockingQueueTimer = new Timer(true);
        GetBlockingQueueTimerTask getBlockingQueueTimerTask = new GetBlockingQueueTimerTask(this);
        getBlockingQueueTimer.scheduleAtFixedRate(getBlockingQueueTimerTask, 0, 500);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Timer sendAgentRawDataTimer = new Timer(true);
        SendAgentRawDataTimerTask sendAgentRawDataTimerTask = new SendAgentRawDataTimerTask(this);
        sendAgentRawDataTimer.scheduleAtFixedRate(sendAgentRawDataTimerTask, 0, 500);

        while (!stopDM) {
            try {
                ConfigurationDTO currentTask = null;
                if (currentTasksInThreadPool.size() > 0) {
                    currentTask = currentTasksInThreadPool.remove();
                }
                if (currentTask != null) {
                    final ConfigurationDTO startingConfiguration = currentTask;
                    Platform.runLater(() -> contestController.updateTaskStartingConfigurationTextField(startingConfiguration.toString()));
                    dmExecutor.submit(new ExecutorServiceTask(enigmaEngine.deepClone(), currentTask, encryptedMessage, taskSize, candidatesPairs,
                            originalEncryptionInput, this));
                    for (Pair<Candidate, Double> candidate : candidatesPairs) {
                        candidates.add(candidate.getKey());
                    }

                    candidatesPairs.clear();

                    iterations.incrementAndGet();
                }
            } catch (RejectedExecutionException e) {
                System.err.println("Task rejected: " + e.getMessage());
            } catch (Exception e) { // Interrupts if there is any possible error
                e.printStackTrace();
            }
        }

        dmExecutor.shutdownNow(); // Thread pool stops
        taskEnd = Instant.now();
        updateContestScreen(0);
        Platform.runLater(() -> contestController.contestFinished(getTimeElapsed()));

        return true;
    }

    public void updateContestScreen(int currentTasksSize) {
        if (candidates.size() > 0) {
            LinkedList<AgentCandidates> agentCandidatesQueue = new LinkedList<>();
            LinkedList<Candidate> uBoatCandidatesQueue = new LinkedList<>();
            LinkedList<TeamCandidates> alliesCandidatesQueue = new LinkedList<>();
            synchronized (candidatesLock) {
                // Adapter for "Candidate to AgentCandidates", then remove all current candidates (Agent client)
                candidates.forEach(
                        candidate -> agentCandidatesQueue.add(new AgentCandidates(candidate.getDictionaryWordsMessage(),
                                candidate.getOriginalTask(), candidate.getMachineConfiguration())));
                // Allies client
                agentCandidatesQueue.forEach(candidate -> alliesCandidatesQueue.add(
                        new TeamCandidates(candidate.getCandidateMessage(), agentUsername, candidate.getCandidateConfiguration())));
                // UBoat client
                candidates.forEach((candidate -> uBoatCandidatesQueue.add(
                        new Candidate(candidate.getMachineConfiguration(),
                                candidate.getOriginalTask(), candidate.getDictionaryWordsMessage(),
                                candidate.getTimeElapsed(), candidate.getAllies()))));
                candidates.clear();
                Platform.runLater(() -> contestController.updateCandidatesTableView(agentCandidatesQueue));
            }
            sendQueuesAndAgentDataToServer(uBoatCandidatesQueue, alliesCandidatesQueue);
        }
        Platform.runLater(() -> contestController.updateProgress(currentTasksSize, iterations.get()));
    }

    private void sendQueuesAndAgentDataToServer(LinkedList<Candidate> uBoatCandidatesQueue, LinkedList<TeamCandidates> alliesCandidatesQueue) {
        SendCandidatesTimerTask sendCandidatesTimerTask = new SendCandidatesTimerTask(this, uBoatCandidatesQueue, alliesCandidatesQueue);
        sendCandidatesTimer.schedule(sendCandidatesTimerTask, 0);
    }

    public ContestController getContestController() {
        return contestController;
    }

    public String getTimeElapsed() {
        taskEnd = Instant.now();
        return Double.toString((double)Duration.between(taskStart, taskEnd).toMillis() / 1_000);
    }

    public long getIterations() {
        return iterations.get();
    }

    public void sendServerYouWon() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/agent/winning-team";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("team", getAlliesUsername());
        String finalUrl = urlBuilder.build().toString();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create("", mediaType);
        // Request + body + response
        try {
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("PUT", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("ERROR: Unable to send server the winning team!");
                    System.out.println(e.getLocalizedMessage());
                }

                @SuppressWarnings({"unused", "EmptyTryBlock"})
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        // Nothing to do...
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
