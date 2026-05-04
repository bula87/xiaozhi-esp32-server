package xiaozhi.modules.knowledge.dto.dataset;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;

/**
 * Knowledge Library Management Aggregation DTO
 * <p>
 * Container class, containing static inner class definitions for all request/response objects of the knowledge library module.
 * </p>
 */
@Schema(description = "Knowledge Library Management Aggregation DTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatasetDTO {

    // ========== Common Inner Classes ==========

    /**
     * Parser Configuration
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Parser Configuration")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParserConfig implements Serializable {

        @Schema(description = "Chunk Token Number", example = "128")
        @JsonProperty("chunk_token_num")
        private Integer chunkTokenNum;

        @Schema(description = "Delimiter", example = "\\n!?;。；！？")
        private String delimiter;

        @Schema(description = "Layout Recognition Model: DeepDOC / Simple", example = "DeepDOC")
        @JsonProperty("layout_recognize")
        private String layoutRecognize;

        @Schema(description = "Whether to Convert Excel to HTML", example = "false")
        private Boolean html4excel;

        @Schema(description = "Number of Auto-Generated Keywords (0 means disabled)", example = "0")
        @JsonProperty("auto_keywords")
        private Integer autoKeywords;

        @Schema(description = "Number of Auto-Generated Questions (0 means disabled)", example = "0")
        @JsonProperty("auto_questions")
        private Integer autoQuestions;
    }

    // ========== Request Classes ==========

    /**
     * Create Knowledge Library Request (Mapped Interface 1: create)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Create Knowledge Library Request")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreateReq implements Serializable {

        @NotBlank(message = "Knowledge Library Name Cannot Be Empty")
        @Schema(description = "Knowledge Library Name", requiredMode = Schema.RequiredMode.REQUIRED, example = "my_dataset")
        private String name;

        @Schema(description = "Knowledge Library Avatar (Base64 Encoded)", example = "")
        private String avatar;

        @Schema(description = "Knowledge Library Description", example = "Used to store product documents")
        private String description;

        @Schema(description = "Embedding Model Name", example = "BAAI/bge-large-zh-v1.5")
        @JsonProperty("embedding_model")
        private String embeddingModel;

        @Schema(description = "Permission Settings: me / team", example = "me")
        private String permission;

        @Schema(description = "Chunk Method: naive / manual / qa / table / paper / book / laws / presentation / picture / one / knowledge_graph / email", example = "naive")
        @JsonProperty("chunk_method")
        private String chunkMethod;

        @Schema(description = "Parser Configuration")
        @JsonProperty("parser_config")
        private ParserConfig parserConfig;
    }

    /**
     * Update Knowledge Library Request (Mapped Interface 4: update)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Update Knowledge Library Request")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UpdateReq implements Serializable {

        @Schema(description = "Knowledge Library Name", example = "updated_dataset")
        private String name;

        @Schema(description = "Knowledge Library Avatar (Base64 Encoded)", example = "")
        private String avatar;

        @Schema(description = "Knowledge Library Description", example = "Updated Description")
        private String description;

        @Schema(description = "Permission Settings: me / team", example = "team")
        private String permission;

        @Schema(description = "Embedding Model Name", example = "BAAI/bge-large-zh-v1.5")
        @JsonProperty("embedding_model")
        private String embeddingModel;

        @Schema(description = "Chunk Method: naive / manual / qa / table / paper / book / laws / presentation / picture / one / knowledge_graph / email", example = "naive")
        @JsonProperty("chunk_method")
        private String chunkMethod;

        @Schema(description = "Parser Configuration")
        @JsonProperty("parser_config")
        private ParserConfig parserConfig;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(description = "PageRank Weight (0-100)", example = "50")
        private Integer pagerank;
    }

    /**
     * Query Knowledge Library List Request (Mapped Interface 3: list_datasets)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Query Knowledge Library List Request")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListReq implements Serializable {

        @Schema(description = "Page Number (Starting from 1)", example = "1")
        private Integer page;

        @Schema(description = "Number of Items per Page", example = "30")
        @JsonProperty("page_size")
        private Integer pageSize;

        @Schema(description = "Sort Field: create_time / update_time", example = "create_time")
        private String orderby;

        @Schema(description = "Descending Order", example = "true")
        private Boolean desc;

        @Schema(description = "Filter by Name (Fuzzy Match)", example = "my_dataset")
        private String name;

        @Schema(description = "Filter by Knowledge Library ID", example = "abc123")
        private String id;
    }

    /**
     * Batch Delete Knowledge Library Request (Mapped Interface 2: delete)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Batch Delete Knowledge Library Request")
    public static class BatchIdReq implements Serializable {

        @NotNull(message = "Knowledge Library ID List Cannot Be Empty")
        @Size(min = 1, message = "At Least One Knowledge Library ID is Required")
        @Schema(description = "Knowledge Library ID List", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"id1\", \"id2\"]")
        private List<String> ids;
    }

    /**
     * Run GraphRAG Request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Run GraphRAG Request")
    public static class RunGraphRagReq implements Serializable {

        @Schema(description = "Entity Type List", example = "[\"person\", \"organization\"]")
        @JsonProperty("entity_types")
        private List<String> entityTypes;

        @Schema(description = "Build Method: light / fast / full", example = "light")
        private String method;
    }

    /**
     * Run RAPTOR Request
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Run RAPTOR Request")
    public static class RunRaptorReq implements Serializable {

        @Schema(description = "Max Clusters", example = "64")
        @JsonProperty("max_cluster")
        private Integer maxCluster;

        @Schema(description = "Custom Prompt", example = "Please summarize the following content...")
        private String prompt;
    }

    /**
     * Asynchronous Task ID Response VO (Mapped Interface 7/8: run_graphrag/run_raptor)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Asynchronous Task ID Response")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TaskIdVO implements Serializable {

        @Schema(description = "GraphRAG Task ID", example = "task_uuid_12345678")
        @JsonProperty("graphrag_task_id")
        private String graphragTaskId;

        @Schema(description = "RAPTOR Task ID", example = "task_uuid_87654321")
        @JsonProperty("raptor_task_id")
        private String raptorTaskId;
    }

    // ========== Response Classes ==========

    /**
     * Knowledge Library Detail VO (Returned Data Item of Mapped Interface 1/3)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Knowledge Library Detail VO")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InfoVO implements Serializable {

        @Schema(description = "Knowledge Library ID", example = "abc123")
        private String id;

        @Schema(description = "Knowledge Library Name", example = "my_dataset")
        private String name;

        @Schema(description = "Knowledge Library Avatar (Base64 Encoded)", example = "")
        private String avatar;

        @Schema(description = "Tenant ID", example = "tenant_001")
        @JsonProperty("tenant_id")
        private String tenantId;

        @Schema(description = "Knowledge Library Description", example = "Used to store product documents")
        private String description;

        @Schema(description = "Embedding Model Name", example = "BAAI/bge-large-zh-v1.5")
        @JsonProperty("embedding_model")
        private String embeddingModel;

        @Schema(description = "Permission Settings: me / team", example = "me")
        private String permission;

        @Schema(description = "Chunk Method", example = "naive")
        @JsonProperty("chunk_method")
        private String chunkMethod;

        @Schema(description = "Parser Configuration")
        @JsonProperty("parser_config")
        private ParserConfig parserConfig;

        @Schema(description = "Total Number of Chunks", example = "1024")
        @JsonProperty("chunk_count")
        private Long chunkCount;

        @Schema(description = "Total Number of Documents", example = "50")
        @JsonProperty("document_count")
        private Long documentCount;

        @Schema(description = "Creation Time (Timestamp)", example = "1700000000000")
        @JsonProperty("create_time")
        private Long createTime;

        @Schema(description = "Last Update Time (Timestamp)", example = "1700000001000")
        @JsonProperty("update_time")
        private Long updateTime;

        @Schema(description = "Total Number of Tokens", example = "102400")
        @JsonProperty("token_num")
        private Long tokenNum;

        @Schema(description = "Creation Date (Format: yyyy-MM-dd HH:mm:ss)")
        @JsonProperty("create_date")
        private String createDate;

        @Schema(description = "Last Update Date (Format: yyyy-MM-dd HH:mm:ss)")
        @JsonProperty("update_date")
        private String updateDate;
    }

    /**
     * Batch Operation Response VO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Batch Operation Response VO")
    public static class BatchOperationVO implements Serializable {

        @Schema(description = "Number of Successful Operations", example = "5")
        @JsonProperty("success_count")
        private Integer successCount;

        @Schema(description = "Error List")
        private List<Object> errors;
    }

    // ========== Knowledge Graph Related ==========

    /**
     * Knowledge Graph Data VO (Mapped Interface 5: knowledge_graph)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Knowledge Graph Data VO")
    public static class GraphVO implements Serializable {

        @Schema(description = "Graph Nodes List")
        private List<Node> nodes;

        @Schema(description = "Graph Edges List")
        private List<Edge> edges;

        @Schema(description = "Mind Map Data")
        @JsonProperty("mind_map")
        private Map<String, Object> mindMap;

        /**
         * Graph Node
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @Schema(description = "Graph Node")
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Node implements Serializable {

            @Schema(description = "Node ID", example = "node_001")
            private String id;

            @Schema(description = "Node Label", example = "Product")
            private String label;

            @Schema(description = "PageRank Value", example = "0.85")
            private Double pagerank;

            @Schema(description = "Node Color", example = "#FF5733")
            private String color;

            @Schema(description = "Node Image URL", example = "https://example.com/icon.png")
            private String img;
        }

        /**
         * Graph Edge
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @Schema(description = "Graph Edge")
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Edge implements Serializable {

            @Schema(description = "Source Node ID", example = "node_001")
            private String source;

            @Schema(description = "Target Node ID", example = "node_002")
            private String target;

            @Schema(description = "Edge Weight", example = "0.75")
            private Double weight;

            @Schema(description = "Edge Label (Relationship Description)", example = "Belongs To")
            private String label;
        }
    }

    // ========== Asynchronous Task Tracking (GraphRAG/RAPTOR) ==========

    /**
     * Asynchronous Task Tracking VO (Mapped Interface 9/10: Task Progress Return)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Asynchronous Task Tracking VO")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TaskTraceVO implements Serializable {

        @Schema(description = "Task ID", example = "task_001")
        private String id;

        @Schema(description = "Document ID", example = "doc_001")
        @JsonProperty("doc_id")
        private String docId;

        @Schema(description = "Starting Page Number", example = "1")
        @JsonProperty("from_page")
        private Integer fromPage;

        @Schema(description = "Ending Page Number", example = "10")
        @JsonProperty("to_page")
        private Integer toPage;

        @Schema(description = "Progress Percentage (0.0 - 1.0)", example = "0.75")
        private Double progress;

        @Schema(description = "Progress Message", example = "Processing Page 5...")
        @JsonProperty("progress_msg")
        private String progressMsg;

        @Schema(description = "Creation Time (Timestamp)", example = "1700000000000")
        @JsonProperty("create_time")
        private Long createTime;

        @Schema(description = "Last Update Time (Timestamp)", example = "1700000001000")
        @JsonProperty("update_time")
        private Long updateTime;
    }
}
 