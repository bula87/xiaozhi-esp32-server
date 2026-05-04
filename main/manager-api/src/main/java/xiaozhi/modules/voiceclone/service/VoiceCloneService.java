package xiaozhi.modules.voiceclone.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import xiaozhi.common.page.PageData;
import xiaozhi.common.service.BaseService;
import xiaozhi.modules.voiceclone.dto.VoiceCloneDTO;
import xiaozhi.modules.voiceclone.dto.VoiceCloneResponseDTO;
import xiaozhi.modules.voiceclone.entity.VoiceCloneEntity;

/**
 * Voice Clone Management
 */
public interface VoiceCloneService extends BaseService<VoiceCloneEntity> {

    /**
     * Paging Query
     */
    PageData<VoiceCloneEntity> page(Map<String, Object> params);

    /**
     * Save Voice Clone
     */
    void save(VoiceCloneDTO dto);

    /**
     * Batch Delete
     */
    void delete(String[] ids);

    /**
     * Query Voice Clone List by User ID
     * 
     * @param userId User ID
     * @return Voice Clone List
     */
    List<VoiceCloneEntity> getByUserId(Long userId);

    /**
     * Paging Query with Model Name and User Name
     */
    PageData<VoiceCloneResponseDTO> pageWithNames(Map<String, Object> params);

    /**
     * Query Voice Clone Information by ID with Model Name and User Name
     */
    VoiceCloneResponseDTO getByIdWithNames(String id);

    /**
     * Query Voice Clone List with Model Name by User ID
     */
    List<VoiceCloneResponseDTO> getByUserIdWithNames(Long userId);

    /**
     * Upload Audio File
     */
    void uploadVoice(String id, MultipartFile voiceFile) throws Exception;

    /**
     * Update Voice Clone Name
     */
    void updateName(String id, String name);

    /**
     * Get Audio Data
     */
    byte[] getVoiceData(String id);

    /**
     * Clone Audio, Call Volcano Engine for Voice Replication Training
     * 
     * @param cloneId Voice Clone Record ID
     */
    void cloneAudio(String cloneId);
}
 