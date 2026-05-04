package xiaozhi.modules.agent.dto;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import xiaozhi.modules.agent.dto.AgentTagDTO;

/**
 * Agent Data Transfer Object
 * Used to pass agent-related data between the service layer and the controller layer
 */
@Data
@Schema(description = "Agent object")
public class AgentDTO {
    @Schema(description = "Agent code", example = "AGT_1234567890")
    private String id;

    @Schema(description = "Agent name", example = "Customer Service Assistant")
    private String agentName;

    @Schema(description = "Text-to-speech model name", example = "tts_model_01")
    private String ttsModelName;

    @Schema(description = "Voice name", example = "voice_01")
    private String ttsVoiceName;

    @Schema(description = "Large language model name", example = "llm_model_01")
    private String llmModelName;

    @Schema(description = "Visual large language model name", example = "vllm_model_01")
    private String vllmModelName;

    @Schema(description = "Memory model ID", example = "mem_model_01")
    private String memModelId;

    @Schema(description = "Role setting parameters", example = "You are a professional customer service assistant, responsible for answering user questions and providing help")
    private String systemPrompt;

    @Schema(description = "Summary memory", example = "Build a growing dynamic memory network to retain key information within limited space while intelligently maintaining the trajectory of information evolution\n" +
            "Summarize user's important information from conversation records to provide more personalized services in future conversations", required = false)
    private String summaryMemory;

    @Schema(description = "Last connection time", example = "2024-03-20 10:00:00")
    private Date lastConnectedAt;

    @Schema(description = "Device count", example = "10")
    private Integer deviceCount;

    @Schema(description = "Tag list")
    private List<AgentTagDTO> tags;
}
 