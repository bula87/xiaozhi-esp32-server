package xiaozhi.modules.sys.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * Password retrieval DTO
 */
@Data
@Schema(description = "Password Retrieval")
public class RetrievePasswordDTO implements Serializable {

    @Schema(description = "Phone Number")
    @NotBlank(message = "{sysuser.password.require}")
    private String phone;

    @Schema(description = "Verification Code")
    @NotBlank(message = "{sysuser.password.require}")
    private String code;

    @Schema(description = "New Password")
    @NotBlank(message = "{sysuser.password.require}")
    private String password;

    @Schema(description = "Captcha ID")
    @NotBlank(message = "{sysuser.uuid.require}")
    private String captchaId;



}
 