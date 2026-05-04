package xiaozhi.modules.agent.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Agent Update DTO
 * Specifically used for updating agents, the id field is required to identify which agent to update.
 * Other fields are optional and only provided fields will be updated.
 */
@Data
@Schema(description = "Agent Update Object")
public class AgentUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "Agent Code", example = "AGT_1234567890", nullable = true)
    private String agentCode;

    @Schema(description = "Agent Name", example = "Customer Service Assistant", nullable = true)
    private String agentName;

    @Schema(description = "ASR Model ID", example = "asr_model_02", nullable = true)
    private String asrModelId;

    @Schema(description = "VAD Model ID", example = "vad_model_02", nullable = true)
    private String vadModelId;

    @Schema(description = "LLM Model ID", example = "llm_model_02", nullable = true)
    private String llmModelId;

    @Schema(description = "SLM Model ID", example = "slm_model_02", nullable = true)
    private String slmModelId;

    @Schema(description = "VLLM Model ID", example = "vllm_model_02", required = false)
    private String vllmModelId;

    @Schema(description = "TTS Model ID", example = "tts_model_02", required = false)
    private String ttsModelId;

    @Schema(description = "Voice ID", example = "voice_02", nullable = true)
    private String ttsVoiceId;

    @Schema(description = "Voice Language", example = "Mandarin", nullable = true)
    private String ttsLanguage;

    @Schema(description = "TTS Volume", example = "50", nullable = true)
    private Integer ttsVolume;

    @Schema(description = "TTS Rate", example = "50", nullable = true)
    private Integer ttsRate;

    @Schema(description = "TTS Pitch", example = "50", nullable = true)
    private Integer ttsPitch;

    @Schema(description = "Memory Model ID", example = "mem_model_02", nullable = true)
    private String memModelId;

    @Schema(description = "Intent Model ID", example = "intent_model_02", nullable = true)
    private String intentModelId;

    @Schema(description = "Plugin Function Information", nullable = true)
    private List<FunctionInfo> functions;

    @Schema(description = "System Prompt", example = "You are a professional customer service assistant, responsible for answering user questions and providing help.", nullable = true)
    private String systemPrompt;

    @Schema(description = "Summary Memory", example = "Build a growing dynamic memory network to retain key information within limited space while intelligently maintaining the trajectory of information evolution.\n"
            + "Summarize important information about the user based on conversation records to provide more personalized service in future conversations.", nullable = true)
    private String summaryMemory;

    @Schema(description = "Chat History Configuration (0 do not record, 1 only record text, 2 record text and voice)", example = "3", nullable = true)
    private Integer chatHistoryConf;

    @Schema(description = "Language Code", example = "zh_CN", nullable = true)
    private String langCode;

    @Schema(description = "Interaction Language", example = "Chinese", nullable = true)
    private String language;

    @Schema(description = "Sort", example = "1", nullable = true)
    private Integer sort;

    @Schema(description = "Context Source Configuration", nullable = true)
    private List<ContextProviderDTO> contextProviders;

    @Schema(description = "Correct Word File IDs", nullable = true)
    private List<String> correctWordFileIds;

    @Data
    @Schema(description = "Plugin Function Information")
    public static class FunctionInfo implements Serializable {
        @Schema(description = "Plugin ID", example = "plugin_01")
        private String pluginId;

        @Schema(description = "Function Parameter Information", nullable = true)
        private HashMap<String, Object> paramInfo;

        private static final long serialVersionUID = 1L;
    }
}
 