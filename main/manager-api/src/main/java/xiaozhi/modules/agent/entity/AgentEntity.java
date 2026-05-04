package xiaozhi.modules.agent.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("ai_agent")
@Schema(description = "Agent information")
public class AgentEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(description = "Unique identifier for the agent")
    private String id;

    @Schema(description = "User ID to which the agent belongs")
    private Long userId;

    @Schema(description = "Agent code")
    private String agentCode;

    @Schema(description = "Agent name")
    private String agentName;

    @Schema(description = "ASR model identifier")
    private String asrModelId;

    @Schema(description = "VAD model identifier")
    private String vadModelId;

    @Schema(description = "LLM model identifier")
    private String llmModelId;

    @Schema(description = "SLM model identifier")
    private String slmModelId;

    @Schema(description = "VLLM model identifier")
    private String vllmModelId;

    @Schema(description = "TTS model identifier")
    private String ttsModelId;

    @Schema(description = "TTS voice identifier")
    private String ttsVoiceId;

    @Schema(description = "Language of TTS voice")
    private String ttsLanguage;

    @Schema(description = "TTS volume")
    private Integer ttsVolume;

    @Schema(description = "TTS rate")
    private Integer ttsRate;

    @Schema(description = "TTS pitch")
    private Integer ttsPitch;

    @Schema(description = "Memory model identifier")
    private String memModelId;

    @Schema(description = "Intent model identifier")
    private String intentModelId;

    @Schema(description = "Chat history configuration (0: no record, 1: record text only, 2: record text and voice)", required = false)
    private Integer chatHistoryConf;

    @Schema(description = "System prompt parameters")
    private String systemPrompt;

    @Schema(description = "Summary of memory", example = "Build a growing dynamic memory network that retains key information while intelligently maintaining the evolution of information within limited space\n" +
            "Summarize important user information from chat records to provide more personalized services in future conversations", required = false)
    private String summaryMemory;

    @Schema(description = "Language code")
    private String langCode;

    @Schema(description = "Interaction language")
    private String language;

    @Schema(description = "Sort order")
    private Integer sort;

    @Schema(description = "Creator")
    private Long creator;

    @Schema(description = "Creation time")
    private Date createdAt;

    @Schema(description = "Updater")
    private Long updater;

    @Schema(description = "Update time")
    private Date updatedAt;
}
 