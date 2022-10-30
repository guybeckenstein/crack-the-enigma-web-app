package web.allies.http;

import jakarta.servlet.ServletContext;

public class AlliesServletUtils {
    private static final String ALLIES_AGENT_MANAGER_ATTRIBUTE_NAME = "alliesAgentManager";
    private static final String ALLIES_CONTEST_MANAGER_ATTRIBUTE_NAME = "alliesContestManager";
    private static final String ALLIES_ENCRYPTION_MESSAGE_MANAGER_ATTRIBUTE_NAME = "alliesEncryptionMessageManager";
    private static final String ALLIES_BLOCKING_QUEUE_MANAGER_ATTRIBUTE_NAME = "alliesBlockingQueueManager";
    private static final String ALLIES_CANDIDATES_MANAGER_ATTRIBUTE_NAME = "alliesCandidatesManager";
    private static final Object alliesAgentManagerLock = new Object(); // Dummy object
    private static final Object alliesContestManagerLock = new Object(); // Dummy object
    private static final Object alliesEncryptionMessageManagerLock = new Object(); // Dummy object
    private static final Object alliesBlockingQueueManagerLock = new Object(); // Dummy object
    private static final Object alliesCandidatesManagerLock = new Object(); // Dummy object

    public static AgentsManager getAgentsMap(ServletContext servletContext) {
        synchronized (alliesAgentManagerLock) {
            if (servletContext.getAttribute(ALLIES_AGENT_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(ALLIES_AGENT_MANAGER_ATTRIBUTE_NAME, new AgentsManager());
            }
        }
        return (AgentsManager) servletContext.getAttribute(ALLIES_AGENT_MANAGER_ATTRIBUTE_NAME);
    }

    public static ContestManager getContestManagerMap(ServletContext servletContext) {
        synchronized (alliesContestManagerLock) {
            if (servletContext.getAttribute(ALLIES_CONTEST_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(ALLIES_CONTEST_MANAGER_ATTRIBUTE_NAME, new ContestManager());
            }
        }
        return (ContestManager) servletContext.getAttribute(ALLIES_CONTEST_MANAGER_ATTRIBUTE_NAME);
    }

    public static EncryptionMessageManager getEncryptionMessageManagerMap(ServletContext servletContext) {
        synchronized (alliesEncryptionMessageManagerLock) {
            if (servletContext.getAttribute(ALLIES_ENCRYPTION_MESSAGE_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(ALLIES_ENCRYPTION_MESSAGE_MANAGER_ATTRIBUTE_NAME, new EncryptionMessageManager());
            }
        }
        return (EncryptionMessageManager) servletContext.getAttribute(ALLIES_ENCRYPTION_MESSAGE_MANAGER_ATTRIBUTE_NAME);
    }

    public static BlockingQueueManager getBlockingQueueManagerMap(ServletContext servletContext) {
        synchronized (alliesBlockingQueueManagerLock) {
            if (servletContext.getAttribute(ALLIES_BLOCKING_QUEUE_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(ALLIES_BLOCKING_QUEUE_MANAGER_ATTRIBUTE_NAME, new BlockingQueueManager());
            }
        }
        return (BlockingQueueManager) servletContext.getAttribute(ALLIES_BLOCKING_QUEUE_MANAGER_ATTRIBUTE_NAME);
    }

    public static CandidatesManager getCandidatesManager(ServletContext servletContext) {

        synchronized (alliesCandidatesManagerLock) {
            if (servletContext.getAttribute(ALLIES_CANDIDATES_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(ALLIES_CANDIDATES_MANAGER_ATTRIBUTE_NAME, new CandidatesManager());
            }
        }
        return (CandidatesManager) servletContext.getAttribute(ALLIES_CANDIDATES_MANAGER_ATTRIBUTE_NAME);
    }
}
