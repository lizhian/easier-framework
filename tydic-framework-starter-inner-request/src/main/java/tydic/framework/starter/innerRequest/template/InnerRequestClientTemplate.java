package tydic.framework.starter.innerRequest.template;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.tydic.framework.starter.innerRequest.model.InnerRequestBody;
import com.tydic.framework.starter.innerRequest.model.InnerRequestResult;
import org.springframework.cloud.client.ServiceInstance;
import tydic.framework.core.plugin.innerRequest.ServiceDetail;
import tydic.framework.core.plugin.innerRequest.enums.ServiceType;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.starter.innerRequest.util.DiscoveryUtil;

public interface InnerRequestClientTemplate {

    /**
     * 获取真实请求地址
     */
    default String getServiceURL(ServiceDetail serviceDetail) {
        ServiceType type = serviceDetail.type();
        String serviceId = serviceDetail.serviceId();
        boolean https = serviceDetail.https();
        String path = serviceDetail.path();
        String url = switch (type) {
            case host -> serviceId;
            case discovery -> this.getUrlByDiscovery(serviceId);
            case property -> SpringUtil.getProperty(serviceId);
        } + path;
        if (url.startsWith("http")) {
            return url;
        }
        if (https) {
            return "https://" + url;
        } else {
            return "http://" + url;
        }
    }

    private String getUrlByDiscovery(String serviceId) {
        ServiceInstance instance = DiscoveryUtil.getInstance(serviceId);
        return instance.getUri().toString();
    }

    /**
     * 前置处理
     */
    void beforeRequest(HttpRequest httpRequest, InnerRequestBody innerRequestBody);

    /**
     * 后置处理
     */
    void afterRequest(HttpResponse httpResponse, InnerRequestResult innerRequestResult);

    /**
     * 异常处理
     */
    void afterException(Exception e);

}
