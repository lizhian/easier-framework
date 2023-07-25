package easier.framework.test.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 应用分配角色-请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
@Data
public class AppAssignRoleQo {
    @Schema(description = "应用编码")
    @NotBlank
    private String appCode;

    @Schema(description = "角色编码")
    private List<String> roleCodes;
}
