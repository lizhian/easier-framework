package easier.framework.test.vo;

import lombok.*;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class Oauth2TokenVo {
    private String access_token;
    private String refresh_token;
    private Long expires_in;
    private Long refresh_expires_in;
    private String client_id;
    private String account;
}
