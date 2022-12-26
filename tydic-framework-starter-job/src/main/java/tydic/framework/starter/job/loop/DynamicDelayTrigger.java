package tydic.framework.starter.job.loop;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 动态Delay的Trigger
 */
public class DynamicDelayTrigger implements Trigger {
    private final AtomicLong delay = new AtomicLong(0);

    public DynamicDelayTrigger(long delay) {
        this.delay.set(delay);
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        Date lastExecution = triggerContext.lastScheduledExecutionTime();
        Date lastCompletion = triggerContext.lastCompletionTime();
        if (lastExecution == null || lastCompletion == null) {
            return new Date(triggerContext.getClock().millis());
        }
        return new Date(lastCompletion.getTime() + this.delay.get());
    }

    public void changeDelay(long delay, TimeUnit timeUnit) {
        this.delay.set(timeUnit.toMillis(delay));
    }
}
