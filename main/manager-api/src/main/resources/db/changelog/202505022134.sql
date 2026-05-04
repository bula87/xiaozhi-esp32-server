-- Initialize agent chat history
DROP TABLE IF EXISTS ai_chat_history;
DROP TABLE IF EXISTS ai_chat_message;
DROP TABLE IF EXISTS ai_agent_chat_history;
CREATE TABLE ai_agent_chat_history
(
    id          BIGINT AUTO_INCREMENT COMMENT 'Primary Key ID' PRIMARY KEY,
    mac_address VARCHAR(100) COMMENT 'MAC Address',
    agent_id    VARCHAR(64) COMMENT 'Agent ID',
    session_id  VARCHAR(100) COMMENT 'Session ID',
    chat_type   TINYINT(3) COMMENT 'Message Type: 1-User, 2-Agent',
    content     VARCHAR(2048) COMMENT 'Chat Content',
    audio_id    VARCHAR(64) COMMENT 'Audio ID',
    created_at  DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL COMMENT 'Creation Time',
    updated_at  DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'Update Time',
    INDEX idx_ai_agent_chat_history_mac (mac_address),
    INDEX idx_ai_agent_chat_history_session_id (session_id),
    INDEX idx_ai_agent_chat_history_agent_id (agent_id),
    INDEX idx_ai_agent_chat_history_agent_session_created (agent_id, session_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Agent Chat History Table';

DROP TABLE IF EXISTS ai_agent_chat_audio;
CREATE TABLE ai_agent_chat_audio
(
    id          VARCHAR(64) COMMENT 'Primary Key ID' PRIMARY KEY,
    audio       LONGBLOB COMMENT 'Audio Opus Data'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'Agent Chat Audio Data Table';