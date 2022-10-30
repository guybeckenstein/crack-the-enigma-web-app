package web.allies.blockingQueue.producer;

import jar.dto.ConfigurationDTO;
import jar.clients.battlefield.Difficulty;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractProducer implements Runnable {
    protected final BlockingQueue<ConfigurationDTO> queue;
    protected final int missionSize;
    protected final long totalTasks; // total machine codes to be inserted
    protected final Difficulty difficulty;
    protected final int totalReflectors;
    protected final int totalRotors;
    // Counts total produced tasks

    protected final AtomicLong tasksGeneratedCounter;

    public AbstractProducer(BlockingQueue<ConfigurationDTO> queue, int missionSize, long totalTasks, Difficulty difficulty, int totalReflectors, int totalRotors) {
        this.queue = queue;
        this.missionSize = missionSize;
        this.totalTasks = totalTasks;
        this.difficulty = difficulty;
        this.totalReflectors = totalReflectors;
        this.totalRotors = totalRotors;

        tasksGeneratedCounter = new AtomicLong(0);
    }

    public BlockingQueue<ConfigurationDTO> getQueue() {
        return queue;
    }

    public long getAvailableTasksSize() {
        return queue.size();
    }

    public long getTasksGenerated() {
        return tasksGeneratedCounter.get();
    }
}