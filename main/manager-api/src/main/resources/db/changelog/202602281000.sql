-- Tag Table
CREATE TABLE IF NOT EXISTS ai_agent_tag (
    id VARCHAR(32) NOT NULL COMMENT 'Primary Key',
    tag_name VARCHAR(64) NOT NULL COMMENT 'Tag Name',
    sort INT UNSIGNED DEFAULT 0 COMMENT 'Sort',
    creator BIGINT COMMENT 'Creator',
    created_at DATETIME COMMENT 'Create Time',
    updater BIGINT COMMENT 'Updater',
    updated_at DATETIME COMMENT 'Update Time',
    deleted TINYINT DEFAULT 0 COMMENT 'Delete Flag',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tag_name (tag_name),
    INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent Tag Table';

-- Agent Tag Relation Table
CREATE TABLE IF NOT EXISTS ai_agent_tag_relation (
    id VARCHAR(32) NOT NULL COMMENT 'Primary Key',
    agent_id VARCHAR(32) NOT NULL COMMENT 'Agent ID',
    tag_id VARCHAR(32) NOT NULL COMMENT 'Tag ID',
    creator BIGINT COMMENT 'Creator',
    created_at DATETIME COMMENT 'Create Time',
    updater BIGINT COMMENT 'Updater',
    updated_at DATETIME COMMENT 'Update Time',
    PRIMARY KEY (id),
    UNIQUE KEY uk_agent_tag (agent_id, tag_id),
    INDEX idx_agent_id (agent_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent Tag Relation Table';
 