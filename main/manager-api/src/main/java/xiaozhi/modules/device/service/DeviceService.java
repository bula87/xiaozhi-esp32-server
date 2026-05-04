package xiaozhi.modules.device.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import xiaozhi.common.page.PageData;
import xiaozhi.common.service.BaseService;
import xiaozhi.modules.device.dto.DeviceManualAddDTO;
import xiaozhi.modules.device.dto.DevicePageUserDTO;
import xiaozhi.modules.device.dto.DeviceReportReqDTO;
import xiaozhi.modules.device.dto.DeviceReportRespDTO;
import xiaozhi.modules.device.entity.DeviceEntity;
import xiaozhi.modules.device.vo.UserShowDeviceListVO;

public interface DeviceService extends BaseService<DeviceEntity> {
    /**
     * Get device online data
     */
    String getDeviceOnlineData(String agentId);

    /**
     * Check if device is activated
     */
    DeviceReportRespDTO checkDeviceActive(String macAddress, String clientId,
            DeviceReportReqDTO deviceReport);

    /**
     * Get device list of user specified agent,
     */
    List<DeviceEntity> getUserDevices(Long userId, String agentId);

    /**
     * Unbind device
     */
    void unbindDevice(Long userId, String deviceId);

    /**
     * Device activation
     */
    Boolean deviceActivation(String agentId, String activationCode);

    /**
     * Delete all devices of this user
     * 
     * @param userId user id
     */
    void deleteByUserId(Long userId);

    /**
     * Delete all devices associated with specified agent
     * 
     * @param agentId agent id
     */
    void deleteByAgentId(String agentId);

    /**
     * Get device count of specified user
     * 
     * @param userId user id
     * @return device count
     */
    Long selectCountByUserId(Long userId);

    /**
     * Get paginated all device information
     *
     * @param dto pagination query parameters
     * @return paginated user list data
     */
    PageData<UserShowDeviceListVO> page(DevicePageUserDTO dto);

    /**
     * Get device information by MAC address
     * 
     * @param macAddress MAC address
     * @return device information
     */
    DeviceEntity getDeviceByMacAddress(String macAddress);

    /**
     * Get activation code by device ID
     * 
     * @param deviceId device ID
     * @return activation code
     */
    String geCodeByDeviceId(String deviceId);

    /**
     * Get the latest last connection time of devices of this agent
     * 
     * @param agentId agent id
     * @return Return the device's most recent last connection time
     */
    Date getLatestLastConnectionTime(String agentId);

    /**
     * Manually add device
     */
    void manualAddDevice(Long userId, DeviceManualAddDTO dto);

    /**
     * Update device connection information
     */
    void updateDeviceConnectionInfo(String agentId, String deviceId, String appVersion);

    /**
     * Generate WebSocket authentication token
     *
     * @param clientId client ID
     * @param username username (usually deviceId)
     * @return authentication token string
     * @throws Exception exception when generating token
     */
    String generateWebSocketToken(String clientId, String username) throws Exception;

    /**
     * Search devices by MAC address
     *
     * @param macAddress MAC address keyword
     * @param userId     user ID
     * @return device list
     */
    List<DeviceEntity> searchDevicesByMacAddress(String macAddress, Long userId);

    /**
     * Get device tool list
     */
    Object getDeviceTools(String deviceId);

    /**
     * Call device tool
     */
    Object callDeviceTool(String deviceId, String toolName, Map<String, Object> arguments);

}
 