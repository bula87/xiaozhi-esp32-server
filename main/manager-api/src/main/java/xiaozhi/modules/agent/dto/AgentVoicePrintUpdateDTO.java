package xiaozhi.modules.agent.dto;

import lombok.Data;

/**
 * Modify the dto of agent voice print
 *
 * @author zjy
 */
@Data
public class AgentVoicePrintUpdateDTO {
    /**
     * The id of agent voice print
     */
    private String id;
    /**
     * The id of audio file
     */
    private String audioId;
    /**
     * The name of the person whose voice print is from
     */
    private String sourceName;
    /**
     * Introduce the person whose voice print is from
     */
    private String introduce;
}
 