package easier.framework.core.plugin.event;

import java.lang.annotation.*;

/**
 * 全局广播事件
 *
 * @author lizhian
 * @date 2023年07月17日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
public @interface BroadcastEvent {
}
