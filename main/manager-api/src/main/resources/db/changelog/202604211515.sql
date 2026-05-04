-- Agent Replacement Word File Table
CREATE TABLE IF NOT EXISTS `ai_agent_correct_word_file` (
    `id`          VARCHAR(32)  NOT NULL,
    `file_name`   VARCHAR(256) NOT NULL COMMENT 'Original File Name',
    `word_count`  INT          NOT NULL DEFAULT 0 COMMENT 'Replacement Word Count',
    `content`     TEXT         COMMENT 'File Original Content',
    `creator`     BIGINT       DEFAULT NULL,
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updater`     BIGINT       DEFAULT NULL,
    `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_creator` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Replacement Word File';

-- Replacement Word Entry Table
CREATE TABLE IF NOT EXISTS `ai_agent_correct_word_item` (
    `id`          VARCHAR(32)  NOT NULL,
    `file_id`     VARCHAR(32)  NOT NULL COMMENT 'Belonging File ID',
    `source_word` VARCHAR(128) NOT NULL COMMENT 'Source Word',
    `target_word` VARCHAR(128) NOT NULL COMMENT 'Target Word',
    PRIMARY KEY (`id`),
    INDEX `idx_file_id` (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Replacement Word Entry';

-- Agent Replacement Word File Mapping Table
CREATE TABLE IF NOT EXISTS `ai_agent_correct_word_mapping` (
    `id`          VARCHAR(32)  NOT NULL,
    `agent_id`    VARCHAR(32)  NOT NULL,
    `file_id`     VARCHAR(32)  NOT NULL,
    `creator`     BIGINT       DEFAULT NULL,
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updater`     BIGINT       DEFAULT NULL,
    `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_agent_file` (`agent_id`, `file_id`),
    INDEX `idx_agent_id` (`agent_id`),
    INDEX `idx_file_id` (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent Replacement Word File Association';
 