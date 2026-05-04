package xiaozhi.modules.sys.service;

import xiaozhi.common.page.PageData;
import xiaozhi.common.service.BaseService;
import xiaozhi.modules.sys.dto.AdminPageUserDTO;
import xiaozhi.modules.sys.dto.PasswordDTO;
import xiaozhi.modules.sys.dto.SysUserDTO;
import xiaozhi.modules.sys.entity.SysUserEntity;
import xiaozhi.modules.sys.vo.AdminPageUserVO;

/**
 * System user
 */
public interface SysUserService extends BaseService<SysUserEntity> {

    SysUserDTO getByUsername(String username);

    SysUserDTO getByUserId(Long userId);

    void save(SysUserDTO dto);

    /**
     * Delete the specified user and all associated data devices and intelligent bodies
     * 
     * @param ids
     */
    void deleteById(Long ids);

    /**
     * Verify whether password modification is allowed
     * 
     * @param userId      User id
     * @param passwordDTO Password verification parameters
     */
    void changePassword(Long userId, PasswordDTO passwordDTO);

    /**
     * Directly modify the password without verification
     * 
     * @param userId   User id
     * @param password Password
     */
    void changePasswordDirectly(Long userId, String password);

    /**
     * Reset the password
     * 
     * @param userId User id
     * @return Randomly generated password that meets the specification
     */
    String resetPassword(Long userId);

    /**
     * Administrator paginated user information
     * 
     * @param dto Pagination search parameters
     * @return Paginated data of user list
     */
    PageData<AdminPageUserVO> page(AdminPageUserDTO dto);

    /**
     * Batch modify user status
     * 
     * @param status  User status
     * @param userIds Array of user IDs
     */
    void changeStatus(Integer status, String[] userIds);

    /**
     * Get whether user registration is allowed
     * 
     * @return Whether user registration is allowed
     */
    boolean getAllowUserRegister();
}
 