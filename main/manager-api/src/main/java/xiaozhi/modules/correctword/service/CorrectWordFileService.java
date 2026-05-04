package xiaozhi.modules.correctword.service;

import java.util.List;
import java.util.Map;

import xiaozhi.common.page.PageData;
import xiaozhi.modules.correctword.dto.CorrectWordFileCreateDTO;
import xiaozhi.modules.correctword.vo.CorrectWordFileVO;
import xiaozhi.modules.correctword.vo.CorrectWordSimpleVO;

public interface CorrectWordFileService {

    /**
     * Create replacement word file
     *
     * @param dto Creation parameters
     * @return File VO
     */
    CorrectWordFileVO createFile(CorrectWordFileCreateDTO dto);

    /**
     * Modify replacement word file (full replacement of entries)
     *
     * @param fileId File ID
     * @param dto    Modification parameters
     */
    void updateFile(String fileId, CorrectWordFileCreateDTO dto);

    /**
     * Get the list of replacement word files for the current user
     *
     * @param params Pagination parameters
     * @return Paginated data
     */
    PageData<CorrectWordFileVO> listFiles(Map<String, Object> params);

    /**
     * Get the list of replacement word files for the current user (no pagination, used for dropdown selection)
     *
     * @return File list
     */
    List<CorrectWordFileVO> listAllFiles();

    /**
     * Get the original content of the file (for download)
     *
     * @param fileId File ID
     * @return File entity
     */
    CorrectWordFileVO getFileContent(String fileId);

    /**
     * Delete replacement word file and all its entries and associated records
     *
     * @param fileId File ID
     */
    void deleteFile(String fileId);

    /**
     * Delete association records of the replacement word files related to the intelligent body (without deleting the file itself)
     *
     * @param agentId Intelligent body ID
     */
    void deleteMappingsByAgentId(String agentId);

    /**
     * Get all replacement entries of the intelligent body (simplified version, for use on device end)
     *
     * @param agentId Intelligent body ID
     * @return Replacement word list
     */
    List<CorrectWordSimpleVO> getAllItemsByAgentId(String agentId);

    /**
     * Get the list of replacement word file IDs associated with the intelligent body
     *
     * @param agentId Intelligent body ID
     * @return File ID list
     */
    List<String> getAgentCorrectWordFileIds(String agentId);

    /**
     * Save the replacement word files associated with the intelligent body (full replacement)
     *
     * @param agentId Intelligent body ID
     * @param fileIds File ID list
     */
    void saveAgentCorrectWords(String agentId, List<String> fileIds);

    /**
     * Batch delete replacement word files
     *
     * @param fileIds File ID list
     */
    void batchDeleteFiles(List<String> fileIds);
}
 