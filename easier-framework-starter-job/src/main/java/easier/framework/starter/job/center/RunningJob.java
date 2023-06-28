package easier.framework.starter.job.center;

import lombok.Data;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Job 运行实例
 */
@Data
public class RunningJob {
    private final List<ScheduledFuture<?>> scheduledFutures;

    public void cancel() throws IOException {
        scheduledFutures.forEach(scheduledFuture -> scheduledFuture.cancel(false));
        scheduledFutures.clear();
    }

    public boolean isCancelled() {
        return scheduledFutures.isEmpty()
                || scheduledFutures.stream().allMatch(ScheduledFuture::isCancelled);
    }
}
