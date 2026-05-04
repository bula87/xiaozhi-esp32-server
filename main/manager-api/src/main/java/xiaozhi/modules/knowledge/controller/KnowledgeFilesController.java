package xiaozhi.modules.knowledge.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import xiaozhi.common.exception.ErrorCode;
import xiaozhi.common.exception.RenException;
import xiaozhi.common.page.PageData;
import xiaozhi.common.utils.Result;
import xiaozhi.modules.knowledge.dto.KnowledgeBaseDTO;
import xiaozhi.modules.knowledge.dto.KnowledgeFilesDTO;
import xiaozhi.modules.knowledge.dto.document.ChunkDTO;
import xiaozhi.modules.knowledge.dto.document.DocumentDTO;
import xiaozhi.modules.knowledge.dto.document.RetrievalDTO;
import xiaozhi.modules.knowledge.service.KnowledgeBaseService;
import xiaozhi.modules.knowledge.service.KnowledgeFilesService;
import xiaozhi.modules.security.user.SecurityUser;

@AllArgsConstructor
@RestController
@RequestMapping("/datasets/{dataset_id}")
@Tag(name = "Knowledge Base Document Management")
public class KnowledgeFilesController {

    private final KnowledgeFilesService knowledgeFilesService;
    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * Verify if the current user has permission to operate on the specified knowledge base
     * 
     * @param datasetId Knowledge base ID
     */
    private void validateKnowledgeBasePermission(String datasetId) {
        // Get the current logged-in user ID
        Long currentUserId = SecurityUser.getUserId();

        // Get knowledge base information
        KnowledgeBaseDTO knowledgeBase = knowledgeBaseService.getByDatasetId(datasetId);

        // Check permissions: Users can only operate on their own created knowledge bases
        if (knowledgeBase.getCreator() == null || !knowledgeBase.getCreator().equals(currentUserId)) {
            throw new RenException(ErrorCode.NO_PERMISSION);
        }
    }

    @GetMapping("/documents")
    @Operation(summary = "Paginated query document list")
    @RequiresPermissions("sys:role:normal")
    public Result<PageData<KnowledgeFilesDTO>> getPageList(
            @PathVariable("dataset_id") String datasetId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer page_size) {
        // Verify knowledge base permission
        validateKnowledgeBasePermission(datasetId);

        // Assemble parameters
        KnowledgeFilesDTO knowledgeFilesDTO = new KnowledgeFilesDTO();
        knowledgeFilesDTO.setDatasetId(datasetId);
        knowledgeFilesDTO.setName(name);
        knowledgeFilesDTO.setStatus(status);
        PageData<KnowledgeFilesDTO> pageData = knowledgeFilesService.getPageList(knowledgeFilesDTO, page, page_size);
        return new Result<PageData<KnowledgeFilesDTO>>().ok(pageData);
    }

    @GetMapping("/documents/status/{status}")
    @Operation(summary = "Paginated query document list by status")
    @RequiresPermissions("sys:role:normal")
    public Result<PageData<KnowledgeFilesDTO>> getPageListByStatus(
            @PathVariable("dataset_id") String datasetId,
            @PathVariable("status") String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer page_size) {
        // Verify knowledge base permission
        validateKnowledgeBasePermission(datasetId);
        // Assemble parameters
        KnowledgeFilesDTO knowledgeFilesDTO = new KnowledgeFilesDTO();
        knowledgeFilesDTO.setDatasetId(datasetId);
        knowledgeFilesDTO.setStatus(status);
        PageData<KnowledgeFilesDTO> pageData = knowledgeFilesService.getPageList(knowledgeFilesDTO, page, page_size);
        return new Result<PageData<KnowledgeFilesDTO>>().ok(pageData);
    }

    @PostMapping("/documents")
    @Operation(summary = "Upload document to knowledge base")
    @RequiresPermissions("sys:role:normal")
    public Result<KnowledgeFilesDTO> uploadDocument(
            @PathVariable("dataset_id") String datasetId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String chunkMethod,
            @RequestParam(required = false) String metaFields,
            @RequestParam(required = false) String parserConfig) {

        // Verify knowledge base permission
        validateKnowledgeBasePermission(datasetId);

        KnowledgeFilesDTO resp = knowledgeFilesService.uploadDocument(datasetId, file, name,
                metaFields != null ? parseJsonMap(metaFields) : null,
                chunkMethod,
                parserConfig != null ? parseJsonMap(parserConfig) : null);
        return new Result<KnowledgeFilesDTO>().ok(resp);
    }

