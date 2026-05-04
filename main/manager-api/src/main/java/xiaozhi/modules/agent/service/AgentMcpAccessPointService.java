package xiaozhi.modules.agent.service;

import java.util.List;

/**
 * Agent Mcp Access Point Processing Service
 *
 * @author zjy
 */
public interface AgentMcpAccessPointService {
    /**
     * Get the agent's mcp access point address
     * @param id Agent ID
     * @return Mcp access point address
     */
   String getAgentMcpAccessAddress(String id);

    /**
     * Get the list of existing tools for the agent's mcp access point
     * @param id Agent ID
     * @return Tool list
     */
   List<String> getAgentMcpToolsList(String id);
}
 