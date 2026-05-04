package xiaozhi.modules.model.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("ai_model_provider")
@Schema(description = "Model Provider Table")
public class ModelProviderEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(description = "Primary Key")
    private String id;

    @Schema(description = "Model Type (Memory/ASR/VAD/LLM/TTS)")
    private String modelType;

    @Schema(description = "Provider Code, e.g., openai")
    private String providerCode;

    @Schema(description = "Provider Name")
    private String name;

    @Schema(description = "Provider Field List (JSON format)")
    private String fields;

    @Schema(description = "Sort Order")
    private Integer sort;

    @Schema(description = "Creator")
    private Long creator;

    @Schema(description = "Creation Time")
    private Date createDate;

    @Schema(description = "Updater")
    private Long updater;

    @Schema(description = "Update Time")
    private Date updateDate;
}
 