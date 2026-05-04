package xiaozhi.modules.knowledge.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@Schema(description = "Knowledge Base Document")
@JsonIgnoreProperties(ignoreUnknown = true)
public class KnowledgeFilesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "Unique Identifier")
    private String id;

    @Schema(description = "Document ID")
    private String documentId;

    @Schema(description = "Knowledge Base ID")
    private String datasetId;

    @Schema(description = "Document Name")
    private String name;

    @Schema(description = "Document Type")
    private String fileType;

    @Schema(description = "File Size (in bytes)")
    private Long fileSize;

    @Schema(description = "File Path")
    private String filePath;

    @Schema(description = "Parsing Progress (0.0 ~ 1.0)")
    private Double progress;

    @Schema(description = "Thumbnail (Base64 or URL)")
    private String thumbnail;

    @Schema(description = "Processing Duration (unit: seconds)")
    private Double processDuration;

    @Schema(description = "Source Type (local, s3, url etc.)")
    private String sourceType;

    @Schema(description = "Metadata Fields (Map format)")
    private Map<String, Object> metaFields;

    @Schema(description = "Chunk Method")
    private String chunkMethod;

    @Schema(description = "Parser Configuration")
    private Map<String, Object> parserConfig;

    @Schema(description = "Available Status (1: Enabled/Normal, 0: Disabled/Expired)")
    private String status;

    @Schema(description = "Runtime Status (UNSTART/RUNNING/CANCEL/DONE/FAIL)")
    private String run;

    @Schema(description = "Creator")
    private Long creator;

    @Schema(description = "Creation Time")
    private Date createdAt;

    @Schema(description = "Updater")
    private Long updater;

    @Schema(description = "Update Time")
    private Date updatedAt;

    @Schema(description = "Chunk Count")
    private Integer chunkCount;

    @Schema(description = "Token Count")
    private Long tokenCount;

    @Schema(description = "Parsing Error Information")
    private String error;

    // Document Parsing Status Constants Definition
    private static final Integer STATUS_UNSTART = 0;
    private static final Integer STATUS_RUNNING = 1;
    private static final Integer STATUS_CANCEL = 2;
    private static final Integer STATUS_DONE = 3;
    private static final Integer STATUS_FAIL = 4;

    /**
     * Get Document Parsing Status Code (based on the run field conversion)
     */
    public Integer getParseStatusCode() {
        if (run == null) {
            return STATUS_UNSTART;
        }

        // RAGFlow directly maps to corresponding status codes based on the value of the run field
        switch (run.toUpperCase()) {
            case "RUNNING":
                return STATUS_RUNNING;
            case "CANCEL":
                return STATUS_CANCEL;
            case "DONE":
                return STATUS_DONE;
            case "FAIL":
                return STATUS_FAIL;
            case "UNSTART":
            default:
                return STATUS_UNSTART;
        }
    }

}
 