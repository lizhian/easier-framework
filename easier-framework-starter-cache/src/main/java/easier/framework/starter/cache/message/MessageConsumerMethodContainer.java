package easier.framework.starter.cache.message;

import cn.hutool.core.thread.SemaphoreRunnable;
import cn.hutool.core.util.ReflectUtil;
import easier.framework.core.Easier;
import easier.framework.core.plugin.codec.args.ArgsCodec;
import easier.framework.core.plugin.message.MessageBean;
import easier.framework.core.plugin.message.MessageContainer;
import easier.framework.core.plugin.message.RedisMessageUtil;
import easier.framework.core.plugin.message.annotation.MessageConsumer;
import easier.framework.core.plugin.message.annotation.MessageProducer;
import easier.framework.core.plugin.message.enums.MessageChannel;
import easier.framework.core.plugin.message.enums.MessageProvider;
import easier.framework.core.plugin.message.fallback.MessageConsumerFallback;
import easier.framework.core.util.AnnotationUtil;
import easier.framework.core.util.SpringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.Semaphore;

/**
 * 消息消费者方法容器
 *
 * @author lizhian
 * @date 2023年12月21日
 */
@Slf4j
@Data
public class MessageConsumerMethodContainer {
    /**
     * 豆
     */
    private final MessageBean bean;
    /**
     * bean方法
     */
    private final Method beanMethod;
    /**
     * 来源
     */
    private final String source;
    /**
     * 姓名
     */
    private final String name;
    /**
     * 频道
     */
    private final MessageChannel channel;
    /**
     * 提供程序
     */
    private final MessageProvider provider;
    /**
     * 编解码器
     */
    private final ArgsCodec codec;
    /**
     * 启用
     */
    private final boolean enable;
    /**
     * 并发
     */
    private final int concurrency;
    /**
     * 投票
     */
    private final int poll;
    /**
     * 延迟
     */
    private final Duration delay;
    /**
     * 回退
     */
    private final MessageConsumerFallback fallback;


    /**
     * 实现MessageConsumerMethodContainer类的构造方法。
     * 通过指定的参数初始化MessageConsumerMethodContainer对象。
     *
     * @param bean            消费者消息的MessageBean对象
     * @param beanMethod      MessageBean类中的被消费者消息的方法
     * @param interfaceMethod 接口方法的引用
     */
    public MessageConsumerMethodContainer(MessageBean bean, Method beanMethod, Method interfaceMethod) {
        MessageProducer[] messageProducers = new MessageProducer[]{
                AnnotationUtil.getAnnotation(beanMethod, MessageProducer.class),
                AnnotationUtil.getAnnotation(beanMethod.getDeclaringClass(), MessageProducer.class),
                AnnotationUtil.getAnnotation(interfaceMethod, MessageProducer.class),
                AnnotationUtil.getAnnotation(interfaceMethod.getDeclaringClass(), MessageProducer.class),
                AnnotationUtil.getAnnotation(MessageBean.class, MessageProducer.class)
        };
        MessageConsumer[] messageConsumers = new MessageConsumer[]{
                AnnotationUtil.getAnnotation(beanMethod, MessageConsumer.class),
                AnnotationUtil.getAnnotation(beanMethod.getDeclaringClass(), MessageConsumer.class),
                AnnotationUtil.getAnnotation(interfaceMethod, MessageConsumer.class),
                AnnotationUtil.getAnnotation(interfaceMethod.getDeclaringClass(), MessageConsumer.class),
                AnnotationUtil.getAnnotation(MessageBean.class, MessageConsumer.class)
        };
        this.bean = bean;
        this.beanMethod = beanMethod;
        this.source = AnnotationUtil.getFirstAvailableValue(MessageProducer::source, messageProducers);
        this.name = AnnotationUtil.getFirstAvailableValue(RedisMessageUtil.getName(interfaceMethod), MessageProducer::source, messageProducers);
        this.channel = AnnotationUtil.getFirstAvailableValue(MessageProducer::channel, messageProducers);
        this.provider = AnnotationUtil.getFirstAvailableValue(MessageProducer::provider, messageProducers);
        Class<? extends ArgsCodec> codecClass = AnnotationUtil.getFirstAvailableValue(MessageProducer::codec, messageProducers);
        this.codec = ReflectUtil.newInstanceIfPossible(codecClass);
        this.enable = AnnotationUtil.getFirstAvailableValue(MessageConsumer::enable, messageConsumers);
        this.concurrency = AnnotationUtil.getFirstAvailableValue(MessageConsumer::concurrency, messageConsumers);
        this.poll = AnnotationUtil.getFirstAvailableValue(MessageConsumer::take, messageConsumers);
        this.delay = AnnotationUtil.getFirstAvailableValue(it -> Duration.ofMillis(it.timeUnit().toMillis(it.take())), messageConsumers);
        Class<? extends MessageConsumerFallback> fallbackClass = AnnotationUtil.getFirstAvailableValue(MessageConsumer::fallback, messageConsumers);
        this.fallback = ReflectUtil.newInstanceIfPossible(fallbackClass);
    }


    /**
     * 启动消息监听
     */
    public void start() {
        if (!this.enable) {
            return;
        }
        this.beanMethod.setAccessible(true);
        // 创建消息容器
        MessageContainer<Object[]> container = Easier.Message.newMessage(Object[].class)
                .source(this.source)
                .name(this.name)
                .use(this.provider)
                .as(this.channel)
                .codec(this.codec)
                .build();
        if (this.concurrency == 1) {
            // 单线程处理消息
            container.asConsumer().setRichListener(this.poll, this.delay, message -> {
                try {
                    Object[] args = message.getBody();
                    // 调用目标方法
                    ReflectUtil.invoke(this.bean, this.beanMethod, args);
                } catch (Throwable error) {
                    this.fallback.onException(message, error, this.source, this.name, this.channel, this.provider, this.codec);
                }
            });
            log.info("启动消息监听:【{}.{}】", this.bean.getClass().getSimpleName(), this.beanMethod.getName());
            return;
        }
        // 多线程处理消息
        final Semaphore semaphore = new Semaphore(this.concurrency);
        final ThreadPoolTaskExecutor executor = SpringUtil.getExecutor();
        container.asConsumer().setRichListener(this.poll, this.delay, message -> {
            SemaphoreRunnable semaphoreRunnable = new SemaphoreRunnable(() -> {
                try {
                    RedisMessageUtil.receiveMessage(message);
                    Object[] args = message.getBody();
                    // 调用目标方法
                    ReflectUtil.invoke(this.bean, this.beanMethod, args);
                } catch (Throwable error) {
                    this.fallback.onException(message, error, this.source, this.name, this.channel, this.provider, this.codec);
                }
            }, semaphore);
            executor.submit(semaphoreRunnable);
        });
        log.info("启动消息监听:【{}.{}】", this.bean.getClass().getSimpleName(), this.beanMethod.getName());
    }
}
