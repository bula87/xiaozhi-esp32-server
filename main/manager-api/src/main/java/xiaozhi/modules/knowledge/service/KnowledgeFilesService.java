package xiaozhi.modules.knowledge.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import xiaozhi.common.page.PageData;
import xiaozhi.modules.knowledge.dto.KnowledgeFilesDTO;
import xiaozhi.modules.knowledge.dto.document.ChunkDTO;
import xiaozhi.modules.knowledge.dto.document.RetrievalDTO;
import xiaozhi.modules.knowledge.dto.document.DocumentDTO;

/**
 * Knowledge base document service interface
 */
public interface KnowledgeFilesService {

        /**
         * Paginated query of document list
         * 
         * @param knowledgeFilesDTO Query conditions
         * @param page              Page number
         * @param limit             Number of items per page
         * @return Paginated data
         */
        PageData<KnowledgeFilesDTO> getPageList(KnowledgeFilesDTO knowledgeFilesDTO, Integer page, Integer limit);

        /**
         * Get document details by document ID and knowledge base ID
         * 
         * @param documentId Document ID
         * @param datasetId  Knowledge base ID
         * @return Document details (strongly typed InfoVO)
         */
        DocumentDTO.InfoVO getByDocumentId(String documentId, String datasetId);

        /**
         * Upload document to knowledge base
         * 
         * @param datasetId    Knowledge base ID
         * @param file         Uploaded file
         * @param name         Document name
         * @param metaFields   Metadata fields
         * @param chunkMethod  Chunking method
         * @param parserConfig Parser configuration
         * @return Uploaded document information
         */
        KnowledgeFilesDTO uploadDocument(String datasetId, MultipartFile file, String name,
                        Map<String, Object> metaFields, String chunkMethod,
                        Map<String, Object> parserConfig);

        /**
         * Batch delete documents
         * 
         * @param datasetId Knowledge base ID
         * @param req       Delete request parameters (contains document ID list)
         */
        void deleteDocuments(String datasetId, DocumentDTO.BatchIdReq req);

        /**
         * Get RAG configuration information
         * 
         * @param ragModelId RAG model configuration ID
         * @return RAG configuration information
         */
        Map<String, Object> getRAGConfig(String ragModelId);

        /**
         * Parse document (chunking)
         * 
         * @param datasetId   Knowledge base ID
         * @param documentIds Document ID list
         * @return Parsing result
         */
        boolean parseDocuments(String datasetId, List<String> documentIds);

        /**
         * List slices of a specified document
         * 
         * @param datasetId  Knowledge base ID
         * @param documentId Document ID
         * @param req        Slice list request parameters
         * @return Slice list information
         */
        ChunkDTO.ListVO listChunks(String datasetId, String documentId, ChunkDTO.ListReq req);

        /**
         * Recall test
         * 
         * @param req Retrieval test request parameters
         * @return Recall test result
         */
        RetrievalDTO.ResultVO retrievalTest(RetrievalDTO.TestReq req);

        /**
         * Save document shadow record
         */
        boolean saveDocumentShadow(String datasetId, KnowledgeFilesDTO result, String originalName, String chunkMethod,
                        Map<String, Object> parserConfig);

        /**
         * Batch delete document shadow records and synchronize statistics
         * 
         * @param documentIds Document ID list
         * @param datasetId   Dataset ID
         * @param chunkDelta  Total number of chunks to be deducted
         * @param tokenDelta  Total number of tokens to be deducted
         */
        void deleteDocumentShadows(List<String> documentIds, String datasetId, Long chunkDelta, Long tokenDelta);

        /**
         * Clean all associated documents based on dataset ID (for cascading deletion)
         * 
         * @param datasetId Dataset ID
         */
        void deleteDocumentsByDatasetId(String datasetId);

        /**
         * Synchronize all documents in RUNNING status (for scheduled task invocation)
         */
        void syncRunningDocuments();

        /**
         * Fully synchronize documents from RAGFlow to local shadow table
         * Pull all remote documents, compare with local shadow table, and insert missing records
         *
         * @param datasetId Dataset ID
         * @return Number of newly synchronized documents
         */
        int syncDocumentsFromRAG(String datasetId);
}
 