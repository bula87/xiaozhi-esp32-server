-- Add powermem memory model provider
INSERT INTO `ai_model_provider` (`id`, `model_type`, `provider_code`, `name`, `fields`, `sort`, `creator`, `create_date`, `updater`, `update_date`)
VALUES ('SYSTEM_Memory_powermem', 'Memory', 'powermem', 'PowerMem Memory', '[
  {"key":"enable_user_profile","label":"Enable User Profile","type":"boolean"},
  {"key":"llm_provider","label":"LLM Provider","type":"string"},
  {"key":"llm_api_key","label":"LLM API Key","type":"string"},
  {"key":"llm_model","label":"LLM Model","type":"string"},
  {"key":"openai_base_url","label":"OpenAI Base URL","type":"string"},
  {"key":"embedding_provider","label":"Embedding Provider","type":"string"},
  {"key":"embedding_api_key","label":"Embedding API Key","type":"string"},
  {"key":"embedding_model","label":"Embedding Model","type":"string"},
  {"key":"embedding_openai_base_url","label":"Embedding OpenAI Base URL","type":"string"},
  {"key":"embedding_dims","label":"Embedding Dimensions","type":"integer"},
  {"key":"vector_store","label":"Vector Store Configuration (JSON)","type":"dict"}
]', 4, 1, NOW(), 1, NOW());

-- Add PowerMem memory model configuration
INSERT INTO `ai_model_config` VALUES (
  'Memory_powermem',
  'Memory',
  'powermem',
  'PowerMem Memory',
  0,
  1,
  '{\"type\": \"powermem\", \"enable_user_profile\": true, \"llm_provider\": \"openai\", \"llm_api_key\": \"Your LLM API Key\", \"llm_model\": \"qwen-plus\", \"openai_base_url\": \"\", \"embedding_provider\": \"openai\", \"embedding_api_key\": \"Your Embedding Model API Key\", \"embedding_model\": \"text-embedding-v4\", \"embedding_openai_base_url\": \"https://api.openai.com/v1\", \"embedding_dims\": \"\", \"vector_store\": {\"provider\": \"sqlite\", \"config\": {}}}',
  NULL,
  NULL,
  4,
  NULL,
  NULL,
  NULL,
  NULL
);


-- PowerMem memory configuration description
UPDATE `ai_model_config` SET
`doc_link` = 'https://github.com/oceanbase/powermem',
`remark` = 'PowerMem is an open-source agent memory component by OceanBase that performs memory summarization using local LLMs.
GitHub: https://github.com/oceanbase/powermem
Official Website: https://www.powermem.ai/
Usage Examples: https://github.com/oceanbase/powermem/tree/main/examples

[Cost Description]
PowerMem itself is free. The actual cost depends on the selected LLM and database:
- Using sqlite + free LLM (e.g., glm-4-flash) = completely free
- Using cloud-based LLM or cloud database = charged according to the corresponding service

[enable_user_profile] User Profile Feature
- false: Use standard memory mode (AsyncMemory)
- true: Use user profile mode (UserMemory), automatically extracting user information
- User profile feature supports: oceanbase, seekdb, sqlite (powermem 0.3.0+)

[llm] LLM Configuration - Used for memory summarization and user profile extraction
  provider: LLM provider, optional values:
    - qwen: Qwen (https://bailian.console.aliyun.com/?apiKey=1#/api-key)
    - openai: OpenAI-compatible interface
    - zhipu: Zhipu AI (https://bigmodel.cn/usercenter/proj-mgmt/apikeys) - Recommended to use free glm-4-flash
  config: LLM configuration parameters
    - api_key: API key (required)
    - model: Model name, such as qwen-plus, glm-4-flash, etc.
    - openai_base_url: Custom service address (optional), e.g., https://api.openai.com/v1
  Example:
    {"provider": "zhipu", "config": {"api_key": "your_key", "model": "glm-4-flash"}}
    {"provider": "qwen", "config": {"api_key": "your_key", "model": "qwen-plus"}}

[embedder] Embedding Configuration - Used for vectorizing memory content
  provider: Embedding model provider, optional values:
    - qwen: Qwen
    - openai: OpenAI-compatible interface
  config: Embedding configuration parameters
    - api_key: API key (required)
    - model: Model name, such as text-embedding-v4, text-embedding-3-small, etc.
    - openai_base_url: Custom service address (optional)
    - embedding_dims: Vector dimensions (optional), must be configured if not 1536
  Example:
    {"provider": "openai", "config": {"api_key": "your_key", "model": "text-embedding-v4", "openai_base_url": "https://dashscope.aliyuncs.com/compatible-mode/v1"}}

[vector_store] Database Storage Configuration - Used for storing vectorized memories
  provider: Database type, optional values:
    - sqlite: Lightweight local database (recommended for beginners, no additional configuration required)
    - oceanbase: OceanBase database (recommended for production use, best performance)
    - seekdb: SeekDB (recommended, integrated storage for AI applications)
    - postgres: PostgreSQL database

  SQLite Configuration (no additional configuration required):
    {"provider": "sqlite", "config": {}}

  OceanBase Configuration Example:
    {"provider": "oceanbase", "config": {
      "host": "127.0.0.1",
      "port": 2881,
      "user": "root@test",
      "password": "your_password",
      "db_name": "powermem",
      "collection_name": "memories",
      "embedding_model_dims": 1024
    }}
  Notes:
    - collection_name: Default table name. If dimension error occurs, delete this table or change the name
    - embedding_model_dims: Embedding vector dimensions, must match the embedder model dimensions
      For example, Zhipu: embedding-2 dimension is 1024, embedding-3 dimension is 2048

[Recommended Configuration Combinations]
1. Completely Free Plan:
   - LLM: zhipu + glm-4-flash (free)
   - Embedder: Qwen text-embedding-v4
   - Database: sqlite

2. Production Environment Plan:
   - LLM: qwen-plus or other commercial models
   - Embedder: text-embedding-v4
   - Database: oceanbase or seekdb
'
WHERE `id` = 'Memory_powermem';
 
 