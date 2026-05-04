-- Add only report chat records memory model provider
delete from `ai_model_provider` where `id` = 'SYSTEM_Memory_mem_report_only';
delete from `ai_model_config` where `id` = 'Memory_mem_report_only';

INSERT INTO `ai_model_provider` VALUES ('SYSTEM_Memory_mem_report_only', 'Memory', 'mem_report_only', 'Only report chat records', '[]', 4, 1, NOW(), 1, NOW());
INSERT INTO `ai_model_config` VALUES ('Memory_mem_report_only', 'Memory', 'mem_report_only', 'Only report chat records', 0, 1, '{"type": "mem_report_only"}', NULL, 'Only report chat records, do not summarize memory', 3, NULL, NULL, NULL, NULL);
 