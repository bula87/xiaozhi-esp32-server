package xiaozhi.modules.knowledge.service;

import java.util.List;
import java.util.Map;

import xiaozhi.common.page.PageData;
import xiaozhi.common.service.BaseService;
import xiaozhi.modules.knowledge.dto.KnowledgeBaseDTO;
import xiaozhi.modules.knowledge.entity.KnowledgeBaseEntity;
import xiaozhi.modules.model.entity.ModelConfigEntity;

/**
 * Knowledge base service interface
 */
public interface KnowledgeBaseService extends BaseService<KnowledgeBaseEntity> {

    /**
     * Paginate query knowledge base list
     *
     * @param knowledgeBaseDTO Query conditions
     * @param page             Page number
     * @param limit            Number of items per page
     * @return Paginated data
     */
    PageData<KnowledgeBaseDTO> getPageList(KnowledgeBaseDTO knowledgeBaseDTO, Integer page, Integer limit);

    /**
     * Get knowledge base details by ID
     *
     * @param id Knowledge base ID
     * @return Knowledge base details
     */
    KnowledgeBaseDTO getById(String id);

    /**
     * Add new knowledge base
     *
     * @param knowledgeBaseDTO Knowledge base information
     * @return Newly added knowledge base
     */
    KnowledgeBaseDTO save(KnowledgeBaseDTO knowledgeBaseDTO);

    /**
     * Update knowledge base
     *
     * @param knowledgeBaseDTO Knowledge base information
     * @return Updated knowledge base
     */
    KnowledgeBaseDTO update(KnowledgeBaseDTO knowledgeBaseDTO);

    /**
     * Query knowledge base by knowledge base ID
     *
     * @param datasetId Knowledge base ID
     * @return Knowledge base details
     */
    KnowledgeBaseDTO getByDatasetId(String datasetId);

    /**
     * Query knowledge bases by knowledge base ID list
     *
     * @param datasetIdList List of knowledge base IDs
     * @return Knowledge base details
     */
    List<KnowledgeBaseDTO> getByDatasetIdList(List<String> datasetIdList);

    /**
     * Delete knowledge base by knowledge base ID
     *
     * @param datasetId Knowledge base ID
     */
    void deleteByDatasetId(String datasetId);

    /**
     * Get RAG configuration information
     *
     * @param ragModelId RAG model configuration ID
     * @return RAG configuration information
     */
    Map<String, Object> getRAGConfig(String ragModelId);

    /**
     * Get corresponding RAG configuration by knowledge base ID
     *
     * @param datasetId Knowledge base ID
     * @return RAG configuration
     */
    Map<String, Object> getRAGConfigByDatasetId(String datasetId);

    /**
     * Get RAG model list
     *
     * @return RAG model list
     */
    List<ModelConfigEntity> getRAGModels();

    /**
     * Update knowledge base statistics (for callback by file service)
     *
     * @param datasetId  Knowledge base ID
     * @param docDelta   Incremental document count
     * @param chunkDelta Incremental chunk count
     * @param tokenDelta Incremental Token count
     */
    void updateStatistics(String datasetId, Integer docDelta, Long chunkDelta, Long tokenDelta);
}
 