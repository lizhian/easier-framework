package easier.framework.core.plugin.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastEventDetail {

    private String instanceId;
    private ApplicationEvent applicationEvent;
}
