-- LLM intent recognition configuration explanation
UPDATE `ai_model_config` SET 
`doc_link` = NULL,
`remark` = 'LLM intent recognition configuration explanation:
1. Use an independent LLM for intent recognition
2. By default use the model of selected_module.LLM
3. Can configure to use an independent LLM (e.g., free ChatGLMLLM)
4. Strong versatility, but will increase processing time
Configuration explanation:
1. Specify the LLM model to use in the llm field
2. If not specified, then use the model of selected_module.LLM' WHERE `id` = 'Intent_intent_llm';

-- Function call intent recognition configuration explanation
UPDATE `ai_model_config` SET 
`doc_link` = NULL,
`remark` = 'Function call intent recognition configuration explanation:
1. Use the LLMs function_call feature for intent recognition
2. Require the selected LLM to support function_call
3. Call tools as needed, processing speed is fast' WHERE `id` = 'Intent_function_call';
 