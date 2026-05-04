package xiaozhi.modules.correctword.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Simplified replacement word VO (for device use)")
public class CorrectWordSimpleVO {

    @Schema(description = "Original word")
    private String sourceWord;

    @Schema(description = "Replacement word")
    private String targetWord;
}
 