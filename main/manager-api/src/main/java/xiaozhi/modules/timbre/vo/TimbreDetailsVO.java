package xiaozhi.modules.timbre.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Timbre details display VO
 * 
 * @author zjy
 * @since 2025-3-21
 */
@Data
public class TimbreDetailsVO implements Serializable {
    @Schema(description = "Timbre id")
    private String id;

    @Schema(description = "Language")
    private String languages;

    @Schema(description = "Timbre name")
    private String name;

    @Schema(description = "Remark")
    private String remark;

    @Schema(description = "Reference audio path")
    private String referenceAudio;

    @Schema(description = "Reference text")
    private String referenceText;

    @Schema(description = "Sort")
    private long sort;

    @Schema(description = "Corresponding TTS model primary key")
    private String ttsModelId;

    @Schema(description = "Timbre encoding")
    private String ttsVoice;

    @Schema(description = "Audio playback address")
    private String voiceDemo;

}
 