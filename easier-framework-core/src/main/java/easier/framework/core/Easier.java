package easier.framework.core;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.aop.ProxyUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import easier.framework.core.plugin.auth.detail.AuthDetail;
import easier.framework.core.plugin.cache.container.CacheContainerBuilder;
import easier.framework.core.plugin.codec.BytesCodec;
import easier.framework.core.plugin.codec.JsonCodec;
import easier.framework.core.plugin.codec.StringCodec;
import easier.framework.core.plugin.codec.StringGzipCodec;
import easier.framework.core.plugin.enums.EnumCodec;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.plugin.jackson.ObjectMapperHolder;
import easier.framework.core.plugin.message.MessageBean;
import easier.framework.core.plugin.message.builder.MessageContainerBuilder;
import easier.framework.core.plugin.message.proxy.MessageProdukerInvocationHandler;
import easier.framework.core.util.SpringUtil;
import easier.framework.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class Easier {
    @SneakyThrows
    public static void main(String[] args) {

    }

    /**
     * 全局主键,默认使用雪花算法
     */
    public static class Id {
        private volatile static long dataCenterId = IdUtil.getDataCenterId(31);
        private volatile static long workerId = IdUtil.getWorkerId(dataCenterId, 31);

        @Nonnull
        public static String nextIdStr() {
            return Singleton.get(Snowflake.class, workerId, dataCenterId).nextIdStr();
        }

        public static long nextId() {
            return Singleton.get(Snowflake.class, workerId, dataCenterId).nextId();
        }

        public synchronized static void reset(long workerId, long dataCenterId) {
            Id.workerId = workerId;
            Id.dataCenterId = dataCenterId;
        }

    }

    /**
     * 链路ID
     */
    public static class TraceId {
        public static final String x_trace_id = "x-trace-id";
        private static final String disable_value = "-1";
        private static final TransmittableThreadLocal<String> THREAD_LOCAL = new TransmittableThreadLocal<>();

        public static String get() {
            return THREAD_LOCAL.get();
        }

        public static String getOr(String defaultVale) {
            String traceId = TraceId.get();
            if (StrUtil.isNotBlank(traceId)) {
                return traceId;
            }
            return defaultVale;
        }

        public static String getOrReset() {
            String traceId = TraceId.get();
            if (StrUtil.isNotBlank(traceId)) {
                return traceId;
            }
            return reset();
        }

        public static String reset() {
            THREAD_LOCAL.set(Id.nextIdStr());
            return THREAD_LOCAL.get();
        }

        public static String reset(String traceId) {
            if (StrUtil.isNotBlank(traceId)) {
                THREAD_LOCAL.set(traceId);
                return traceId;
            }
            return reset();
        }

        public static void set(String traceId) {
            THREAD_LOCAL.set(traceId);
        }

        /**
         * 禁止输出和收集日志
         */
        public static void disable() {
            THREAD_LOCAL.set(disable_value);
        }

        public static boolean isDisable() {
            return disable_value.equals(get());
        }

    }

    /**
     * 枚举编解码器
     */
    public static class Enum {
        private final static com.github.benmanes.caffeine.cache.Cache<Class<?>, Object> _cache = Caffeine.newBuilder().build();

        @SuppressWarnings("unchecked")
        public static <E extends java.lang.Enum<E>> EnumCodec<E> of(Class<E> enumClass) {
            Object codec = _cache.get(enumClass, EnumCodec::of);
            return (EnumCodec<E>) codec;
        }
    }

    /**
     * 标准Json功能
     * 与JsonCodec区别是会生成标准Json字符串,不带@class
     */
    public static class Json {

        /**
         * 获取对象映射器
         *
         * @return {@link ObjectMapper}
         */
        public static ObjectMapper getObjectMapper() {
            return ObjectMapperHolder.get();
        }

        /**
         * 到json字符串
         *
         * @param value 值
         * @return {@link String}
         */
        @SneakyThrows
        public static String toJsonString(Object value) {
            if (value == null) {
                return StrUtil.EMPTY;
            }
            return ObjectMapperHolder.get().writeValueAsString(value);
        }

        /**
         * 到json字节
         */
        @SneakyThrows
        public static byte[] toJsonBytes(Object value) {
            if (value == null) {
                return new byte[0];
            }
            return ObjectMapperHolder.get().writeValueAsBytes(value);
        }

        /**
         * json转object
         */
        @SneakyThrows
        public static Object toObject(String jsonStr) {
            if (StrUtil.isBlank(jsonStr)) {
                return null;
            }
            return ObjectMapperHolder.get().readValue(jsonStr, Object.class);
        }

        /**
         * json转object
         */
        @SneakyThrows
        public static Object toObject(byte[] bytes) {
            if (ArrayUtil.isEmpty(bytes)) {
                return null;
            }
            return ObjectMapperHolder.get().readValue(bytes, Object.class);
        }

        /**
         * json转bean
         */
        @SneakyThrows
        public static <T> T toBean(String jsonStr, Class<T> clazz) {
            if (StrUtil.isBlank(jsonStr)) {
                return null;
            }
            return ObjectMapperHolder.get().readValue(jsonStr, clazz);
        }

        /**
         * json转bean
         */
        @SneakyThrows
        public static <T> T toBean(byte[] bytes, Class<T> clazz) {
            if (ArrayUtil.isEmpty(bytes)) {
                return null;
            }
            return ObjectMapperHolder.get().readValue(bytes, clazz);
        }

        /**
         * json转bean
         */
        @SneakyThrows
        public static <T> T toBean(String jsonStr, TypeReference<T> valueTypeRef) {
            if (StrUtil.isBlank(jsonStr)) {
                return null;
            }
            return ObjectMapperHolder.get().readValue(jsonStr, valueTypeRef);
        }

        /**
         * json转bean
         */
        @SneakyThrows
        public static <T> T toBean(byte[] bytes, TypeReference<T> valueTypeRef) {
            if (ArrayUtil.isEmpty(bytes)) {
                return null;
            }
            return ObjectMapperHolder.get().readValue(bytes, valueTypeRef);
        }

        /**
         * json转map
         */
        @SneakyThrows
        public static <K, V> Map<K, V> toMap(String jsonStr, Class<K> keyClass, Class<V> valueClass) {
            if (StrUtil.isBlank(jsonStr)) {
                return null;
            }
            JavaType type = ObjectMapperHolder.get()
                    .getTypeFactory()
                    .constructParametricType(Map.class, keyClass, valueClass);
            return ObjectMapperHolder.get().readValue(jsonStr, type);
        }

        /**
         * json转map
         */
        @SneakyThrows
        public static <K, V> Map<K, V> toMap(byte[] bytes, Class<K> keyClass, Class<V> valueClass) {
            if (ArrayUtil.isEmpty(bytes)) {
                return new HashMap<>();
            }
            JavaType type = ObjectMapperHolder.get()
                    .getTypeFactory()
                    .constructParametricType(Map.class, keyClass, valueClass);
            return ObjectMapperHolder.get().readValue(bytes, type);
        }

        /**
         * json转List
         */
        @SneakyThrows
        public static <T> List<T> toList(String jsonStr, Class<T> clazz) {
            if (StrUtil.isBlank(jsonStr)) {
                return null;
            }
            JavaType type = ObjectMapperHolder.get()
                    .getTypeFactory()
                    .constructParametricType(List.class, clazz);
            return ObjectMapperHolder.get().readValue(jsonStr, type);
        }

        /**
         * json转List
         */
        @SneakyThrows
        public static <T> List<T> toList(byte[] bytes, Class<T> clazz) {
            if (ArrayUtil.isEmpty(bytes)) {
                return new ArrayList<>();
            }
            JavaType type = ObjectMapperHolder.get()
                    .getTypeFactory()
                    .constructParametricType(List.class, clazz);
            return ObjectMapperHolder.get().readValue(bytes, type);
        }

        /**
         * json转Set
         */
        @SneakyThrows
        public static <T> Set<T> toSet(String jsonStr, Class<T> clazz) {
            if (StrUtil.isBlank(jsonStr)) {
                return new HashSet<>();
            }
            JavaType type = ObjectMapperHolder.get()
                    .getTypeFactory()
                    .constructParametricType(Set.class, clazz);
            return ObjectMapperHolder.get().readValue(jsonStr, type);
        }

        /**
         * json转List
         */
        @SneakyThrows
        public static <T> Set<T> toSet(byte[] bytes, Class<T> clazz) {
            if (ArrayUtil.isEmpty(bytes)) {
                return new HashSet<>();
            }
            JavaType type = ObjectMapperHolder.get()
                    .getTypeFactory()
                    .constructParametricType(Set.class, clazz);
            return ObjectMapperHolder.get().readValue(bytes, type);
        }

    }

    /**
     * 带类型标记的Json功能
     * 与Json区别是会生成带@class的Json字符串,反序列化时会进行自动类型转换
     */
    public static class JsonTyped {

        /**
         * 获取对象映射器
         *
         * @return {@link ObjectMapper}
         */
        public static ObjectMapper getObjectMapper() {
            return ObjectMapperHolder.getTyped();
        }


        /**
         * 到json字符串
         *
         * @param value 值
         * @return {@link String}
         */
        @SneakyThrows
        public static String toJsonString(Object value) {
            if (value == null) {
                return StrUtil.EMPTY;
            }
            return ObjectMapperHolder.getTyped().writeValueAsString(value);
        }

        /**
         * 到json字节
         */
        @SneakyThrows
        public static byte[] toJsonBytes(Object value) {
            if (value == null) {
                return new byte[0];
            }
            return ObjectMapperHolder.getTyped().writeValueAsBytes(value);
        }

        /**
         * json转对象
         */
        @SneakyThrows
        @SuppressWarnings("unchecked")
        public static <T> T toObject(String jsonStr) {
            if (StrUtil.isBlank(jsonStr)) {
                return null;
            }
            return (T) ObjectMapperHolder.getTyped().readValue(jsonStr, Object.class);
        }

        /**
         * json转object
         */
        @SneakyThrows
        @SuppressWarnings("unchecked")
        public static <T> T toObject(byte[] bytes) {
            if (ArrayUtil.isEmpty(bytes)) {
                return null;
            }
            return (T) ObjectMapperHolder.getTyped().readValue(bytes, Object.class);
        }
    }

    /**
     * 权限功能
     */
    public static final class Auth {
        public static final String ROLE_ADMIN = "admin";

        public static void login(String account) {
            login(account, null);
        }

        public static void login(String account, Consumer<SaLoginModel> configurer) {
            SaLoginModel model = SaLoginModel.create()
                    .setDevice(SpringUtil.getApplicationName())
                    .build();
            if (configurer != null) {
                configurer.accept(model);
            }
            StpUtil.login(account, model);
        }

        public static <T extends AuthDetail> void setDetail(T t) {
            mustLogin();
            SaSession session = StpUtil.getSession();
            if (t == null) {
                return;
            }
            session.set(t.getClass().getName(), t);
        }

        @Nonnull
        public static <T extends AuthDetail> T getDetail(@Nonnull Class<T> clazz) {
            T detail = getDetail(clazz, true);
            if (detail == null) {
                throw FrameworkException.of("未获取到用户权限信息,请联系管理员");
            }
            return detail;
        }

        @Nullable
        @SuppressWarnings("unchecked")
        public static <T extends AuthDetail> T getDetail(@Nonnull Class<T> clazz, boolean nullable) {
            mustLogin();
            SaSession session = StpUtil.getSession();
            Object value = session.get(clazz.getName());
            if (value != null) {
                return (T) value;
            }
            if (nullable) {
                return null;
            }
            throw FrameworkException.of("未获取到用户权限信息,请联系管理员");
        }

        @Nonnull
        public static String getAccount() {
            return StpUtil.getLoginId().toString();
        }

        @Nullable
        public static String getAccount(boolean throwable) {
            try {
                return getAccount();
            } catch (Exception exception) {
                if (throwable) {
                    throw exception;
                }
                return null;
            }
        }

        @Nullable
        public static String getAccountOr(String defaultAccount) {
            String account = getAccount(false);
            if (account == null) {
                return defaultAccount;
            }
            return account;
        }

        @Nullable
        public static String getTokenValue() {
            return StpUtil.getTokenValue();
        }

        public static String tryGetTokenValue() {
            try {
                return StpUtil.getTokenValue();
            } catch (Exception ignored) {
                return null;
            }
        }

        public static boolean isLogin() {
            return StpUtil.isLogin();
        }

        public static void mustLogin() {
            String account = getAccount();
            if (StrUtil.isBlank(account)) {
                String loginType = StpUtil.getLoginType();
                throw NotLoginException.newInstance(loginType, NotLoginException.DEFAULT_MESSAGE);
            }
        }

        public static boolean isAdmin() {
            mustLogin();
            List<String> roleList = StpUtil.getRoleList();
            if (CollUtil.isEmpty(roleList)) {
                return false;
            }
            return roleList.contains(ROLE_ADMIN);
        }

        public static String getSameToken() {
            return SaSameUtil.getToken();
        }

        public static boolean isSafeRequest() {
            try {
                SaSameUtil.checkCurrentRequestToken();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 缓存功能
     *
     * @author lizhian
     * @date 2023年11月21日
     */
    public static class Cache {
        /**
         * 新建对象缓存
         */
        public static <T> CacheContainerBuilder<T> newCache(Class<T> clazz) {
            boolean autoType = clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers());
            return newCache(clazz, autoType);
        }

        /**
         * 新建对象缓存
         */
        public static <T> CacheContainerBuilder<T> newCache(Class<T> clazz, boolean typed) {
            return new CacheContainerBuilder<T>()
                    .timeToLiveForever()
                    .disableLocalCache()
                    .nullValueCacheable(false)
                    .codec(new JsonCodec<>(clazz, typed))
                    .mappingIfAbsent(null);
        }

        /**
         * 新建对象缓存
         */
        public static <T> CacheContainerBuilder<T> newCache(TypeReference<T> typeReference) {
            return newCache(typeReference, false);
        }

        /**
         * 新建对象缓存
         */
        public static <T> CacheContainerBuilder<T> newCache(TypeReference<T> typeReference, boolean typed) {
            return new CacheContainerBuilder<T>()
                    .timeToLiveForever()
                    .disableLocalCache()
                    .nullValueCacheable(false)
                    .codec(new JsonCodec<>(typeReference, typed))
                    .mappingIfAbsent(null);
        }

        /**
         * 新建字符串缓存
         */
        public static CacheContainerBuilder<String> newStringCache() {
            return newStringCache(false);
        }

        /**
         * 新建字符串缓存
         */
        public static CacheContainerBuilder<String> newStringCache(boolean gzip) {
            return new CacheContainerBuilder<String>()
                    .timeToLiveForever()
                    .disableLocalCache()
                    .nullValueCacheable(false)
                    .codec(gzip ? StringGzipCodec.UTF_8 : StringCodec.UTF_8)
                    .mappingIfAbsent(null);

        }

        /**
         * 新建字节缓存
         */
        public static CacheContainerBuilder<byte[]> newBytesCache() {
            return new CacheContainerBuilder<byte[]>()
                    .timeToLiveForever()
                    .disableLocalCache()
                    .nullValueCacheable(false)
                    .codec(BytesCodec.INSTANCE)
                    .mappingIfAbsent(null);
        }

    }

    /**
     * 消息队列
     */
    public static class Message {


        /**
         * 新建消息容器
         */
        public static <T> MessageContainerBuilder<T> newMessage(Class<T> clazz) {
            boolean typed = clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers());
            return newMessage(clazz, typed);
        }

        /**
         * 新建消息容器
         */
        public static <T> MessageContainerBuilder<T> newMessage(Class<T> clazz, boolean typed) {
            return new MessageContainerBuilder<T>()
                    .codec(new JsonCodec<>(clazz, typed))
                    .useRedis()
                    .asQueue()
                    ;
        }

        /**
         * 新建消息容器
         */
        public static <T> MessageContainerBuilder<T> newMessage(TypeReference<T> typeReference) {
            return newMessage(typeReference, false);
        }

        /**
         * 新建消息容器
         */
        public static <T> MessageContainerBuilder<T> newMessage(TypeReference<T> typeReference, boolean typed) {
            return new MessageContainerBuilder<T>()
                    .codec(new JsonCodec<>(typeReference, typed))
                    .useRedis()
                    .asQueue()
                    ;
        }

        /**
         * 新建消息容器
         */
        public static MessageContainerBuilder<String> newStringMessage() {
            return newStringMessage(false);
        }

        /**
         * 新建消息容器
         */
        public static MessageContainerBuilder<String> newStringMessage(boolean gzip) {
            return new MessageContainerBuilder<String>()
                    .codec(gzip ? StringGzipCodec.UTF_8 : StringCodec.UTF_8)
                    .useRedis()
                    .asQueue()
                    ;

        }

        /**
         * 新建消息容器
         */
        public static MessageContainerBuilder<byte[]> newBytesMessage() {
            return new MessageContainerBuilder<byte[]>()
                    .codec(BytesCodec.INSTANCE)
                    .useRedis()
                    .asQueue()
                    ;
        }

        public static <T extends MessageBean> T build(Class<T> interfaces) {
            MessageProdukerInvocationHandler handler = new MessageProdukerInvocationHandler(interfaces);
            T instance = ProxyUtil.newProxyInstance(handler, interfaces);
            log.info("创建消息生产者: {}", interfaces.getName());
            return instance;
        }
    }

    /**
     * 任务调度
     */
    public static class Job {
    }

    /**
     * 广播事件
     */
    public static class Event {

    }

    /**
     * 远程服务调用
     */
    public static class Rpc {

    }

    /**
     * 全局配置
     */
    public static class Configuration {

    }


    /**
     * 服务发现
     */
    public static class Discovery {

    }
}
