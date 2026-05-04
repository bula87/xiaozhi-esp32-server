-- Model Provider Table
DROP TABLE IF EXISTS `ai_model_provider`;
CREATE TABLE `ai_model_provider` (
    `id` VARCHAR(64) NOT NULL COMMENT 'Primary Key',
    `model_type` VARCHAR(50) COMMENT 'Model Type (Memory/ASR/VAD/LLM/TTS)',
    `provider_code` VARCHAR(100) COMMENT 'Provider Type',
    `name` VARCHAR(150) COMMENT 'Provider Name',
    `fields` JSON COMMENT 'Provider Fields List (JSON)',
    `sort` INT UNSIGNED DEFAULT 0 COMMENT 'Sort Order',
    `creator` BIGINT COMMENT 'Creator ID',
    `create_date` DATETIME COMMENT 'Creation Time',
    `updater` BIGINT COMMENT 'Updater ID',
    `update_date` DATETIME COMMENT 'Update Time',
    PRIMARY KEY (`id`),
    INDEX `idx_ai_model_provider_model_type` (`model_type`) COMMENT 'Index for fast lookup of providers by model type'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Model Provider Table';

-- Model Config Table
DROP TABLE IF EXISTS `ai_model_config`;
CREATE TABLE `ai_model_config` (
    `id` VARCHAR(64) NOT NULL COMMENT 'Primary Key',
    `model_type` VARCHAR(50) COMMENT 'Model Type (Memory/ASR/VAD/LLM/TTS)',
    `model_code` VARCHAR(100) COMMENT 'Model Code (e.g. AliLLM, DoubaoTTS)',
    `model_name` VARCHAR(150) COMMENT 'Model Name',
    `is_default` TINYINT(1) DEFAULT 0 COMMENT 'Is Default Config (0 No, 1 Yes)',
    `is_enabled` TINYINT(1) DEFAULT 0 COMMENT 'Is Enabled',
    `config_json` JSON COMMENT 'Model Config (JSON)',
    `doc_link` VARCHAR(500) COMMENT 'Official Doc Link',
    `remark` VARCHAR(500) COMMENT 'Remark',
    `sort` INT UNSIGNED DEFAULT 0 COMMENT 'Sort Order',
    `creator` BIGINT COMMENT 'Creator ID',
    `create_date` DATETIME COMMENT 'Creation Time',
    `updater` BIGINT COMMENT 'Updater ID',
    `update_date` DATETIME COMMENT 'Update Time',
    PRIMARY KEY (`id`),
    INDEX `idx_ai_model_config_model_type` (`model_type`) COMMENT 'Index for fast lookup of configs by model type'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Model Configuration Table';

-- TTS Voice Table
DROP TABLE IF EXISTS `ai_tts_voice`;
CREATE TABLE `ai_tts_voice` (
    `id` VARCHAR(64) NOT NULL COMMENT 'Primary Key',
    `tts_model_id` VARCHAR(64) COMMENT 'Corresponding TTS Model ID',
    `name` VARCHAR(150) COMMENT 'Voice Name',
    `tts_voice` VARCHAR(150) COMMENT 'Voice Code',
    `languages` VARCHAR(100) COMMENT 'Language',
    `voice_demo` VARCHAR(500) DEFAULT NULL COMMENT 'Voice Demo URL',
    `remark` VARCHAR(500) COMMENT 'Remark',
    `sort` INT UNSIGNED DEFAULT 0 COMMENT 'Sort Order',
    `creator` BIGINT COMMENT 'Creator ID',
    `create_date` DATETIME COMMENT 'Creation Time',
    `updater` BIGINT COMMENT 'Updater ID',
    `update_date` DATETIME COMMENT 'Update Time',
    PRIMARY KEY (`id`),
    INDEX `idx_ai_tts_voice_tts_model_id` (`tts_model_id`) COMMENT 'Index for TTS Model ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='TTS Voice Table';

-- Agent Configuration Template Table
DROP TABLE IF EXISTS `ai_agent_template`;
CREATE TABLE `ai_agent_template` (
    `id` VARCHAR(64) NOT NULL COMMENT 'Agent Unique ID',
    `agent_code` VARCHAR(64) COMMENT 'Agent Code',
    `agent_name` VARCHAR(150) COMMENT 'Agent Name',
    `asr_model_id` VARCHAR(64) COMMENT 'ASR Model ID',
    `vad_model_id` VARCHAR(100) COMMENT 'VAD Model ID',
    `llm_model_id` VARCHAR(64) COMMENT 'LLM Model ID',
    `tts_model_id` VARCHAR(64) COMMENT 'TTS Model ID',
    `tts_voice_id` VARCHAR(64) COMMENT 'TTS Voice ID',
    `mem_model_id` VARCHAR(64) COMMENT 'Memory Model ID',
    `intent_model_id` VARCHAR(64) COMMENT 'Intent Model ID',
    `system_prompt` TEXT COMMENT 'Role Settings / System Prompt',
    `lang_code` VARCHAR(20) COMMENT 'Language Code',
    `language` VARCHAR(50) COMMENT 'Interactive Language',
    `sort` INT UNSIGNED DEFAULT 0 COMMENT 'Sort Weight',
    `creator` BIGINT COMMENT 'Creator ID',
    `created_at` DATETIME COMMENT 'Creation Time',
    `updater` BIGINT COMMENT 'Updater ID',
    `updated_at` DATETIME COMMENT 'Update Time',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent Configuration Template Table';

-- Agent Configuration Table
DROP TABLE IF EXISTS `ai_agent`;
CREATE TABLE `ai_agent` (
    `id` VARCHAR(64) NOT NULL COMMENT 'Agent Unique ID',
    `user_id` BIGINT COMMENT 'Owning User ID',
    `agent_code` VARCHAR(64) COMMENT 'Agent Code',
    `agent_name` VARCHAR(150) COMMENT 'Agent Name',
    `asr_model_id` VARCHAR(64) COMMENT 'ASR Model ID',
    `vad_model_id` VARCHAR(100) COMMENT 'VAD Model ID',
    `llm_model_id` VARCHAR(64) COMMENT 'LLM Model ID',
    `tts_model_id` VARCHAR(64) COMMENT 'TTS Model ID',
    `tts_voice_id` VARCHAR(64) COMMENT 'TTS Voice ID',
    `mem_model_id` VARCHAR(64) COMMENT 'Memory Model ID',
    `intent_model_id` VARCHAR(64) COMMENT 'Intent Model ID',
    `system_prompt` TEXT COMMENT 'Role Settings / System Prompt',
    `lang_code` VARCHAR(20) COMMENT 'Language Code',
    `language` VARCHAR(50) COMMENT 'Interactive Language',
    `sort` INT UNSIGNED DEFAULT 0 COMMENT 'Sort Weight',
    `creator` BIGINT COMMENT 'Creator ID',
    `created_at` DATETIME COMMENT 'Creation Time',
    `updater` BIGINT COMMENT 'Updater ID',
    `updated_at` DATETIME COMMENT 'Update Time',
    PRIMARY KEY (`id`),
    INDEX `idx_ai_agent_user_id` (`user_id`) COMMENT 'Index for fast lookup of user agents'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent Configuration Table';

-- Device Information Table
DROP TABLE IF EXISTS `ai_device`;
CREATE TABLE `ai_device` (
    `id` VARCHAR(64) NOT NULL COMMENT 'Device Unique ID',
    `user_id` BIGINT COMMENT 'Associated User ID',
    `mac_address` VARCHAR(100) COMMENT 'MAC Address',
    `last_connected_at` DATETIME COMMENT 'Last Connected Time',
    `auto_update` TINYINT UNSIGNED DEFAULT 0 COMMENT 'Auto Update Toggle (0 Off, 1 On)',
    `board` VARCHAR(100) COMMENT 'Hardware Model',
    `alias` VARCHAR(150) DEFAULT NULL COMMENT 'Device Alias',
    `agent_id` VARCHAR(64) COMMENT 'Agent ID',
    `app_version` VARCHAR(50) COMMENT 'Firmware Version',
    `sort` INT UNSIGNED DEFAULT 0 COMMENT 'Sort Order',
    `creator` BIGINT COMMENT 'Creator ID',
    `create_date` DATETIME COMMENT 'Creation Time',
    `updater` BIGINT COMMENT 'Updater ID',
    `update_date` DATETIME COMMENT 'Update Time',
    PRIMARY KEY (`id`),
    INDEX `idx_ai_device_created_at` (`mac_address`) COMMENT 'Index for fast lookup by MAC'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Device Information Table';

-- Voiceprint Recognition Table
DROP TABLE IF EXISTS `ai_voiceprint`;
CREATE TABLE `ai_voiceprint` (
    `id` VARCHAR(64) NOT NULL COMMENT 'Voiceprint Unique ID',
    `name` VARCHAR(150) COMMENT 'Voiceprint Name',
    `user_id` BIGINT COMMENT 'User ID',
    `agent_id` VARCHAR(64) COMMENT 'Associated Agent ID',
    `agent_code` VARCHAR(64) COMMENT 'Associated Agent Code',
    `agent_name` VARCHAR(150) COMMENT 'Associated Agent Name',
    `description` VARCHAR(500) COMMENT 'Voiceprint Description',
    `embedding` LONGTEXT COMMENT 'Voiceprint Feature Vector (JSON Array)',
    `memory` TEXT COMMENT 'Associated Memory Data',
    `sort` INT UNSIGNED DEFAULT 0 COMMENT 'Sort Weight',
    `creator` BIGINT COMMENT 'Creator ID',
    `created_at` DATETIME COMMENT 'Creation Time',
    `updater` BIGINT COMMENT 'Updater ID',
    `updated_at` DATETIME COMMENT 'Update Time',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Voiceprint Recognition Table';

-- Chat History Table
DROP TABLE IF EXISTS `ai_chat_history`;
CREATE TABLE `ai_chat_history` (
    `id` VARCHAR(64) NOT NULL COMMENT 'Chat ID',
    `user_id` BIGINT COMMENT 'User ID',
    `agent_id` VARCHAR(64) DEFAULT NULL COMMENT 'Agent Role ID',
    `device_id` VARCHAR(64) DEFAULT NULL COMMENT 'Device ID',
    `message_count` INT COMMENT 'Message Count',
    `creator` BIGINT COMMENT 'Creator ID',
    `create_date` DATETIME COMMENT 'Creation Time',
    `updater` BIGINT COMMENT 'Updater ID',
    `update_date` DATETIME COMMENT 'Update Time',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Chat History Table';

-- Chat Messages Table
DROP TABLE IF EXISTS `ai_chat_message`;
CREATE TABLE `ai_chat_message` (
    `id` VARCHAR(64) NOT NULL COMMENT 'Message Unique ID',
    `user_id` BIGINT COMMENT 'User Unique ID',
    `chat_id` VARCHAR(100) COMMENT 'Chat History ID',
    `role` ENUM('user', 'assistant') COMMENT 'Role (user or assistant)',
    `content` TEXT COMMENT 'Message Content',
    `prompt_tokens` INT UNSIGNED DEFAULT 0 COMMENT 'Prompt Tokens',
    `total_tokens` INT UNSIGNED DEFAULT 0 COMMENT 'Total Tokens',
    `completion_tokens` INT UNSIGNED DEFAULT 0 COMMENT 'Completion Tokens',
    `prompt_ms` INT UNSIGNED DEFAULT 0 COMMENT 'Prompt Latency (ms)',
    `total_ms` INT UNSIGNED DEFAULT 0 COMMENT 'Total Latency (ms)',
    `completion_ms` INT UNSIGNED DEFAULT 0 COMMENT 'Completion Latency (ms)',
    `creator` BIGINT COMMENT 'Creator ID',
    `create_date` DATETIME COMMENT 'Creation Time',
    `updater` BIGINT COMMENT 'Updater ID',
    `update_date` DATETIME COMMENT 'Update Time',
    PRIMARY KEY (`id`),
    INDEX `idx_ai_chat_message_user_id_chat_id_role` (`user_id`, `chat_id`) COMMENT 'Compound index for quick message retrieval',
    INDEX `idx_ai_chat_message_created_at` (`create_date`) COMMENT 'Index for chronological sorting'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Chat Messages Table';