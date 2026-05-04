package xiaozhi.modules.agent.service;

import xiaozhi.common.service.BaseService;
import xiaozhi.modules.agent.entity.AgentContextProviderEntity;

public interface AgentContextProviderService extends BaseService<AgentContextProviderEntity> {
    /**
     * According to the agent ID, get the context source configuration
     * @param agentId Agent ID
     * @return Context source configuration entity
     */
    AgentContextProviderEntity getByAgentId(String agentId);

    /**
     * Save or update the context source configuration
     * @param entity Entity
     */
    void saveOrUpdateByAgentId(AgentContextProviderEntity entity);

    /**
     * According to the agent ID, delete the context source configuration
     * @param agentId Agent ID
     */
    void deleteByAgentId(String agentId);
}
 