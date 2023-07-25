package easier.framework.starter.job.center;

import lombok.Data;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Job 运行实例
 */
@Data
public class RunningJob {
    private final List<ScheduledFuture<?>> scheduledFutures;

    public void cancel() {
        this.scheduledFutures.forEach(scheduledFuture -> scheduledFuture.cancel(false));
        this.scheduledFutures.clear();
    }

    public boolean isCancelled() {
        return this.scheduledFutures.isEmpty()
                || this.scheduledFutures.stream().allMatch(ScheduledFuture::isCancelled);
    }
}
