package xiaozhi.modules.knowledge.dto.bot;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

@Schema(description = "External Robot (Bot) Aggregation DTO")
public class BotDTO {

    // ========== 1. SearchBot (Search Robot) ==========

    // Corresponds to /api/v1/searchbots/ask
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SearchBot Question Request")
    public static class SearchAskReq implements Serializable {
        @Schema(description = "User Question", requiredMode = Schema.RequiredMode.REQUIRED, example = "What is RAG?")
        @NotBlank(message = "Question cannot be empty")
        @JsonProperty("question")
        private String question;

        @Schema(description = "Whether to return citation", defaultValue = "false")
        @JsonProperty("quote")
        @Builder.Default
        private Boolean quote = false;

        @Schema(description = "Whether to stream return", defaultValue = "true")
        @JsonProperty("stream")
        @Builder.Default
        private Boolean stream = true;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SearchBot Question Response")
    public static class SearchAskVO implements Serializable {
        @Schema(description = "Answer Content")
        @JsonProperty("answer")
        private String answer;

        @Schema(description = "Reference Source (Value structure usually corresponds to RetrievalDTO.HitVO)")
        @JsonProperty("reference")
        private Map<String, Object> reference;
    }

    // Corresponds to /api/v1/searchbots/related_questions
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Related Questions Request")
    public static class RelatedQuestionReq implements Serializable {
        @Schema(description = "User Question", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Question cannot be empty")
        @JsonProperty("question")
        private String question;
    }

    // Corresponds to /api/v1/searchbots/mindmap
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Mind Map Request")
    public static class MindMapReq implements Serializable {
        @Schema(description = "User Question", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Question cannot be empty")
        @JsonProperty("question")
        private String question;
    }

    // ========== 2. AgentBot (Embedded Agent) ==========

    // Corresponds to /api/v1/agentbots/{id}/inputs
    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "AgentBot Input Parameters Request")
    public static class AgentInputsReq implements Serializable {
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "AgentBot Input Parameters Definition Response")
    public static class AgentInputsVO implements Serializable {
        @Schema(description = "Form variable definition list")
        @JsonProperty("variables")
        private List<Map<String, Object>> variables;
    }

    // Corresponds to /api/v1/agentbots/{id}/completions
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "AgentBot Conversation Request")
    public static class AgentCompletionReq implements Serializable {
        @Schema(description = "Input parameter values")
        @JsonProperty("inputs")
        private Map<String, Object> inputs;

        @Schema(description = "User query", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Query content cannot be empty")
        @JsonProperty("question")
        private String question;

        @Schema(description = "Whether to stream return", defaultValue = "true")
        @JsonProperty("stream")
        @Builder.Default
        private Boolean stream = true;

        @Schema(description = "Session ID")
        @JsonProperty("session_id")
        private String sessionId;
    }
}
 