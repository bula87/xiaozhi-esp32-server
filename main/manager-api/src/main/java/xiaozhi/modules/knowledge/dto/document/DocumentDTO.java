package xiaozhi.modules.knowledge.dto.document;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;

/**
 * Document management aggregation DTO
 */
@Schema(description = "Document management aggregation DTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentDTO {

    /**
     * Upload document request parameters
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Upload document request parameters")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UploadReq implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "Knowledge base ID (Must specify归属)", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("dataset_id")
        @NotBlank(message = "Knowledge base ID cannot be empty")
        private String datasetId;

        @Schema(description = "File name (If specified, it will override the original file name)")
        private String name;

        @Schema(description = "Chunking method")
        @JsonProperty("chunk_method")
        private DocumentDTO.InfoVO.ChunkMethod chunkMethod;

        @Schema(description = "Parser configuration parameters")
        @JsonProperty("parser_config")
        private DocumentDTO.InfoVO.ParserConfig parserConfig;

        @Schema(description = "Virtual folder path (Default: /)")
        @JsonProperty("parent_path")
        private String parentPath;

        @Schema(description = "Metadata fields")
        @JsonProperty("meta")
        private Map<String, Object> metaFields;

        @Schema(description = "File binary stream (Supports PDF, DOCX, TXT, MD etc. multiple formats)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Uploaded file cannot be empty")
        private org.springframework.web.multipart.MultipartFile file;
    }

    /**
     * Update document request parameters
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Update document request parameters")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UpdateReq implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "New document name (Must include file extension and cannot change the original type)")
        private String name;

        @Schema(description = "Enable/disable status (true: Enable, false: Disable; Disabled documents will not participate in retrieval)")
        private Boolean enabled;

        @Schema(description = "New parsing method (Modifying this item will reset the parsing state)")
        @JsonProperty("chunk_method")
        private InfoVO.ChunkMethod chunkMethod;

        @Schema(description = "New parser detailed configuration (Should be used in conjunction with chunk_method)")
        @JsonProperty("parser_config")
        private InfoVO.ParserConfig parserConfig;
    }

    /**
     * Get document list request parameters
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Get document list request parameters")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListReq implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "Page number (Default: 1)")
        private Integer page;

        @Schema(description = "Number of items per page (Default: 30)")
        @JsonProperty("page_size")
        private Integer pageSize;

        @Schema(description = "Sorting field (Optional: create_time, name, size; Default: create_time)")
        private String orderby;

        @Schema(description = "Whether to sort in descending order (true: Latest/Maximum first; false: Oldest/Minimum first; Default: true)")
        private Boolean desc;

        @Schema(description = "Exact filter: Document ID")
        private String id;

        @Schema(description = "Exact filter: Full document name (including extension)")
        private String name;

        @Schema(description = "Fuzzy search: Document name keyword")
        private String keywords;

        @Schema(description = "Filter: File suffix list (e.g. ['pdf', 'docx'])")
        private List<String> suffix;

        @Schema(description = "Filter: Running status list")
        private List<InfoVO.RunStatus> run;

        @Schema(description = "Filter: Start creation time (timestamp, milliseconds)")
        @JsonProperty("create_time_from")
        private Long createTimeFrom;

        @Schema(description = "Filter: End creation time (timestamp, milliseconds)")
        @JsonProperty("create_time_to")
        private Long createTimeTo;
    }

    /**
     * Batch document operation request parameters (for deletion, parsing, etc.)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Batch document operation request parameters")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BatchIdReq implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "Document ID list", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("ids") // For compatibility, also consider supporting document_ids, but here it is unified as ids
        @JsonAlias("document_ids")
        @NotEmpty(message = "Document ID list cannot be empty")
        private List<String> ids;
    }

    /**
     * Knowledge base document information VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Knowledge base document information")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InfoVO implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "Document ID (Unique identifier)", requiredMode = Schema.RequiredMode.REQUIRED)
        private String id;

        @Schema(description = "Document thumbnail URL (Base64 or link)")
        private String thumbnail;

        @Schema(description = "Belonging knowledge base ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("dataset_id")
        private String datasetId;

        @Schema(description = "Document parsing method (Determines how the document is chunked)")
        @JsonProperty("chunk_method")
        private ChunkMethod chunkMethod;

        @Schema(description = "Associated ETL Pipeline ID (If any)")
        @JsonProperty("pipeline_id")
        private String pipelineId;

        @Schema(description = "Detailed configuration of the parser")
        @JsonProperty("parser_config")
        private ParserConfig parserConfig;

        @Schema(description = "Source type (e.g. local, s3, url etc.)")
        @JsonProperty("source_type")
        private String sourceType;

        @Schema(description = "Document file type (e.g. pdf, docx, txt)", requiredMode = Schema.RequiredMode.REQUIRED)
        private String type;

        @Schema(description = "Creator user ID")
        @JsonProperty("created_by")
        private String createdBy;

        @Schema(description = "Document name (including extension)", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @Schema(description = "File storage path or location identifier")
        private String location;

        @Schema(description = "File size (unit: Bytes)")
        private Long size;

        @Schema(description = "Total number of tokens contained (Counted after parsing)")
        @JsonProperty("token_count")
        private Long tokenCount;

        @Schema(description = "Total number of chunks (chunks)")
        @JsonProperty("chunk_count")
        private Long chunkCount;

        @Schema(description = "Parsing progress (0.0 ~ 1.0, 1.0 indicates completion)")
        private Double progress;

        @Schema(description = "Current progress description or error message")
        @JsonProperty("progress_msg")
        private String progressMsg;

        @Schema(description = "Timestamp when processing began (RFC1123 format returned by RAGFlow)")
        @JsonProperty("process_begin_at")
        private String processBeginAt;

        @Schema(description = "Total processing duration (unit: seconds)")
        @JsonProperty("process_duration")
        private Double processDuration;

        @Schema(description = "Custom metadata fields (Key-Value pairs)")
        @JsonProperty("meta_fields")
        private Map<String, Object> metaFields;

        @Schema(description = "File extension (without dot)")
        private String suffix;

        @Schema(description = "Document parsing running status")
        private RunStatus run;

        @Schema(description = "Document availability status (1: Enabled/Normal, 0: Disabled/Invalid)", requiredMode = Schema.RequiredMode.REQUIRED)
        private String status;

        @Schema(description = "Creation time (timestamp, milliseconds)", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("create_time")
        private Long createTime;

        @Schema(description = "Creation date (RFC1123 format returned by RAGFlow)")
        @JsonProperty("create_date")
        private String createDate;

        @Schema(description = "Last update time (timestamp, milliseconds)")
        @JsonProperty("update_time")
        private Long updateTime;

        @Schema(description = "Last update date (RFC1123 format returned by RAGFlow)")
        @JsonProperty("update_date")
        private String updateDate;

        /**
         * Parsing method enumeration (ChunkMethod)
         */
        public enum ChunkMethod {
            @Schema(description = "General mode: Suitable for most plain text or mixed documents")
            @JsonProperty("naive")
            NAIVE,
            @Schema(description = "Manual mode: Allows users to manually edit chunks")
            @JsonProperty("manual")
            MANUAL,
            @Schema(description = "Q&A mode: Optimized specifically for Q&A format documents")
            @JsonProperty("qa")
            QA,
            @Schema(description = "Table mode: Optimized specifically for Excel or CSV etc. table data")
            @JsonProperty("table")
            TABLE,
            @Schema(description = "Paper mode: Optimized for academic paper formatting")
            @JsonProperty("paper")
            PAPER,
            @Schema(description = "Book mode: Optimized for book chapter structure")
            @JsonProperty("book")
            BOOK,
            @Schema(description = "Laws mode: Optimized for legal article structure")
            @JsonProperty("laws")
            LAWS,
            @Schema(description = "Presentation mode: Optimized for PPT etc. presentation files")
            @JsonProperty("presentation")
            PRESENTATION,
            @Schema(description = "Picture mode: OCR and description of picture content")
            @JsonProperty("picture")
            PICTURE,
            @Schema(description = "Overall mode: Treats the entire document as a single chunk")
            @JsonProperty("one")
            ONE,
            @Schema(description = "Knowledge graph mode: Extract entity relationships to build graphs")
            @JsonProperty("knowledge_graph")
            KNOWLEDGE_GRAPH,
            @Schema(description = "Email mode: Optimized for email format")
            @JsonProperty("email")
            EMAIL;
        }

        /**
         * Running status enumeration (RunStatus)
         */
        public enum RunStatus {
            @Schema(description = "Not started: Waiting in parsing queue")
            @JsonProperty("UNSTART")
            UNSTART,
            @Schema(description = "Running: Currently parsing or indexing")
            @JsonProperty("RUNNING")
            RUNNING,
            @Schema(description = "Cancelled: Manually cancelled by user")
            @JsonProperty("CANCEL")
            CANCEL,
            @Schema(description = "Completed: Parsing successful")
            @JsonProperty("DONE")
            DONE,
            @Schema(description = "Failed: Error during parsing process")
            @JsonProperty("FAIL")
            FAIL;
        }

        /**
         * Layout recognition model enumeration
         */
        public enum LayoutRecognize {
            @Schema(description = "Deep document understanding model: Suitable for complex formatting")
            @JsonProperty("DeepDOC")
            DeepDOC,
            @Schema(description = "Simple rule model: Suitable for plain text")
            @JsonProperty("Simple")
            Simple;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "Document parser configuration parameters")
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ParserConfig implements Serializable {
            private static final long serialVersionUID = 1L;

            @Schema(description = "Maximum number of tokens per chunk (Recommended values: 512, 1024, 2048)")
            @JsonProperty("chunk_token_num")
            private Integer chunkTokenNum;

            @Schema(description = "Segment separator (Supports escape characters, e.g. \\n)")
            private String delimiter;

            @Schema(description = "Layout recognition model (DeepDOC/Simple)")
            @JsonProperty("layout_recognize")
            private LayoutRecognize layoutRecognize;

            @Schema(description = "Convert Excel to HTML table")
            @JsonProperty("html4excel")
            private Boolean html4excel;

            @Schema(description = "Number of automatically extracted keywords (0 indicates no extraction)")
            @JsonProperty("auto_keywords")
            private Integer autoKeywords;

            @Schema(description = "Number of automatically generated questions (0 indicates no generation)")
            @JsonProperty("auto_questions")
            private Integer autoQuestions;

            @Schema(description = "Number of automatically generated tags")
            @JsonProperty("topn_tags")
            private Integer topnTags;

            @Schema(description = "RAPTOR advanced index configuration")
            private RaptorConfig raptor;

            @Schema(description = "GraphRAG knowledge graph configuration")
            @JsonProperty("graphrag")
            private GraphRagConfig graphRag;

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            @Schema(description = "RAPTOR (Recursive summary indexing) configuration")
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class RaptorConfig implements Serializable {
                private static final long serialVersionUID = 1L;
                @Schema(description = "Whether to enable RAPTOR index")
                @JsonProperty("use_raptor")
                private Boolean useRaptor;
            }

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            @Schema(description = "GraphRAG (Graph-enhanced retrieval) configuration")
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class GraphRagConfig implements Serializable {
                private static final long serialVersionUID = 1L;
                @Schema(description = "Whether to enable GraphRAG index")
                @JsonProperty("use_graphrag")
                private Boolean useGraphRag;
            }
        }
    }
}
 