package xiaozhi.modules.agent.vo;

import lombok.Data;

import java.util.Date;

/**
 * Display Intelligent Agent Voice Print List VO
 */
@Data
public class AgentVoicePrintVO {

    /**
     * Primary key id
     */
    private String id;
    /**
     * Audio file id
     */
    private String audioId;
    /**
     * Name of the person whose voice print is from
     */
    private String sourceName;
    /**
     * Description of the person whose voice print is from
     */
    private String introduce;
    /**
     * Creation time
     */
    private Date createDate;
}
 