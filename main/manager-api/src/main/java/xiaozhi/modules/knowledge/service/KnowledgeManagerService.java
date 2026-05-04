package xiaozhi.modules.knowledge.service;

import java.util.List;

/**
 * Knowledge library module domain orchestration service
 * Used to handle complex business processes across KnowledgeBase and KnowledgeFiles, completely solving cyclic dependency issues between Services.
 */
public interface KnowledgeManagerService {

    /**
     * Cascade delete knowledge base and all subordinate documents (including local DB and RAGFlow remote data)
     * 
     * @param datasetId Knowledge base ID
     */
    void deleteDatasetWithFiles(String datasetId);

    /**
     * Batch cascade delete knowledge bases
     * 
     * @param datasetIds List of knowledge base IDs
     */
    void batchDeleteDatasetsWithFiles(List<String> datasetIds);
}
 