package xiaozhi.modules.sys.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * Admin paging user parameters DTO
 * 
 * @author zjy
 * @since 2025-3-21
 */
@Data
@Schema(description = "Admin paging user parameters DTO")
public class AdminPageUserDTO {

    @Schema(description = "Mobile number")
    private String mobile;

    @Schema(description = "Page number")
    @Min(value = 0, message = "{sort.number}")
    private String page;

    @Schema(description = "Number of columns displayed")
    @Min(value = 0, message = "{sort.number}")
    private String limit;
}
 