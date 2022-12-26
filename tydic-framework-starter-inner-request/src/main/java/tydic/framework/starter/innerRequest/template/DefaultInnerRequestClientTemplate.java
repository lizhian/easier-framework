package tydic.framework.starter.innerRequest.template;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.tydic.framework.starter.innerRequest.model.InnerRequestBody;
import com.tydic.framework.starter.innerRequest.model.InnerRequestResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import tydic.framework.core.plugin.innerRequest.InnerRequestException;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.core.util.TraceIdUtil;

import java.util.List;

@RequiredArgsConstructor
public class DefaultInnerRequestClientTemplate implements InnerRequestClientTemplate {


    @Override
    public void beforeRequest(HttpRequest httpRequest, InnerRequestBody innerRequestBody) {
        //添加IdToken,用于校验是否为内部请求
        httpRequest.header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());

        //如果有token,传递tokenValue,相当于传递认证信息
        String tokenValue = this.tryGetTokenValue();
        if (StrUtil.isNotBlank(tokenValue)) {
            String key = SaManager.getConfig().getTokenName();
            httpRequest.header(key, tokenValue);
        }

        //设置追踪码
        String traceId = TraceIdUtil.getOrCreate();
        httpRequest.header(TraceIdUtil.traceId, traceId);
    }

    private String tryGetTokenValue() {
        try {
            return StpUtil.getTokenValue();
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public void afterRequest(HttpResponse httpResponse, InnerRequestResult innerRequestResult) {

    }

    @Override
    @SneakyThrows
    public void afterException(Exception e) {
        if (e instanceof InnerRequestException) {
            throw e;
        }
        throw new InnerRequestException("内部请求异常", e);
    }

    private List<WeightRandom.WeightObj<String>> loadAddresses(String serviceId) {
        DiscoveryClient discoveryClient = SpringUtil.getAndCache(DiscoveryClient.class);
        //返回地址以及对应权重
        return discoveryClient.getInstances(serviceId)
                              .stream()
                              .map(instance -> {
                                  String address = instance.getHost() + ":" + instance.getPort();
                                  String weight = instance.getMetadata().getOrDefault("weight", "1");
                                  return new WeightRandom.WeightObj<>(address, Double.parseDouble(weight));
                              })
                              .toList();
    }

}
