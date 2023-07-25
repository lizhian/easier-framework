package easier.framework.test.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 角色分配app请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
@Data
public class RoleAssignAppQo {
    @Schema(description = "角色编码")
    private String roleCode;
    @Schema(description = "应用编码")
    private List<String> appCodes;
}
