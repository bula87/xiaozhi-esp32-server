package xiaozhi.modules.knowledge.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Document DTO
 */
@Data
@Schema(description = "Knowledge Base Document")
public class DocumentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "Local ID")
    private String id;

    @Schema(description = "Knowledge Base ID")
    private String datasetId;

    @Schema(description = "RAGFlow Document ID")
    private String documentId;

    @Schema(description = "Document Name")
    private String name;

    @Schema(description = "File Size")
    private Long size;

    @Schema(description = "File Type")
    private String type;

    @Schema(description = "Chunk Method")
    private String chunkMethod;

    @Schema(description = "Parser Configuration")
    private Map<String, Object> parserConfig;

    @Schema(description = "Processing Status (1: Parsing 3: Success 4: Failure)")
    private Integer status;

    @Schema(description = "Error Message")
    private String error;

    @Schema(description = "Chunk Count")
    private Integer chunkCount;

    @Schema(description = "Token Count")
    private Long tokenCount;

    @Schema(description = "Enabled (1: Yes 0: No)")
    private Integer enabled;

    @Schema(description = "Creation Time")
    private Date createdAt;

    @Schema(description = "Update Time")
    private Date updatedAt;

    @Schema(description = "Upload Progress (Virtual Field)")
    private Double progress;

    @Schema(description = "Thumbnail/Preview Image (Virtual Field)")
    private String thumbnail;
}
 