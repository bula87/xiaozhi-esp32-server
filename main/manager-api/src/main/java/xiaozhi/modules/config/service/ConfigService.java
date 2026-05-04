package xiaozhi.modules.config.service;

import java.util.List;
import java.util.Map;

public interface ConfigService {
    /**
     * Get server configuration
     *
     * @param isCache Whether to use cache
     * @return Configuration information
     */
    Object getConfig(Boolean isCache);

    /**
     * Get agent model configuration
     *
     * @param macAddress     MAC address
     * @param selectedModule Models already instantiated by the client
     * @return Model configuration information
     */
    Map<String, Object> getAgentModels(String macAddress, Map<String, String> selectedModule);

    /**
     * Get agent replacement words
     *
     * @param macAddress Device MAC address
     * @return List of replacement words, format: ["Template1|Template01", "Template2|Template02"]
     */
    List<String> getCorrectWords(String macAddress);
}