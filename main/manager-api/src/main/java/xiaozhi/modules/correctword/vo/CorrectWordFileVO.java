package xiaozhi.modules.correctword.vo;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Replace Word File List VO")
public class CorrectWordFileVO {

    @Schema(description = "Replace Word File ID")
    private String id;

    @Schema(description = "Original File Name")
    private String fileName;

    @Schema(description = "Number of Replace Words")
    private Integer wordCount;

    @Schema(description = "Content of Replace Words, one per line")
    private List<String> content;

    @Schema(description = "Creation Time")
    private Date createdAt;

    @Schema(description = "Update Time")
    private Date updatedAt;
}
 