-- Modify memory model name
UPDATE `ai_model_config` SET `model_name` = 'Local short-term memory（summary memory）' WHERE `id` = 'Memory_mem_local_short';
UPDATE `ai_model_provider` SET `name` = 'Local short-term memory（summary memory）' WHERE `id` = 'SYSTEM_Memory_mem_local_short';

UPDATE `ai_model_config` SET `model_name` = 'Only upload chat records（no summary memory）' WHERE `id` = 'Memory_mem_report_only';
UPDATE `ai_model_provider` SET `name` = 'Only upload chat records（no summary memory）' WHERE `id` = 'SYSTEM_Memory_mem_report_only';
 