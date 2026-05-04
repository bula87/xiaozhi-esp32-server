package xiaozhi.modules.agent.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("ai_agent_correct_word_mapping")
@Schema(description = "AI Agent Correct Word Mapping")
public class AgentCorrectWordMappingEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(description = "Primary Key")
    private String id;

    @Schema(description = "Agent ID")
    private String agentId;

    @Schema(description = "Correct Word File ID")
    private String fileId;

    @Schema(description = "Creator")
    private Long creator;

    @Schema(description = "Created At")
    private Date createdAt;

    @Schema(description = "Updater")
    private Long updater;

    @Schema(description = "Updated At")
    private Date updatedAt;
}
 