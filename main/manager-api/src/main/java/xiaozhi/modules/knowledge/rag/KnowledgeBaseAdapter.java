package xiaozhi.modules.knowledge.rag;

import java.util.List;
import java.util.Map;

import xiaozhi.modules.knowledge.dto.dataset.DatasetDTO;

import xiaozhi.common.page.PageData;
import xiaozhi.modules.knowledge.dto.KnowledgeFilesDTO;
import xiaozhi.modules.knowledge.dto.document.DocumentDTO;
import xiaozhi.modules.knowledge.dto.document.ChunkDTO;
import xiaozhi.modules.knowledge.dto.document.RetrievalDTO;
import java.util.function.Consumer;

/**
 * Knowledge Base Adapter abstract base class
 * Define a generic knowledge base operation interface, supporting multiple backend API implementations
 */
public abstract class KnowledgeBaseAdapter {

        /**
         * Get adapter type identifier
         * 
         * @return Adapter type (e.g., ragflow, milvus, pinecone etc.)
         */
        public abstract String getAdapterType();

        /**
         * Initialize adapter configuration
         * 
         * @param config Configuration parameters
         */
        public abstract void initialize(Map<String, Object> config);

        /**
         * Validate if the configuration is valid
         * 
         * @param config Configuration parameters
         * @return Validation result
         */
        public abstract boolean validateConfig(Map<String, Object> config);

        /**
         * Paginated query document list
         * 
         * @param datasetId   Knowledge base ID
         * @param queryParams Query parameters
         * @param page        Page number
         * @param limit       Number of items per page
         * @return Paginated data
         */
        public abstract PageData<KnowledgeFilesDTO> getDocumentList(String datasetId,
                        DocumentDTO.ListReq req);

        /**
         * Get document details by document ID
         * 
         * @param datasetId  Knowledge base ID
         * @param documentId Document ID
         * @return Document details (strongly typed InfoVO)
         */
        public abstract DocumentDTO.InfoVO getDocumentById(String datasetId, String documentId);

        /**
         * Upload document to knowledge base
         * 
         * @param req Upload request parameters
         * @return Uploaded document information
         */
        public abstract KnowledgeFilesDTO uploadDocument(DocumentDTO.UploadReq req);

        /**
         * Paginated query document list by status
         * 
         * @param datasetId Knowledge base ID
         * @param status    Document parsing status
         * @param page      Page number
         * @param limit     Number of items per page
         * @return Paginated data
         */
        public abstract PageData<KnowledgeFilesDTO> getDocumentListByStatus(String datasetId,
                        Integer status,
                        Integer page,
                        Integer limit);

        /**
         * Delete document (supports batch deletion)
         * 
         * @param datasetId Knowledge base ID
         * @param req       Request object containing list of document IDs
         */
        public abstract void deleteDocument(String datasetId, DocumentDTO.BatchIdReq req);

        /**
         * Parse documents (chunking)
         * 
         * @param datasetId   Knowledge base ID
         * @param documentIds List of document IDs
         * @return Parsing result
         */
        public abstract boolean parseDocuments(String datasetId, List<String> documentIds);

        /**
         * List chunks of a specified document
         * 
         * @param datasetId  Knowledge base ID
         * @param documentId Document ID
         * @param req        Listing request parameters (pagination, keywords etc.)
         * @return Chunk list VO
         */
        public abstract ChunkDTO.ListVO listChunks(String datasetId,
                        String documentId,
                        ChunkDTO.ListReq req);

        /**
         * Retrieval test - Retrieve relevant chunks from the knowledge base
         * 
         * @param req Retrieval test request parameters
         * @return Retrieval test result
         */
        public abstract RetrievalDTO.ResultVO retrievalTest(
                        RetrievalDTO.TestReq req);

        /**
         * Test connection
         * 
         * @return Connection test result
         */
        public abstract boolean testConnection();

        /**
         * Get adapter status information
         * 
         * @return Status information
         */
        public abstract Map<String, Object> getStatus();

        /**
         * Get supported configuration parameters
         * 
         * @return Configuration parameter descriptions
         */
        public abstract Map<String, Object> getSupportedConfig();

        /**
         * Get default configuration
         * 
         * @return Default configuration
         */
        public abstract Map<String, Object> getDefaultConfig();

        /**
         * Create dataset
         * 
         * @param req Creation parameters
         * @return Dataset details
         */
        public abstract DatasetDTO.InfoVO createDataset(DatasetDTO.CreateReq req);

        /**
         * Update dataset
         * 
         * @param datasetId Dataset ID
         * @param req       Update parameters
         * @return Dataset details
         */
        public abstract DatasetDTO.InfoVO updateDataset(String datasetId, DatasetDTO.UpdateReq req);

        /**
         * Delete dataset
         * 
         * @param req Deletion request parameters (containing ID list)
         * @return Batch operation result
         */
        public abstract DatasetDTO.BatchOperationVO deleteDataset(DatasetDTO.BatchIdReq req);

        /**
         * Get the number of documents in a dataset
         *
         * @param datasetId Dataset ID
         * @return Number of documents
         */
        public abstract Integer getDocumentCount(String datasetId);

        /**
         * Get full information of a dataset (name, summary, number of documents etc.)
         * Used to detect if RAGFlow end has been deleted or synchronized name/summary change
         *
         * @param datasetId Dataset ID
         * @return Dataset details, returns null if RAGFlow end does not exist
         */
        public abstract DatasetDTO.InfoVO getDatasetInfo(String datasetId);

        /**
         * Send streaming request (SSE)
         * 
         * @param endpoint API endpoint
         * @param body     Request body
         * @param onData   Data callback
         */
        public abstract void postStream(String endpoint, Object body, Consumer<String> onData);

        /**
         * SearchBot Ask
         *
         * @param config RAG configuration
         * @param body   Request body
         * @param onData Data callback
         * @return Response object
         */
        public abstract Object postSearchBotAsk(Map<String, Object> config, Object body,
                        Consumer<String> onData);

        /**
         * AgentBot Completion
         *
         * @param config  RAG configuration
         * @param agentId Agent ID
         * @param body    Request body
         * @param onData  Data callback
         */
        public abstract void postAgentBotCompletion(Map<String, Object> config, String agentId, Object body,
                        Consumer<String> onData);
}
 