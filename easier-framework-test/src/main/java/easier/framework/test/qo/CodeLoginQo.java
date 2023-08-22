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
public class CodeLoginQo {
    @Schema(description = "code授权码")
    @NotBlank
    private String code;

    @Schema(description = "是否使用?符号返回token,false=[redirect_uri#token=xxx],true=[redirect_uri?token=xxx]")
    private boolean use_question_mark;

    @Schema(description = "前端地址")
    private String frontend_url;


}
