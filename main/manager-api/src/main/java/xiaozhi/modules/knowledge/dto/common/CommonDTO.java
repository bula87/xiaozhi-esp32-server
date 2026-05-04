package xiaozhi.modules.knowledge.dto.common;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

@Schema(description = "Common extension function DTO")
public class CommonDTO {

    // ========== 1. Reference details (detail_share_embedded) ==========

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Reference detail request")
    public static class ReferenceDetailReq implements Serializable {
        @Schema(description = "Chunk ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Chunk ID cannot be empty")
        @JsonProperty("chunk_id")
        private String chunkId;

        @Schema(description = "Knowledge base ID")
        @JsonProperty("knowledge_id")
        private String knowledgeId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Reference detail response")
    public static class ReferenceDetailVO implements Serializable {
        @Schema(description = "Chunk ID")
        @JsonProperty("chunk_id")
        private String chunkId;

        @Schema(description = "Full content")
        @JsonProperty("content_with_weight")
        private String contentWithWeight;

        @Schema(description = "Document name")
        @JsonProperty("doc_name")
        private String docName;

        @Schema(description = "Image ID list")
        @JsonProperty("img_id")
        private String imageId; // Note: RAGFlow sometimes returns String and sometimes returns List, confirmation required based on actual situation, temporarily set as String for ID

        @Schema(description = "Document ID")
        @JsonProperty("doc_id")
        private String docId;
    }

    // ========== 2. General Q&A (ask_about) - For debugging ==========

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "General Q&A request (for debugging)")
    public static class AskAboutReq implements Serializable {
        @Schema(description = "User question", requiredMode = Schema.RequiredMode.REQUIRED, example = "What is this dataset about?")
        @NotBlank(message = "Question cannot be empty")
        @JsonProperty("question")
        private String question;

        @Schema(description = "Dataset ID list", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "Dataset list cannot be empty")
        @JsonProperty("dataset_ids")
        private List<String> datasetIds;
    }

    // Response is usually reused as String or a simple Map structure, depending on the specific implementation, not defining a dedicated VO for now
}
 