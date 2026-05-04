package xiaozhi.modules.voiceclone.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import xiaozhi.modules.model.dto.VoiceDTO;
import xiaozhi.modules.voiceclone.entity.VoiceCloneEntity;

/**
 * Voice Cloning
 */
@Mapper
public interface VoiceCloneDao extends BaseMapper<VoiceCloneEntity> {
    /**
     * Get the list of successfully trained voice colors for a user
     * 
     * @param modelId Model ID
     * @param userId  User ID
     * @return List of successfully trained voice colors
     */
    List<VoiceDTO> getTrainSuccess(String modelId, Long userId);

}
 