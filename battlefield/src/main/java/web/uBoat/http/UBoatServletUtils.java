package web.uBoat.http;

import jakarta.servlet.ServletContext;

public class UBoatServletUtils {

    private static final String UBOAT_ENIGMA_ENGINE_AND_XML_MANAGER_ATTRIBUTE_NAME = "uBoatEnigmaEngineAndXmlManager";
    private static final String UBOAT_BATTLEFIELD_MANAGER_ATTRIBUTE_NAME = "uBoatBattlefieldManager";
    private static final String UBOAT_READY_MANAGER_ATTRIBUTE_NAME = "uBoatReadyManager";
    private static final String UBOAT_CANDIDATES_MANAGER_ATTRIBUTE_NAME = "uBoatCandidatesManager";

    /*
    Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exist -
    the actual fetch of them is remained un-synchronized for performance POV
     */
    private static final Object uBoatEnigmaEngineAndXmlManagerLock = new Object();
    private static final Object uBoatBattlefieldManagerLock = new Object();
    private static final Object uBoatReadyManagerLock = new Object();
    private static final Object uBoatCandidatesManagerLock = new Object();

    public static EnigmaEngineAndXmlManager getEnigmaEngineAndXmlManager(ServletContext servletContext) {

        synchronized (uBoatEnigmaEngineAndXmlManagerLock) {
            if (servletContext.getAttribute(UBOAT_ENIGMA_ENGINE_AND_XML_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(UBOAT_ENIGMA_ENGINE_AND_XML_MANAGER_ATTRIBUTE_NAME, new EnigmaEngineAndXmlManager());
            }
        }
        return (EnigmaEngineAndXmlManager) servletContext.getAttribute(UBOAT_ENIGMA_ENGINE_AND_XML_MANAGER_ATTRIBUTE_NAME);
    }

    public static BattlefieldManager getBattlefieldManager(ServletContext servletContext) {

        synchronized (uBoatBattlefieldManagerLock) {
            if (servletContext.getAttribute(UBOAT_BATTLEFIELD_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(UBOAT_BATTLEFIELD_MANAGER_ATTRIBUTE_NAME, new BattlefieldManager());
            }
        }
        return (BattlefieldManager) servletContext.getAttribute(UBOAT_BATTLEFIELD_MANAGER_ATTRIBUTE_NAME);
    }

    public static ReadyManager getReadyManager(ServletContext servletContext) {

        synchronized (uBoatReadyManagerLock) {
            if (servletContext.getAttribute(UBOAT_READY_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(UBOAT_READY_MANAGER_ATTRIBUTE_NAME, new ReadyManager());
            }
        }
        return (ReadyManager) servletContext.getAttribute(UBOAT_READY_MANAGER_ATTRIBUTE_NAME);
    }

    public static CandidatesManager getCandidatesManager(ServletContext servletContext) {

        synchronized (uBoatCandidatesManagerLock) {
            if (servletContext.getAttribute(UBOAT_CANDIDATES_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(UBOAT_CANDIDATES_MANAGER_ATTRIBUTE_NAME, new CandidatesManager());
            }
        }
        return (CandidatesManager) servletContext.getAttribute(UBOAT_CANDIDATES_MANAGER_ATTRIBUTE_NAME);
    }
}
