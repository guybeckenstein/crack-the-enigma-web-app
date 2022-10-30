package web.agent.http;

import jakarta.servlet.ServletContext;

public class AgentServletUtils {
    private static final String ALLIES_READY_SET_MANAGER_ATTRIBUTE_NAME = "alliesReadySetManager";
    private static final String INFORMATION_FOR_DM_MANAGER_ATTRIBUTE_NAME = "informationForDmManager";
    private static final String INFORMATION_FOR_ALLIES_MANAGER_ATTRIBUTE_NAME = "informationForAlliesManager";
    private static final Object alliesReadyManagerLock = new Object(); // Dummy object
    private static final Object informationForDmManagerLock = new Object(); // Dummy object
    private static final Object informationForAlliesManagerLock = new Object(); // Dummy Object

    public static AlliesReadySetManager getAlliesReadySetManager(ServletContext servletContext) {
        synchronized (alliesReadyManagerLock) {
            if (servletContext.getAttribute(ALLIES_READY_SET_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(ALLIES_READY_SET_MANAGER_ATTRIBUTE_NAME, new AlliesReadySetManager());
            }
        }
        return (AlliesReadySetManager) servletContext.getAttribute(ALLIES_READY_SET_MANAGER_ATTRIBUTE_NAME);
    }

    public static InformationForDmManager getInformationForDmManager(ServletContext servletContext) {
        synchronized (informationForDmManagerLock) {
            if (servletContext.getAttribute(INFORMATION_FOR_DM_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(INFORMATION_FOR_DM_MANAGER_ATTRIBUTE_NAME, new InformationForDmManager());
            }
        }
        return (InformationForDmManager) servletContext.getAttribute(INFORMATION_FOR_DM_MANAGER_ATTRIBUTE_NAME);
    }

    public static InformationForAlliesManager getInformationForAlliesManager(ServletContext servletContext) {
        synchronized (informationForAlliesManagerLock) {
            if (servletContext.getAttribute(INFORMATION_FOR_ALLIES_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(INFORMATION_FOR_ALLIES_MANAGER_ATTRIBUTE_NAME, new InformationForAlliesManager());
            }
        }
        return (InformationForAlliesManager) servletContext.getAttribute(INFORMATION_FOR_ALLIES_MANAGER_ATTRIBUTE_NAME);
    }
}
