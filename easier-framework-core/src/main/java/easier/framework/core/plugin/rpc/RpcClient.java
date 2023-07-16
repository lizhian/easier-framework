package easier.framework.core.plugin.rpc;

import easier.framework.core.plugin.rpc.enums.HostType;

import java.lang.annotation.*;

/**
 * 内部请求服务名称
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcClient {
    String PATH = "/easier/rpc";

    String host();

    HostType type();

    String path() default RpcClient.PATH;
}
