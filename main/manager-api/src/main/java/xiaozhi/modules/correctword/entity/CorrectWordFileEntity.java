package xiaozhi.modules.correctword.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("ai_agent_correct_word_file")
@Schema(description = "AI Agent Correct Word File")
public class CorrectWordFileEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(description = "Correct Word File ID")
    private String id;

    @Schema(description = "Original File Name")
    private String fileName;

    @Schema(description = "Word Count")
    private Integer wordCount;

    @Schema(description = "File Original Content (for download)")
    private String content;

    @Schema(description = "Creator")
    private Long creator;

    @Schema(description = "Creation Time")
    private Date createdAt;

    @Schema(description = "Updater")
    private Long updater;

    @Schema(description = "Update Time")
    private Date updatedAt;
}
 