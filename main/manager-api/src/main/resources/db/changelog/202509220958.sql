delete from `ai_model_config` where id = 'LLM_XunfeiSparkLLM';
INSERT INTO `ai_model_config` VALUES ('LLM_XunfeiSparkLLM', 'LLM', 'Xunfei Spark Cognitive Large Model', 'Xunfei Spark Cognitive Large Model', 0, 1, '{"type": "openai", "model_name": "generalv3.5", "base_url": "https://spark-api-open.xf-yun.com/v1", "api_password": "your api_password", "temperature": 0.5, "max_tokens": 2048, "top_p": 1.0, "frequency_penalty": 0.0}', 'https://www.xfyun.cn/doc/spark/HTTP%E8%B0%83%E7%94%A8%E6%96%87%E6%A1%A3.html', 'Xunfei Spark Cognitive Large Model，supports multi-turn dialogue、text generation and functions', 14, NULL, NULL, NULL, NULL);

-- Update Xunfei Spark Cognitive Large Model configuration documentation
UPDATE `ai_model_config` SET
`doc_link` = 'https://www.xfyun.cn/doc/spark/HTTP%E8%B0%83%E7%94%A8%E6%96%87%E6%A1%A3.html',
`remark` = 'Xunfei Spark Cognitive Large Model configuration explanation：
1. Log in Xunfei Open Platform https://www.xfyun.cn/，Each model corresponds to each api_password,When changing the model, you need to check the api_password corresponding to the model
2. Create Spark Cognitive Large Model application to obtain API Password
3. Parameter explanation：
   - api_password: API Password，After creating an application on the Xunfei Open Platform, obtain
   - model_name: model name，supports generalv3.5、generalv3 and other versions
   - base_url: API address，default https://spark-api-open.xf-yun.com/v1
   - temperature: temperature parameter，controls generation randomness，range 0-1，default 0.5
   - max_tokens: maximum output token count，default 2048
   - top_p: core sampling parameter，controls vocabulary diversity，default 1.0
   - frequency_penalty: frequency penalty，reduces repetitive content，default 0.0
4. Each model corresponds to each api_password,When changing the model, you need to check the api_password corresponding to the model。
' WHERE `id` = 'LLM_XunfeiSparkLLM';
 