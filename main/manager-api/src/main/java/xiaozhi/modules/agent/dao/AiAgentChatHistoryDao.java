package xiaozhi.modules.agent.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import xiaozhi.modules.agent.entity.AgentChatHistoryEntity;

/**
 * {@link AgentChatHistoryEntity} AI agent chat history Dao object
 *
 * @author Goody
 * @version 1.0, 2025/4/30
 * @since 1.0.0
 */
@Mapper
public interface AiAgentChatHistoryDao extends BaseMapper<AgentChatHistoryEntity> {

    /**
     * Delete chat history records by AI agent ID
     *
     * @param agentId AI agent ID
     */
    void deleteHistoryByAgentId(String agentId);

    /**
     * Delete audio IDs by AI agent ID
     *
     * @param agentId AI agent ID
     */
    void deleteAudioIdByAgentId(String agentId);

    /**
     * Get all audio IDs list by AI agent ID
     *
     * @param agentId AI agent ID
     * @return Audio IDs list
     */
    List<String> getAudioIdsByAgentId(String agentId);

    /**
     * Batch delete audios
     *
     * @param audioIds Audio IDs list
     */
    void deleteAudioByIds(@Param("audioIds") List<String> audioIds);
}
 