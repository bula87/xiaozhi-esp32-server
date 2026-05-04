package xiaozhi.modules.knowledge.dto.chat;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

/**
 * Chat management aggregation DTO
 * <p>
 * Container class containing all request/response objects for assistant, session, and message.
 * </p>
 */
@Schema(description = "Chat management aggregation DTO")
public class ChatDTO {

    // ========== 1. Assistant/Bot related ==========

    /**
     * Prompt configuration
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Prompt configuration")
    public static class PromptConfig implements Serializable {

        @Schema(description = "System prompt", example = "You are a professional customer service assistant...")
        @JsonProperty("prompt")
        private String systemPrompt;

        @Schema(description = "Opening statement", example = "Hello, I am your intelligent assistant. What can I help you with?")
        private String opener;

        @Schema(description = "Empty response", example = "Sorry, I could not find any relevant information.")
        @JsonProperty("empty_response")
        private String emptyResponse;

        @Schema(description = "Whether to show quotes", example = "true")
        @JsonProperty("show_quote")
        private Boolean quote;

        @Schema(description = "Whether to enable TTS", example = "false")
        private Boolean tts;

        @Schema(description = "Similarity threshold (0.0 - 1.0)", example = "0.2")
        @JsonProperty("similarity_threshold")
        private Float similarityThreshold;

        @Schema(description = "Keyword similarity weight (0.0 - 1.0)", example = "0.7")
        @JsonProperty("keywords_similarity_weight")
        private Float vectorSimilarityWeight;

        @Schema(description = "Retrieve Top N", example = "6")
        @JsonProperty("top_n")
        private Integer topK;

        @Schema(description = "Rerank model", example = "rerank_model_001")
        @JsonProperty("rerank_model")
        private String rerankId;

        @Schema(description = "Whether to enable multi-turn conversation optimization", example = "false")
        @JsonProperty("refine_multiturn")
        private Boolean refineMultigraph;

        @Schema(description = "Variable list")
        private List<Map<String, Object>> variables;
    }

    /**
     * LLM configuration
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "LLM model configuration")
    public static class LLMConfig implements Serializable {

        @NotBlank(message = "Model name cannot be empty")
        @Schema(description = "Model name", requiredMode = Schema.RequiredMode.REQUIRED, example = "gpt-4")
        @JsonProperty("model_name")
        private String modelName;

        @Schema(description = "Temperature parameter (0.0 - 2.0)", example = "0.7")
        private Float temperature;

        @Schema(description = "Top P sampling", example = "0.9")
        @JsonProperty("top_p")
        private Float topP;

        @Schema(description = "Maximum Token count", example = "4096")
        @JsonProperty("max_tokens")
        private Integer maxTokens;

        @Schema(description = "Presence penalty", example = "0.0")
        @JsonProperty("presence_penalty")
        private Float presencePenalty;

        @Schema(description = "Frequency penalty", example = "0.0")
        @JsonProperty("frequency_penalty")
        private Float frequencyPenalty;
    }

    /**
     * Create assistant request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Create assistant request")
    public static class AssistantCreateReq implements Serializable {

        @NotBlank(message = "Assistant name cannot be empty")
        @Schema(description = "Assistant name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Intelligent customer service assistant")
        private String name;

        @Schema(description = "Assistant avatar (Base64 encoded)", example = "")
        private String avatar;

        @Schema(description = "Associated knowledge base ID list", example = "[\"kb_001\", \"kb_002\"]")
        @JsonProperty("dataset_ids")
        private List<String> datasetIds;

        @Schema(description = "Assistant description", example = "This is an intelligent customer service assistant")
        private String description;

        @Schema(description = "LLM model configuration")
        @JsonProperty("llm")
        private LLMConfig llm;

        @Schema(description = "Prompt configuration")
        @JsonProperty("prompt")
        private PromptConfig promptConfig;
    }

    /**
     * Update assistant request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Update assistant request")
    public static class AssistantUpdateReq implements Serializable {

        @Schema(description = "Assistant name", example = "Intelligent customer service assistant V2")
        private String name;

        @Schema(description = "Assistant avatar (Base64 encoded)", example = "")
        private String avatar;

        @Schema(description = "Associated knowledge base ID list", example = "[\"kb_001\", \"kb_002\"]")
        @JsonProperty("dataset_ids")
        private List<String> datasetIds;

        @Schema(description = "Assistant description", example = "This is an intelligent customer service assistant")
        private String description;

        @Schema(description = "LLM model configuration")
        @JsonProperty("llm")
        private LLMConfig llm;

        @Schema(description = "Prompt configuration")
        @JsonProperty("prompt")
        private PromptConfig promptConfig;
    }

    /**
     * Query assistant list request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Query assistant list request")
    public static class AssistantListReq implements Serializable {

        @Schema(description = "Page number (starting from 1)", example = "1")
        private Integer page;

        @Schema(description = "Number of items per page", example = "30")
        @JsonProperty("page_size")
        private Integer pageSize;

        @Schema(description = "Filter by name (fuzzy match)", example = "customer service")
        private String name;

        @Schema(description = "Sort field: create_time / update_time", example = "create_time")
        private String orderby;

        @Schema(description = "Whether to sort in descending order", example = "true")
        private Boolean desc;

        @Schema(description = "Filter by ID (exact match)", example = "assistant_001")
        private String id;
    }

    /**
     * Assistant details VO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Assistant details VO")
    public static class AssistantVO implements Serializable {

        @Schema(description = "Assistant ID", example = "assistant_001")
        private String id;

        @Schema(description = "Tenant ID", example = "tenant_001")
        @JsonProperty("tenant_id")
        private String tenantId;

        @Schema(description = "Assistant name", example = "Intelligent customer service assistant")
        private String name;

        @Schema(description = "Assistant avatar", example = "")
        private String avatar;

        @Schema(description = "Associated knowledge base ID list")
        @JsonProperty("dataset_ids")
        private List<String> datasetIds;

        @Schema(description = "Associated knowledge base list (details)")
        private List<SimpleDatasetVO> datasets;

        @Schema(description = "Assistant description")
        private String description;

        @Schema(description = "LLM model configuration")
        @JsonProperty("llm")
        private LLMConfig llm;

        @Schema(description = "Prompt configuration")
        @JsonProperty("prompt")
        private PromptConfig promptConfig;

        @Schema(description = "Create time (timestamp)", example = "1700000000000")
        @JsonProperty("create_time")
        private Long createTime;

        @Schema(description = "Update time (timestamp)", example = "1700000001000")
        @JsonProperty("update_time")
        private Long updateTime;
    }

    /**
     * Delete assistant request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Delete assistant request")
    public static class AssistantDeleteReq implements Serializable {

        @Schema(description = "List of assistant IDs to delete", example = "[\"assistant_001\", \"assistant_002\"]")
        private List<String> ids;
    }

    // ========== 2. Session related ==========

    /**
     * Create session request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Create session request")
    public static class SessionCreateReq implements Serializable {

        @Schema(description = "Session name", example = "Technical consultation session")
        private String name;

        @Schema(description = "User ID", example = "user_001")
        @JsonProperty("user_id")
        private String userId;
    }

    /**
     * Update session request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Update session request")
    public static class SessionUpdateReq implements Serializable {

        @Schema(description = "Session name", example = "Technical consultation session - Updated")
        private String name;
    }

    /**
     * Query session list request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Query session list request")
    public static class SessionListReq implements Serializable {

        @Schema(description = "Assistant ID", example = "assistant_001")
        @JsonProperty("assistant_id")
        private String assistantId;

        @Schema(description = "Page number (starting from 1)", example = "1")
        private Integer page;

        @Schema(description = "Number of items per page", example = "30")
        @JsonProperty("page_size")
        private Integer pageSize;

        @Schema(description = "Filter by name", example = "Technical")
        private String name;

        @Schema(description = "Sort field", example = "create_time")
        private String orderby;

        @Schema(description = "Whether to sort in descending order", example = "true")
        private Boolean desc;

        @Schema(description = "Session ID (exact match)", example = "session_001")
        private String id;

        @Schema(description = "User identifier filter", example = "user_001")
        @JsonProperty("user_id")
        private String userId;
    }

    /**
     * Session details VO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Session details VO")
    public static class SessionVO implements Serializable {

        @Schema(description = "Session ID", example = "session_001")
        private String id;

        @Schema(description = "Assistant ID", example = "assistant_001")
        @JsonProperty("chat_id")
        private String chatId;

        @Schema(description = "Assistant ID (for backward compatibility)", example = "assistant_001")
        @JsonProperty("assistant_id")
        private String assistantId;

        @Schema(description = "Session name", example = "Technical consultation session")
        private String name;

        @Schema(description = "Create time (timestamp)", example = "1700000000000")
        @JsonProperty("create_time")
        private Long createTime;

        @Schema(description = "Update time (timestamp)", example = "1700000001000")
        @JsonProperty("update_time")
        private Long updateTime;

        @Schema(description = "Create date", example = "2024-05-01 10:00:00")
        @JsonProperty("create_date")
        private String createDate;

        @Schema(description = "Update date", example = "2024-05-01 10:00:00")
        @JsonProperty("update_date")
        private String updateDate;

        @Schema(description = "User ID", example = "user_001")
        @JsonProperty("user_id")
        private String userId;

        @Schema(description = "Conversation history message list")
        private List<Map<String, Object>> messages;
    }

    /**
     * Delete session request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Delete session request")
    public static class SessionDeleteReq implements Serializable {

        @Schema(description = "List of session IDs to delete", example = "[\"session_001\", \"session_002\"]")
        private List<String> ids;
    }

    // ========== 3. Message/Completion related ==========

    /**
     * Send message request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Send message request")
    public static class CompletionReq implements Serializable {

        @NotBlank(message = "Question content cannot be empty")
        @Schema(description = "User question", requiredMode = Schema.RequiredMode.REQUIRED, example = "Please introduce your product")
        private String question;

        @Schema(description = "Whether to use streaming response (SSE)", example = "true")
        @Builder.Default
        private Boolean stream = true;

        @NotBlank(message = "Session ID cannot be empty")
        @Schema(description = "Session ID (optional, if not provided, a new session will be created)", example = "session_001")
        @JsonProperty("session_id")
        private String sessionId;

        @Schema(description = "Whether to show quotes", example = "true")
        private Boolean quote;

        @Schema(description = "Specify the document ID list to search (comma-separated)", example = "doc_001,doc_002")
        @JsonProperty("doc_ids")
        private String docIds;

        @Schema(description = "Metadata filter conditions")
        @JsonProperty("metadata_condition")
        private Map<String, Object> metadataCondition;
    }

    /**
     * Message response VO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Message response VO")
    public static class CompletionVO implements Serializable {

        @Schema(description = "AI answer content")
        private String answer;

        @Schema(description = "Reference information")
        private Reference reference;

        @Schema(description = "Session ID", example = "session_001")
        @JsonProperty("session_id")
        private String sessionId;

        @Schema(description = "Task ID (used for tracking streaming responses)", example = "task_001")
        @JsonProperty("task_id")
        private String taskId;

        /**
         * Reference information (retrieval hit results)
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @Schema(description = "Reference information")
        public static class Reference implements Serializable {

            @Schema(description = "List of matched document chunks")
            private List<xiaozhi.modules.knowledge.dto.document.RetrievalDTO.HitVO> chunks;

            @Schema(description = "Document aggregation information")
            @JsonProperty("doc_aggs")
            private List<DocAgg> docAggs;
        }

        /**
         * Document aggregation information
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @Schema(description = "Document aggregation information")
        public static class DocAgg implements Serializable {

            @Schema(description = "Document ID", example = "doc_001")
            @JsonProperty("doc_id")
            private String docId;

            @Schema(description = "Document name", example = "Product manual.pdf")
            @JsonProperty("doc_name")
            private String docName;

            @Schema(description = "Number of hits", example = "3")
            private Integer count;
        }
    }

    /**
     * Simple knowledge base VO (for Assistant list)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Simple knowledge base VO")
    public static class SimpleDatasetVO implements Serializable {
        @Schema(description = "Knowledge base ID")
        private String id;
        @Schema(description = "Knowledge base name")
        private String name;
        @Schema(description = "Avatar")
        private String avatar;
        @Schema(description = "Number of chunks")
        @JsonProperty("chunk_num")
        private Integer chunkNum;
    }
}
 