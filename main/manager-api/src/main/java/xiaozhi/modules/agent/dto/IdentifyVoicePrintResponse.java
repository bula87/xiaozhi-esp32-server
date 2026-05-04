package xiaozhi.modules.agent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Speaker recognition interface response object
 */
@Data
public class IdentifyVoicePrintResponse {
    /**
     * The most matching voice print id
     */
    @JsonProperty("speaker_id")
    private String speakerId;
    /**
     * Voice print score
     */
    private Double score;
}
 