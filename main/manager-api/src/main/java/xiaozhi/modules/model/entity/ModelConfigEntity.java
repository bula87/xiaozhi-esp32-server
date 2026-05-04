package xiaozhi.modules.model.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName(value = "ai_model_config", autoResultMap = true)
@Schema(description = "Model Configuration Table")
public class ModelConfigEntity {

    @Schema(description = "Primary Key")
    private String id;

    @Schema(description = "Model Type(Memory/ASR/VAD/LLM/TTS)")
    private String modelType;

    @Schema(description = "Model Code(Example: AliLLM, DoubaoTTS)")
    private String modelCode;

    @Schema(description = "Model Name")
    private String modelName;

    @Schema(description = "Is Default Configuration (0 No, 1 Yes)")
    private Integer isDefault;

    @Schema(description = "Is Enabled")
    private Integer isEnabled;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "Model Configuration (JSON format)")
    private JSONObject configJson;

    @Schema(description = "Official Documentation Link")
    private String docLink;

    @Schema(description = "Remarks")
    private String remark;

    @Schema(description = "Sort Order")
    private Integer sort;

    @Schema(description = "Updater")
    @TableField(fill = FieldFill.UPDATE)
    private Long updater;

    @Schema(description = "Update Date")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;

    @Schema(description = "Creator")
    @TableField(fill = FieldFill.INSERT)
    private Long creator;

    @Schema(description = "Creation Date")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;
}
 