    @DeleteMapping("/documents")
    @Operation(summary = "Batch delete documents")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> delete(@PathVariable("dataset_id") String datasetId,
            @RequestBody DocumentDTO.BatchIdReq req) {
        // Verify knowledge base permission
        validateKnowledgeBasePermission(datasetId);

        knowledgeFilesService.deleteDocuments(datasetId, req);
        return new Result<>();
    }

    @DeleteMapping("/documents/{document_id}")
    @Operation(summary = "Delete single document")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> deleteSingle(@PathVariable("dataset_id") String datasetId,
            @PathVariable("document_id") String documentId) {
        // Verify knowledge base permission
        validateKnowledgeBasePermission(datasetId);

        DocumentDTO.BatchIdReq req = new DocumentDTO.BatchIdReq();
        req.setIds(java.util.Collections.singletonList(documentId));
        knowledgeFilesService.deleteDocuments(datasetId, req);
        return new Result<>();
    }

    @PostMapping("/chunks")
    @Operation(summary = "Parse document (chunk)")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> parseDocuments(@PathVariable("dataset_id") String datasetId,
            @RequestBody Map<String, List<String>> requestBody) {
        // Verify knowledge base permission
        validateKnowledgeBasePermission(datasetId);

        List<String> documentIds = requestBody.get("document_ids");
        if (documentIds == null || documentIds.isEmpty()) {
            return new Result<Void>().error("document_ids parameter cannot be empty");
        }

        boolean success = knowledgeFilesService.parseDocuments(datasetId, documentIds);
        if (success) {
            return new Result<Void>();
        } else {
            return new Result<Void>().error("Document parsing failed, the document may still be processing");
        }
    }

    @GetMapping("/documents/{document_id}/chunks")
    @Operation(summary = "List chunks of specified document")
    @RequiresPermissions("sys:role:normal")
    public Result<ChunkDTO.ListVO> listChunks(
            @PathVariable("dataset_id") String datasetId,
            @PathVariable("document_id") String documentId,
            @ParameterObject ChunkDTO.ListReq req) {

        // Verify permission (internal contains knowledge base existence validation and ownership verification)
        validateKnowledgeBasePermission(datasetId);

        // Set default values
        if (req.getPage() == null)
            req.setPage(1);
        if (req.getPageSize() == null)
            req.setPageSize(50);

        // Call the service layer to get a strongly typed chunk list
        ChunkDTO.ListVO result = knowledgeFilesService.listChunks(datasetId, documentId, req);
        return new Result<ChunkDTO.ListVO>().ok(result);
    }

    @PostMapping("/retrieval-test")
    @Operation(summary = "Recall test")
    @RequiresPermissions("sys:role:normal")
    public Result<RetrievalDTO.ResultVO> retrievalTest(
            @PathVariable("dataset_id") String datasetId,
            @RequestBody RetrievalDTO.TestReq req) {

        // Verify knowledge base permission
        validateKnowledgeBasePermission(datasetId);

        // Business下沉 logic: If no knowledge base ID is specified, set it to the datasetId in the current path
        if (req.getDatasetIds() == null || req.getDatasetIds().isEmpty()) {
            req.setDatasetIds(java.util.Arrays.asList(datasetId));
        }

        // [Reinforce] Strongly control pagination parameters to prevent Negative Slicing error in RAGFlow end
        if (req.getPage() == null || req.getPage() < 1) {
            req.setPage(1);
        }
        if (req.getPageSize() == null || req.getPageSize() < 1) {
            req.setPageSize(100);
        }

        // Call retrieval service and return a strongly typed aggregation object
        RetrievalDTO.ResultVO result = knowledgeFilesService.retrievalTest(req);
        return new Result<RetrievalDTO.ResultVO>().ok(result);
    }

    /**
     * Parse JSON string to Map object
     */
    private Map<String, Object> parseJsonMap(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON string: " + jsonString, e);
        }
    }
}
 