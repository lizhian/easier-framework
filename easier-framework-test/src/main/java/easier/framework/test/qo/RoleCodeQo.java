package easier.framework.test.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * app请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
@Data
public class RoleCodeQo {

    @Schema(description = "角色编码")
    @NotBlank
    private String roleCode;


}
