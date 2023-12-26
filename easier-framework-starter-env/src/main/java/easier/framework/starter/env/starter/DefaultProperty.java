package easier.framework.starter.env.starter;

import lombok.Builder;
import lombok.Data;

/**
 * 默认属性
 *
 * @author lizhian
 * @date 2023年07月20日
 */
@Data
@Builder
public class DefaultProperty {
    /**
     * 注释
     */
    private final String comment;
    /**
     * 键
     */
    private final String key;
    /**
     * 值
     */
    private final String value;
}
