package xiaozhi.modules.agent.service;

import java.util.List;

import xiaozhi.modules.agent.dto.AgentVoicePrintSaveDTO;
import xiaozhi.modules.agent.dto.AgentVoicePrintUpdateDTO;
import xiaozhi.modules.agent.vo.AgentVoicePrintVO;

/**
 * Agent voice print processing service
 *
 * @author zjy
 */
public interface AgentVoicePrintService {
    /**
     * Add a new voice print for the agent
     *
     * @param dto Data to save the agent's voice print
     * @return T: Success F: Failure
     */
    boolean insert(AgentVoicePrintSaveDTO dto);

    /**
     * Delete a specified voice print of the agent
     *
     * @param userId       Current logged-in user ID
     * @param voicePrintId Voice print ID
     * @return Whether successful T: Success F: Failure
     */
    boolean delete(Long userId, String voicePrintId);

    /**
     * Get all voice print data for a specified agent
     *
     * @param userId  Current logged-in user ID
     * @param agentId Agent ID
     * @return Collection of voice prints
     */
    List<AgentVoicePrintVO> list(Long userId, String agentId);

    /**
     * Update the voice print data for a specified agent
     *
     * @param userId Current logged-in user ID
     * @param dto    Data to modify the voice print
     * @return Whether successful T: Success F: Failure
     */
    boolean update(Long userId, AgentVoicePrintUpdateDTO dto);

}
 