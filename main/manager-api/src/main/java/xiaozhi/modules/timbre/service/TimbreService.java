package xiaozhi.modules.timbre.service;

import java.util.List;

import xiaozhi.common.page.PageData;
import xiaozhi.common.service.BaseService;
import xiaozhi.modules.model.dto.VoiceDTO;
import xiaozhi.modules.timbre.dto.TimbreDataDTO;
import xiaozhi.modules.timbre.dto.TimbrePageDTO;
import xiaozhi.modules.timbre.entity.TimbreEntity;
import xiaozhi.modules.timbre.vo.TimbreDetailsVO;

/**
 * Timbre service definition
 * 
 * @author zjy
 * @since 2025-3-21
 */
public interface TimbreService extends BaseService<TimbreEntity> {
    /**
     * Paginated retrieval of timbres under a specified tts
     * 
     * @param dto Pagination query parameters
     * @return Paged data of timbre list
     */
    PageData<TimbreDetailsVO> page(TimbrePageDTO dto);

    /**
     * Retrieve detailed information of a specified id timbre
     * 
     * @param timbreId Timbre table id
     * @return Timbre information
     */
    TimbreDetailsVO get(String timbreId);

    /**
     * Save timbre information
     * 
     * @param dto Data to be saved
     */
    void save(TimbreDataDTO dto);

    /**
     * Update timbre information
     * 
     * @param timbreId Id to be updated
     * @param dto      Data to be updated
     */
    void update(String timbreId, TimbreDataDTO dto);

    /**
     * Batch delete timbres
     * 
     * @param ids List of timbre ids to be deleted
     */
    void delete(String[] ids);

    List<VoiceDTO> getVoiceNames(String ttsModelId, String voiceName);

    /**
     * Get timbre name by ID
     * 
     * @param id Timbre ID
     * @return Timbre name
     */
    String getTimbreNameById(String id);

    /**
     * Get timbre information by timbre code
     * 
     * @param ttsModelId Timbre model ID
     * @param voiceCode  Timbre code
     * @return Timbre information
     */
    VoiceDTO getByVoiceCode(String ttsModelId, String voiceCode);
}
 