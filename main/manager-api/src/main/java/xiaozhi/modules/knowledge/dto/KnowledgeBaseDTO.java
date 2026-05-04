package xiaozhi.modules.knowledge.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Knowledge Base")
public class KnowledgeBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Unique Identifier")
    private String id;

    @Schema(description = "Dataset ID")
    private String datasetId;

    @Schema(description = "RAG Model Config ID")
    private String ragModelId;

    @Schema(description = "Knowledge Base Name")
    private String name;

    @Schema(description = "Knowledge Base Avatar (Base64)")
    private String avatar;

    @Schema(description = "Knowledge Base Description")
    private String description;

    @Schema(description = "Embedding Model Name")
    private String embeddingModel;

    @Schema(description = "Permission Settings: me/team")
    private String permission;

    @Schema(description = "Chunking Method")
    private String chunkMethod;

    @Schema(description = "Parser Config (JSON String)")
    private String parserConfig;

    @Schema(description = "Total Number of Chunks")
    private Long chunkCount;

    @Schema(description = "Total Token Count")
    private Long tokenNum;

    @Schema(description = "Status (0: Disabled, 1: Enabled)")
    private Integer status;

    @Schema(description = "Creator")
    private Long creator;

    @Schema(description = "Creation Time")
    private Date createdAt;

    @Schema(description = "Updater")
    private Long updater;

    @Schema(description = "Update Time")
    private Date updatedAt;

    @Schema(description = "Document Count")
    private Integer documentCount;
}
 