package xiaozhi.modules.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * Query all devices DTO
 * 
 * @author zjy
 * @since 2025-3-21
 */
@Data
@Schema(description = "Query all devices DTO")
public class DevicePageUserDTO {

    @Schema(description = "Device keywords")
    private String keywords;

    @Schema(description = "Page number")
    @Min(value = 0, message = "{page.number}")
    private String page;

    @Schema(description = "Display column count")
    @Min(value = 0, message = "{limit.number}")
    private String limit;
}
 