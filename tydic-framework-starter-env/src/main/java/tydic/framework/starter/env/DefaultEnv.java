package tydic.framework.starter.env;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DefaultEnv {
    private final String comment;
    private final String key;
    private final String value;
}
