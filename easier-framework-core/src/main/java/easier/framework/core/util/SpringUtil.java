package easier.framework.core.util;


import cn.hutool.core.collection.CollUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * spring工具类
 */
public class SpringUtil extends cn.hutool.extra.spring.SpringUtil {
    public static final String local = "local";
    public static final String dev = "dev";
    public static final String test = "test";
    public static final String prod = "prod";

    private final static Cache<Object, Object> _cache = Caffeine.newBuilder().build();

    /**
     * 获取实例并缓存
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAndCache(Class<T> clazz) {
        return (T) _cache.get(clazz, key -> getBean(clazz));
    }

    /**
     * 懒获取实例
     */
    public static <T> Supplier<T> lazyBean(Class<T> clazz) {
        return lazyBean(clazz, false);
    }


    /**
     * 懒获取实例
     */
    public static <T> Supplier<T> lazyBean(Class<T> clazz, boolean nullable) {
        return Suppliers.memoize(() -> {
            T bean = SpringUtil.getBeanDefaultNull(clazz);
            if (bean != null || nullable) {
                return bean;
            }
            throw FrameworkException.of("未找到[{}]实例", clazz.getSimpleName());
        });
    }

    public static <T> T getBeanDefaultNull(Class<T> clazz) {
        try {
            return getBean(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static ThreadPoolTaskExecutor getExecutor() {
        return getAndCache(ThreadPoolTaskExecutor.class);
    }

    public static ThreadPoolTaskScheduler getScheduler() {
        return getAndCache(ThreadPoolTaskScheduler.class);
    }

    public static int getServerPort() {
        return getServerPort(8080);
    }

    public static int getServerPort(int defaultValue) {
        return getApplicationContext()
                .getEnvironment()
                .getProperty("server.port", Integer.class, defaultValue);
    }

    public static <T> T getBeanByClassName(Class<T> type, String className) {
        return SpringUtil.getBeansOfType(type)
                .values()
                .stream()
                .filter(it -> {
                    if (it.getClass().getName().equals(className)) {
                        return true;
                    }
                    boolean interfaceMatch = Arrays.stream(it.getClass().getInterfaces())
                            .map(Class::getName)
                            .anyMatch(interfaceName -> interfaceName.equals(className));
                    boolean superClassMatch = it.getClass().getSuperclass().getName().equals(className);
                    return interfaceMatch || superClassMatch;

                })
                .findAny()
                .orElse(null);
    }

    /**
     * 根据注解获取实例方法
     */
    public static <A extends Annotation> Map<Object, Map<Method, A>> getMethodByAnnotation(Class<A> annotationType) {
        ApplicationContext applicationContext = getApplicationContext();
        MethodIntrospector.MetadataLookup<A> lookup = method -> AnnotatedElementUtils.findMergedAnnotation(method, annotationType);
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        Map<Object, Map<Method, A>> result = new HashMap<>();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            try {
                Map<Method, A> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(), lookup);
                if (CollUtil.isNotEmpty(annotatedMethods)) {
                    result.put(bean, annotatedMethods);
                }
            } catch (Throwable ignored) {
            }
        }
        return result;
    }
}
