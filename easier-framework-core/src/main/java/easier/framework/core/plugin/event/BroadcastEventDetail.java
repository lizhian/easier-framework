package easier.framework.core.plugin.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;


/**
 * 全局广播事件详情
 *
 * @author lizhian
 * @date 2023年07月18日
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastEventDetail {

    /**
     * 实例id
     */
    private String instanceId;
    /**
     * 跟踪id
     */
    private String traceId;
    /**
     * 应用事件
     */
    private ApplicationEvent applicationEvent;
}
