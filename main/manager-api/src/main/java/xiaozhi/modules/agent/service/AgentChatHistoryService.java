package xiaozhi.modules.agent.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

import xiaozhi.common.page.PageData;
import xiaozhi.modules.agent.dto.AgentChatHistoryDTO;
import xiaozhi.modules.agent.dto.AgentChatSessionDTO;
import xiaozhi.modules.agent.entity.AgentChatHistoryEntity;
import xiaozhi.modules.agent.vo.AgentChatHistoryUserVO;

/**
 * Intelligent agent chat history processing service
 *
 * @author Goody
 * @version 1.0, 2025/4/30
 * @since 1.0.0
 */
public interface AgentChatHistoryService extends IService<AgentChatHistoryEntity> {

    /**
     * Get session list by agent ID
     *
     * @param params Query parameters, including agentId, page, limit
     * @return Paged session list
     */
    PageData<AgentChatSessionDTO> getSessionListByAgentId(Map<String, Object> params);

    /**
     * Get chat history list by session ID
     *
     * @param agentId   Agent ID
     * @param sessionId Session ID
     * @return Chat history list
     */
    List<AgentChatHistoryDTO> getChatHistoryBySessionId(String agentId, String sessionId);

    /**
     * Delete chat records by agent ID
     *
     * @param agentId     Agent ID
     * @param deleteAudio Whether to delete audio
     * @param deleteText  Whether to delete text
     */
    void deleteByAgentId(String agentId, Boolean deleteAudio, Boolean deleteText);

    /**
     * Get the most recent 50 user chat records by agent ID (with audio data)
     *
     * @param agentId Agent ID
     * @return Chat record list (only users)
     */
    List<AgentChatHistoryUserVO> getRecentlyFiftyByAgentId(String agentId);

    /**
     * Get chat content by audio ID
     *
     * @param audioId Audio ID
     * @return Chat content
     */
    String getContentByAudioId(String audioId);


    /**
     * Query whether this audio ID belongs to this intelligent agent
     *
     * @param audioId Audio ID
     * @param agentId Agent ID
     * @return T: Belongs F: Does not belong
     */
    boolean isAudioOwnedByAgent(String audioId, String agentId);
}
 