package tydic.framework.starter.discovery.core;

import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

public class RedissonAutoServiceRegistration extends AbstractAutoServiceRegistration<RedissonRegistration> {
    public RedissonAutoServiceRegistration(ServiceRegistry<RedissonRegistration> serviceRegistry, AutoServiceRegistrationProperties properties) {
        super(serviceRegistry, properties);
    }

    @Override
    protected Object getConfiguration() {
        return null;
    }

    @Override
    protected boolean isEnabled() {
        return false;
    }

    @Override
    protected RedissonRegistration getRegistration() {
        return null;
    }

    @Override
    protected RedissonRegistration getManagementRegistration() {
        return null;
    }
}
