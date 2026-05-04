package xiaozhi.modules.agent.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Smart Interaction Session List DTO
 */
@Data
public class AgentChatSessionDTO {
    /**
     * Session ID
     */
    private String sessionId;

    /**
     * Session Time
     */
    private LocalDateTime createdAt;

    /**
     * Chat Count
     */
    private Integer chatCount;

    /**
     * Session Title
     */
    private String title;
}
 