package ui.decryptionManager.agent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public class AgentThreadFactory implements ThreadFactory {
    private int counter = 1;
    private final String prefix;

    public AgentThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        return new Thread(r, prefix + counter++);
    }
}
