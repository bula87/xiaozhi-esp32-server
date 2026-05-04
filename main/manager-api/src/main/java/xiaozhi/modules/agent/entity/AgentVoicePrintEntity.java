package xiaozhi.modules.agent.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * Intelligent body voice print table
 *
 * @author zjy
 */
@TableName(value = "ai_agent_voice_print")
@Data
public class AgentVoicePrintEntity {
    /**
     * Primary key id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * Associated intelligent body id
     */
    private String agentId;
    /**
     * Associated audio id
     */
    private String audioId;
    /**
     * Name of the person who provides the voice print
     */
    private String sourceName;
    /**
     * Description of the person who provides the voice print
     */
    private String introduce;

    /**
     * Creator
     */
    @TableField(fill = FieldFill.INSERT)
    private Long creator;
    /**
     * Creation time
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * Updater
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    /**
     * Update time
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}
 