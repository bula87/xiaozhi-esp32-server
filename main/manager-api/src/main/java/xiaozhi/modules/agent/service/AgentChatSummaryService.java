package xiaozhi.modules.agent.service;

/**
 * Agent chat record summary service interface
 */
public interface AgentChatSummaryService {

    /**
     * Generate and save chat record summary based on session ID to agent memory
     * 
     * @param sessionId Session ID
     * @return Save result
     */
    boolean generateAndSaveChatSummary(String sessionId);

    /**
     * Generate and save chat title based on session ID
     *
     * @param sessionId Session ID
     * @return Whether successful
     */
    boolean generateAndSaveChatTitle(String sessionId);
}
 