package tydic.framework.starter.discovery.core;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

@Slf4j
@AllArgsConstructor
public class RedissonServiceRegistry implements ServiceRegistry<RedissonRegistration> {

    public static final String UP = "UP";
    public static final String DOWN = "DOWN";


    @Override
    public void register(RedissonRegistration registration) {
        log.info("register {}", registration);
    }

    @Override
    public void deregister(RedissonRegistration registration) {
        log.info("deregister {}", registration);
    }

    @Override
    public void close() {
        log.info("RedissonServiceRegistry  close");
    }

    @Override
    public void setStatus(RedissonRegistration registration, String status) {
        log.info("setStatus {} {}", registration, status);
    }

    @Override
    public <T> T getStatus(RedissonRegistration registration) {
        try {
            return (T) "111";
        } catch (Exception ignored) {

        }
        return null;
    }
}
