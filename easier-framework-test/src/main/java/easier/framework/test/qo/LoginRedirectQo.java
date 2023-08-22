package easier.framework.test.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求对象
 *
 * @author lizhian
 * @date 2023年07月18日
 */
@Data
public class LoginRedirectQo {
    @Schema(description = "重定向前端地址的时候是否使用?符号返回token,true=[frontend_url?token=xxx],false=[frontend_url#token=xxx]")
    private boolean question_mark;

    @Schema(description = "前端地址")
    @NotBlank
    private String frontend_uri;
}
