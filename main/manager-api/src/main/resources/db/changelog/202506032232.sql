-- VLLM model configuration
delete from `ai_model_config` where id = 'VLLM_QwenVLVLLM';
INSERT INTO `ai_model_config` VALUES ('VLLM_QwenVLVLLM', 'VLLM', 'QwenVLVLLM', 'Qwen vision model', 0, 1, '{\"type\": \"openai\", \"model_name\": \"qwen2.5-vl-3b-instruct\", \"base_url\": \"https://dashscope.aliyuncs.com/compatible-mode/v1\", \"api_key\": \"your api_key\"}', NULL, NULL, 2, NULL, NULL, NULL, NULL);

-- Update document
UPDATE `ai_model_config` SET 
`doc_link` = 'https://bailian.console.aliyun.com/?tab=api#/api/?type=model&url=https%3A%2F%2Fhelp.aliyun.com%2Fdocument_detail%2F2845564.html&renderType=iframe',
`remark` = 'Qwen vision model configuration description：
1. Visit https://bailian.console.aliyun.com/?tab=model#/api-key
2. Register and obtain API key
3. Fill in configuration file'
WHERE `id` = 'VLLM_QwenVLVLLM';

-- Delete parameters，these two parameters have been moved to python configuration file
delete from `sys_params` where id  in (113,114);
 