package xiaozhi.modules.model.dto;

import java.io.Serial;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Model Supplier/Dealer")
public class ModelConfigBodyDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Model ID, auto-generated if not filled")
    private String id;

    @Schema(description = "Model Code (e.g., AliLLM, DoubaoTTS)")
    private String modelCode;

    @Schema(description = "Model Name")
    private String modelName;

    @Schema(description = "Is Default Configuration (0 No, 1 Yes)")
    private Integer isDefault;

    @Schema(description = "Is Enabled")
    private Integer isEnabled;

    @Schema(description = "Model Configuration (JSON format)")
    private JSONObject configJson;

    @Schema(description = "Official Documentation Link")
    private String docLink;

    @Schema(description = "Remarks")
    private String remark;

    @Schema(description = "Sort Order")
    private Integer sort;
}
 