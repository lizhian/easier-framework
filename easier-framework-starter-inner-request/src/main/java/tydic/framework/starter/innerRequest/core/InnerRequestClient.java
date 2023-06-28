package easier.framework.starter.innerRequest.core;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.easier.framework.starter.innerRequest.model.InnerRequestBody;
import com.easier.framework.starter.innerRequest.model.InnerRequestMethodDetail;
import com.easier.framework.starter.innerRequest.model.InnerRequestResult;
import easier.framework.core.plugin.innerRequest.InnerRequest;
import easier.framework.core.plugin.innerRequest.InnerRequestBuilder;
import easier.framework.core.util.HessianUtil;
import easier.framework.core.util.InstanceUtil;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.cache.builder.CacheMethodDetail;
import easier.framework.starter.innerRequest.InnerRequestProperties;
import easier.framework.starter.innerRequest.template.InnerRequestClientTemplate;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;

/**
 * 内部请求客户端
 */
public class InnerRequestClient implements InnerRequestBuilder.Invoker {

    private final InnerRequestClientTemplate template;
    private final InnerRequestProperties properties;

    public InnerRequestClient(InnerRequestClientTemplate template, InnerRequestProperties properties) {
        this.template = template;
        this.properties = properties;
    }


    @Override
    @SneakyThrows
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            InnerRequestMethodDetail methodDetail = InstanceUtil.in(InnerRequestClient.class)
                                                                .getInstance(method, InnerRequestMethodDetail::new);

            //先从缓存获取
            Object invokeFromCache = this.invokeFromCache(args, methodDetail);
            if (invokeFromCache != null) {
                return invokeFromCache;
            }
            //从本地Spring容器获取
            InnerRequest bean = SpringUtil.getBeanByClassName(InnerRequest.class, methodDetail.getClassName());
            if (bean != null) {
                Object result = method.invoke(bean, args);
                this.cacheInvokeValue(result, args, methodDetail.getCacheMethodDetail());
                return result;
            }
            //发起远程访问
            //构建请求体
            InnerRequestBody innerRequestBody = InnerRequestBody.builder()
                                                                .className(methodDetail.getClassName())
                                                                .methodName(methodDetail.getMethodName())
                                                                .args(args)
                                                                .build();
            byte[] body = HessianUtil.serialize(innerRequestBody);

            //获取请求地址
            String serviceURL = this.template.getServiceURL(methodDetail.getServiceDetail());

            HttpRequest httpRequest = HttpUtil.createPost(serviceURL)
                                              .body(body)
                                              .contentType(ContentType.JSON.getValue())
                                              .setConnectionTimeout(this.properties.getConnectionTimeout())
                                              .setReadTimeout(this.properties.getReadTimeout())
                                              .disableCache()
                                              .disableCookie();

            //前置处理
            this.template.beforeRequest(httpRequest, innerRequestBody);
            //发起请求
            @Cleanup HttpResponse httpResponse = httpRequest.execute();
            //转换
            byte[] httpResponseBodyBytes = httpResponse.bodyBytes();
            InnerRequestResult innerRequestResult = HessianUtil.deserialize(httpResponseBodyBytes);
            //后置处理
            this.template.afterRequest(httpResponse, innerRequestResult);
            //返回处理
            Object result = innerRequestResult.getResult();
            this.cacheInvokeValue(result, args, methodDetail.getCacheMethodDetail());
            return result;
        } catch (Exception e) {
            if (this.template != null) {
                this.template.afterException(e);
            }
            return null;
        }
    }

    private void cacheInvokeValue(Object result, Object[] args, CacheMethodDetail methodDetail) {
        //空值不缓存
        if (result == null) {
            return;
        }
        if (!methodDetail.isGet()) {
            return;
        }
        RedissonClient redissonClient = methodDetail.getRedissonClient();
        String cacheKey = methodDetail.parseCacheKey(args);
        RBucket<Object> bucket = redissonClient.getBucket(cacheKey);
        long timeToLive = methodDetail.getTimeToLive(args);
        if (timeToLive > 0) {
            bucket.setAsync(result, timeToLive, methodDetail.getTimeUnit());
        } else {
            bucket.setAsync(result);
        }
    }


    public Object invokeFromCache(Object[] args, InnerRequestMethodDetail methodDetail) {
        CacheMethodDetail cacheMethodDetail = methodDetail.getCacheMethodDetail();
        if (!cacheMethodDetail.isGet()) {
            return null;
        }
        RedissonClient redissonClient = cacheMethodDetail.getRedissonClient();
        String cacheKey = cacheMethodDetail.parseCacheKey(args);
        return redissonClient.getBucket(cacheKey).get();
    }
}
