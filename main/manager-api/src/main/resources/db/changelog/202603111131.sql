-- Add some ragflow return parameters (when creating/querying knowledge base)
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ai_rag_dataset' AND COLUMN_NAME = 'tenant_id');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `ai_rag_dataset` ADD COLUMN `tenant_id` varchar(32) DEFAULT NULL COMMENT ''tenant ID''', 'SELECT ''Column tenant_id already exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ai_rag_dataset' AND COLUMN_NAME = 'avatar');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `ai_rag_dataset` ADD COLUMN `avatar` text DEFAULT NULL COMMENT ''knowledge base avatar (Base64)''', 'SELECT ''Column avatar already exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ai_rag_dataset' AND COLUMN_NAME = 'embedding_model');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `ai_rag_dataset` ADD COLUMN `embedding_model` varchar(50) DEFAULT NULL COMMENT ''embedding model name''', 'SELECT ''Column embedding_model already exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ai_rag_dataset' AND COLUMN_NAME = 'permission');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `ai_rag_dataset` ADD COLUMN `permission` varchar(20) DEFAULT ''me'' COMMENT ''permission settings: me/team''', 'SELECT ''Column permission already exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ai_rag_dataset' AND COLUMN_NAME = 'chunk_method');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `ai_rag_dataset` ADD COLUMN `chunk_method` varchar(50) DEFAULT NULL COMMENT ''chunk method''', 'SELECT ''Column chunk_method already exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ai_rag_dataset' AND COLUMN_NAME = 'parser_config');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `ai_rag_dataset` ADD COLUMN `parser_config` text DEFAULT NULL COMMENT ''parser configuration (JSON)''', 'SELECT ''Column parser_config already exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ai_rag_dataset' AND COLUMN_NAME = 'chunk_count');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `ai_rag_dataset` ADD COLUMN `chunk_count` bigint(20) DEFAULT 0 COMMENT ''total chunk count''', 'SELECT ''Column chunk_count already exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ai_rag_dataset' AND COLUMN_NAME = 'document_count');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `ai_rag_dataset` ADD COLUMN `document_count` bigint(20) DEFAULT 0 COMMENT ''total document count''', 'SELECT ''Column document_count already exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ai_rag_dataset' AND COLUMN_NAME = 'token_num');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `ai_rag_dataset` ADD COLUMN `token_num` bigint(20) DEFAULT 0 COMMENT ''total token count''', 'SELECT ''Column token_num already exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Document table (Shadow DB for RAGFlow)
-- Keep a copy of document id, associate ragflow remote document id with local id (just backup a metadata link, actually file content storage is still in ragflow)
DROP TABLE IF EXISTS `ai_rag_knowledge_document`;
CREATE TABLE `ai_rag_knowledge_document` (
     `id` varchar(36) NOT NULL COMMENT 'local unique ID',
     `dataset_id` varchar(36) NOT NULL COMMENT 'knowledge base ID (associated with ai_rag_dataset)',
     `document_id` varchar(64) NOT NULL COMMENT 'RAGFlow document ID (remote ID)',
     `name` varchar(255) DEFAULT NULL COMMENT 'document name',
     `size` bigint(20) DEFAULT NULL COMMENT 'file size (Bytes)',
     `type` varchar(20) DEFAULT NULL COMMENT 'file type',
     `chunk_method` varchar(50) DEFAULT NULL COMMENT 'chunk method',
     `parser_config` text COMMENT 'parser configuration (JSON)',
     `status` varchar(10) DEFAULT '1' COMMENT 'availability status (1: enabled 0: disabled)',
     `run` varchar(32) DEFAULT 'UNSTART' COMMENT 'run status (UNSTART/RUNNING/CANCEL/DONE/FAIL)',
     `progress` double DEFAULT '0' COMMENT 'parse progress (0.0 ~ 1.0)',
     `thumbnail` mediumtext COMMENT 'thumbnail (Base64 or URL)',
     `process_duration` double DEFAULT '0' COMMENT 'process duration (unit: seconds)',
     `meta_fields` text COMMENT 'custom metadata (JSON)',
     `source_type` varchar(32) DEFAULT 'local' COMMENT 'source type (local, s3, url, etc.)',
     `error` text COMMENT 'error information',
     `chunk_count` int(11) DEFAULT '0' COMMENT 'chunk count',
     `token_count` bigint(20) DEFAULT '0' COMMENT 'token count',
     `enabled` tinyint(1) DEFAULT '1' COMMENT 'enabled status',
     `creator` bigint(20) DEFAULT NULL COMMENT 'creator',
     `created_at` datetime DEFAULT NULL COMMENT 'creation time',
     `updated_at` datetime DEFAULT NULL COMMENT 'update time',
     `last_sync_at` datetime DEFAULT NULL COMMENT 'last sync time',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_doc_id` (`document_id`),
     KEY `idx_dataset_id` (`dataset_id`),
     KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='knowledge base document table';
 