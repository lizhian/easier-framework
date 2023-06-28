package easier.framework.core.plugin.mq.annotation;

import easier.framework.core.plugin.codec.Codec;
import easier.framework.core.plugin.codec.JacksonCodec;
import easier.framework.core.plugin.mq.enums.MQType;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Queue {
    /**
     * 队列名称
     */
    String name() default "";

    /**
     * 消息队列类型
     */
    MQType type();

    int max() default -1;

    /**
     * 编码器
     */
    Class<? extends Codec> codec() default JacksonCodec.class;

    /**
     * 消息队列源
     */
    String source() default "";
}
