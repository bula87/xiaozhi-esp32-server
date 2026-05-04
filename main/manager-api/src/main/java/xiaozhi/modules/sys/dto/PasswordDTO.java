package xiaozhi.modules.sys.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Change Password
 */
@Data
@Schema(description = "Change Password")
public class PasswordDTO implements Serializable {

    @Schema(description = "Old Password")
    @NotBlank(message = "{sysuser.password.require}")
    private String password;

    @Schema(description = "New Password")
    @NotBlank(message = "{sysuser.password.require}")
    private String newPassword;

}
 