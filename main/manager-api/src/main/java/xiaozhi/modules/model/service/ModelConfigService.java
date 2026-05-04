package xiaozhi.modules.model.service;

import java.util.List;
import java.util.Map;

import xiaozhi.common.page.PageData;
import xiaozhi.common.service.BaseService;
import xiaozhi.modules.model.dto.LlmModelBasicInfoDTO;
import xiaozhi.modules.model.dto.ModelBasicInfoDTO;
import xiaozhi.modules.model.dto.ModelConfigBodyDTO;
import xiaozhi.modules.model.dto.ModelConfigDTO;
import xiaozhi.modules.model.entity.ModelConfigEntity;

public interface ModelConfigService extends BaseService<ModelConfigEntity> {

    List<ModelBasicInfoDTO> getModelCodeList(String modelType, String modelName);

    List<LlmModelBasicInfoDTO> getLlmModelCodeList(String modelName);

    PageData<ModelConfigDTO> getPageList(String modelType, String modelName, String page, String limit);

    ModelConfigDTO add(String modelType, String provideCode, ModelConfigBodyDTO modelConfigBodyDTO);

    ModelConfigDTO edit(String modelType, String provideCode, String id, ModelConfigBodyDTO modelConfigBodyDTO);

    void delete(String id);

    /**
     * Get model name by ID
     * 
     * @param id Model ID
     * @return Model name
     */
    String getModelNameById(String id);

    /**
     * Get model configuration by ID from cache
     * 
     * @param id Model ID
     * @return Model configuration entity
     */
    ModelConfigEntity getModelByIdFromCache(String id);

    /**
     * Set default model
     *
     * @param modelType Model type
     * @param isDefault Whether it is default (1: Yes, 0: No)
     */
    void setDefaultModel(String modelType, int isDefault);

    /**
     * Get TTS platform list that meets the conditions
     *
     * @return TTS platform list(id and modelName)
     */
    List<Map<String, Object>> getTtsPlatformList();

    /**
     * Get all enabled model configurations by model type
     *
     * @param modelType Model type (e.g. LLM, TTS, ASR, etc.)
     * @return List of enabled model configurations
     */
    List<ModelConfigEntity> getEnabledModelsByType(String modelType);
}
 