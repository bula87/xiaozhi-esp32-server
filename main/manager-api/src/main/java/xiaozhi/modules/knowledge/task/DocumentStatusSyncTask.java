package xiaozhi.modules.knowledge.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xiaozhi.modules.knowledge.service.KnowledgeFilesService;

/**
 * Knowledge base document status synchronization scheduled task
 * 
 * Function:
 * 1. Automatically scan documents in "RUNNING" (parsing) status
 * 2. Call RAGFlow interface to get the latest status
 * 3. When the status flips (RUNNING -> SUCCESS/FAIL), synchronously update the database
 * 4. [Key] When parsing is successful, compensate for updating the knowledge base statistics information (TokenCount)
 */
@Component
@AllArgsConstructor
@Slf4j
public class DocumentStatusSyncTask {

    private final KnowledgeFilesService knowledgeFilesService;

    /**
     * Execute synchronization every 30 seconds
     * Use fixedDelay to ensure that the next execution starts 30 seconds after the previous one is completed, preventing backlog
     */
    @Scheduled(fixedDelay = 30000)
    public void syncRunningDocuments() {
        try {
            // log.debug("Starting document status synchronization task...");
            knowledgeFilesService.syncRunningDocuments();
        } catch (Exception e) {
            log.error("Document status synchronization task exception", e);
        }
    }
}
 