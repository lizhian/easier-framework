package tydic.framework.core.plugin.innerRequest;

import tydic.framework.core.plugin.codec.Codec;
import tydic.framework.core.plugin.codec.JacksonCodec;
import tydic.framework.core.plugin.innerRequest.enums.ServiceType;

import java.lang.annotation.*;

/**
 * 内部请求服务名称
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceDetail {

    ServiceType type() default ServiceType.discovery;

    boolean https() default false;

    String serviceId();

    String path() default InnerRequest.PATH;

    Class<? extends Codec> codec() default JacksonCodec.class;

}
