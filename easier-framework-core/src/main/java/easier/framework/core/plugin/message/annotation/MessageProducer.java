package easier.framework.core.plugin.message.annotation;

import easier.framework.core.plugin.codec.args.ArgsCodec;
import easier.framework.core.plugin.codec.args.JsonArgsCodec;
import easier.framework.core.plugin.message.enums.MessageChannel;
import easier.framework.core.plugin.message.enums.MessageProvider;

import java.lang.annotation.*;


/**
 * 标记为消息生产者
 *
 * @author lizhian
 * @date 2023年11月24日
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MessageProducer {
    /**
     * 消息队列源
     */
    String source() default "";

    /**
     * 队列名称
     */
    String name() default "";


    /**
     * 消息频道
     */
    MessageChannel channel() default MessageChannel.queue;

    /**
     * 消息队列类型
     */
    MessageProvider provider() default MessageProvider.redis;

    /**
     * 编码器
     */
    Class<? extends ArgsCodec> codec() default JsonArgsCodec.class;
}
