package xiaozhi.modules.agent.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Agent memory update DTO
 */
@Data
@Schema(description = "Agent memory update object")
public class AgentMemoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "Summary of memory", example = "Build a growing dynamic memory network to retain key information within limited space while intelligently maintaining the evolution trajectory of information\n" +
            "Summarize important user information based on conversation records to provide more personalized services in future conversations", required = false)
    private String summaryMemory;
}
 