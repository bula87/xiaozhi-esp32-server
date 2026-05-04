package xiaozhi.modules.sys.service;

import java.util.function.Consumer;

/**
 * Define a system user utility class to avoid circular dependencies with the user module
 * For example, if users and devices depend on each other, users need to get all devices, and devices need to get the username for each device.
 * @author zjy
 * @since 2025-4-2
 */
public interface SysUserUtilService {
    /**
     * Assign a username
     * @param userId User ID
     * @param setter Assignment method
     */
    void assignUsername(Long userId, Consumer<String> setter);
}
 