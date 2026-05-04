package xiaozhi.modules.knowledge.dto.document;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;

/**
 * Retrieval and metadata management aggregation DTO
 */
@Schema(description = "Retrieval and metadata management aggregation DTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RetrievalDTO {

    /**
     * Document aggregation information (VO)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Document aggregation information")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DocAggVO implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "Document name")
        @JsonProperty("doc_name")
        private String docName;

        @Schema(description = "Document ID")
        @JsonProperty("doc_id")
        private String docId;

        @Schema(description = "Count")
        private Integer count;
    }

    /**
     * Retrieval test request parameters
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Retrieval test request parameters")
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TestReq implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "Knowledge base ID list", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("dataset_ids")
        @NotEmpty(message = "Knowledge base ID list cannot be empty")
        private List<String> datasetIds;

        @Schema(description = "Document ID list (optional, used to limit retrieval scope)")
        @JsonProperty("document_ids")
        private List<String> documentIds;

        @Schema(description = "Retrieval question", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Retrieval question cannot be empty")
        private String question;

        @Schema(description = "Page number (default 1)")
        private Integer page;

        @Schema(description = "Number of items per page (default 10)")
        @JsonProperty("page_size")
        private Integer pageSize;

        @Schema(description = "Similarity threshold (default 0.2)")
        @JsonProperty("similarity_threshold")
        private Float similarityThreshold;

        @Schema(description = "Vector similarity weight (default 0.3)")
        @JsonProperty("vector_similarity_weight")
        private Float vectorSimilarityWeight;

        @Schema(description = "Return top K slices (default 1024)")
        @JsonProperty("top_k")
        private Integer topK;

        @Schema(description = "Re-ranking model ID")
        @JsonProperty("rerank_id")
        private String rerankId;

        @Schema(description = "Whether to highlight keywords")
        private Boolean highlight;

        @Schema(description = "Whether to enable keyword retrieval")
        private Boolean keyword;

        @Schema(description = "Cross-language translation list (optional)")
        @JsonProperty("cross_languages")
        private List<String> crossLanguages;

        @Schema(description = "Metadata filter conditions (JSON object)")
        @JsonProperty("metadata_condition")
        private Map<String, Object> metadataCondition;
    }

    /**
     * Retrieval hit results (VO)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Retrieval hit slice details")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HitVO implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "Slice ID", requiredMode = Schema.RequiredMode.REQUIRED)
        private String id;

        @Schema(description = "Slice content", requiredMode = Schema.RequiredMode.REQUIRED)
        private String content;

        @Schema(description = "Belonging document ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("document_id")
        private String documentId;

        @Schema(description = "Belonging knowledge base ID")
        @JsonProperty("dataset_id")
        private String datasetId;

        @Schema(description = "Document name")
        @JsonProperty("document_name")
        private String documentName;

        @Schema(description = "Document keyword")
        @JsonProperty("document_keyword")
        private String documentKeyword;

        @Schema(description = "Overall similarity", requiredMode = Schema.RequiredMode.REQUIRED)
        private Float similarity;

        @Schema(description = "Vector similarity")
        @JsonProperty("vector_similarity")
        private Float vectorSimilarity;

        @Schema(description = "Keyword similarity")
        @JsonProperty("term_similarity")
        private Float termSimilarity;

        @Schema(description = "Index position")
        private Integer index;

        @Schema(description = "Highlighted content")
        private String highlight;

        @Schema(description = "Important keyword list")
        @JsonProperty("important_keywords")
        private List<String> importantKeywords;

        @Schema(description = "Predefined question list")
        private List<String> questions;

        @Schema(description = "Image ID")
        @JsonProperty("image_id")
        private String imageId;

        @Schema(description = "Position index (RAGFlow returns nested array, e.g., [[start, end, filename]])")
        private Object positions;
    }

    /**
     * Knowledge base metadata summary (VO)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Knowledge base metadata summary information")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetaSummaryVO implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "Total number of documents", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("total_doc_count")
        private Long totalDocCount;

        @Schema(description = "Total token count", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("total_token_count")
        private Long totalTokenCount;

        @Schema(description = "File type distribution (key: file extension, value: count)")
        @JsonProperty("file_type_distribution")
        private Map<String, Long> fileTypeDistribution;

        @Schema(description = "Document status distribution (key: status code, value: count)")
        @JsonProperty("status_distribution")
        private Map<String, Long> statusDistribution;

        @Schema(description = "Custom metadata statistics (key: field name, value: count/value)")
        @JsonProperty("custom_metadata")
        private Map<String, Object> customMetadata;
    }

    /**
     * Batch update metadata request parameters
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Batch update metadata request parameters")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetaBatchReq implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "Filter: used to specify the range of documents to be updated (default all)")
        private Selector selector;

        @Schema(description = "List of metadata to add or update")
        private List<UpdateItem> updates;

        @Schema(description = "List of metadata keys to delete")
        private List<DeleteItem> deletes;

        /**
         * Document filter
         */
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "Metadata update filter")
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Selector implements Serializable {
            private static final long serialVersionUID = 1L;

            @Schema(description = "List of specified document IDs")
            @JsonProperty("document_ids")
            private List<String> documentIds;

            @Schema(description = "Metadata condition matching (key: field name, value: match value)")
            @JsonProperty("metadata_condition")
            private Map<String, Object> metadataCondition;
        }

        /**
         * Update item
         */
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "Metadata update item")
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class UpdateItem implements Serializable {
            private static final long serialVersionUID = 1L;

            @Schema(description = "Metadata key name", requiredMode = Schema.RequiredMode.REQUIRED)
            private String key;

            @Schema(description = "Metadata value", requiredMode = Schema.RequiredMode.REQUIRED)
            private Object value;
        }

        /**
         * Delete item
         */
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "Metadata delete item")
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class DeleteItem implements Serializable {
            private static final long serialVersionUID = 1L;

            @Schema(description = "Metadata key name to be deleted", requiredMode = Schema.RequiredMode.REQUIRED)
            private String key;
        }
    }

    /**
     * Recall test result aggregation response
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Recall test result aggregation response")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultVO implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "List of retrieved slice chunks")
        private List<HitVO> chunks;

        @Schema(description = "Document distribution statistics")
        @JsonProperty("doc_aggs")
        private List<DocAggVO> docAggs;

        @Schema(description = "Total number of hits")
        private Long total;
    }
}
 