package xiaozhi.modules.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User Display Device List VO")
public class UserShowDeviceListVO {

    @Schema(description = "app version")
    private String appVersion;

    @Schema(description = "bound user name")
    private String bindUserName;

    @Schema(description = "device model")
    private String deviceType;

    @Schema(description = "device unique identifier")
    private String id;

    @Schema(description = "MAC address")
    private String macAddress;

    @Schema(description = "enable OTA")
    private Integer otaUpgrade;

    @Schema(description = "last chat time")
    private String recentChatTime;

}
 