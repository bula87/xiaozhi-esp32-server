package xiaozhi.modules.llm.service;

/**
 * LLM Service Interface
 * Supports calls to multiple large models
 */
public interface LLMService {

    /**
     * Generate chat record summary
     * 
     * @param conversation   Conversation content
     * @param promptTemplate Prompt template
     * @return Summary result
     */
    String generateSummary(String conversation, String promptTemplate);

    /**
     * Generate chat record summary (using default prompt)
     * 
     * @param conversation Conversation content
     * @return Summary result
     */
    String generateSummary(String conversation);

    /**
     * Generate chat record summary (specify model ID)
     * 
     * @param conversation Conversation content
     * @param modelId      Model ID
     * @return Summary result
     */
    String generateSummaryWithModel(String conversation, String modelId);

    /**
     * Generate chat record summary (specify model ID and prompt template)
     * 
     * @param conversation   Conversation content
     * @param promptTemplate Prompt template
     * @param modelId        Model ID
     * @return Summary result
     */
    String generateSummary(String conversation, String promptTemplate, String modelId);

    /**
     * Generate chat record summary (include merged historical memory)
     * 
     * @param conversation   Conversation content
     * @param historyMemory  Historical memory
     * @param promptTemplate Prompt template
     * @param modelId        Model ID
     * @return Summary result
     */
    String generateSummaryWithHistory(String conversation, String historyMemory, String promptTemplate, String modelId);

    /**
     * Check if the service is available
     * 
     * @return Availability status
     */
    boolean isAvailable();

    /**
     * Check if a specified model's service is available
     * 
     * @param modelId Model ID
     * @return Availability status
     */
    boolean isAvailable(String modelId);

    /**
     * Generate conversation title
     * 
     * @param conversation Conversation content
     * @param modelId      Model ID
     * @return Title (approximately 15 characters)
     */
    String generateTitle(String conversation, String modelId);
}
 