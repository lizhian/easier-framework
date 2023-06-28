package easier.framework.starter.innerRequest.template;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.easier.framework.starter.innerRequest.model.InnerRequestBody;
import com.easier.framework.starter.innerRequest.model.InnerRequestResult;
import easier.framework.core.plugin.innerRequest.ServiceDetail;
import easier.framework.core.plugin.innerRequest.enums.ServiceType;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.innerRequest.util.DiscoveryUtil;
import org.springframework.cloud.client.ServiceInstance;

public interface InnerRequestClientTemplate {

    /**
     * 获取真实请求地址
     */
    default String getServiceURL(ServiceDetail serviceDetail) {
        ServiceType type = serviceDetail.type();
        String serviceId = serviceDetail.serviceId();
        boolean https = serviceDetail.https();
        String path = serviceDetail.path();
        String url = formatURL(type, serviceId, path);
        if (url.startsWith("http")) {
            return url;
        }
        if (https) {
            return "https://" + url;
        } else {
            return "http://" + url;
        }
    }

    default String formatURL(ServiceType type, String serviceId, String path) {
        if (ServiceType.host.equals(type)) {
            return serviceId + path;
        }
        if (ServiceType.discovery.equals(type)) {
            return this.getUrlByDiscovery(serviceId) + path;

        }
        if (ServiceType.property.equals(type)) {
            return SpringUtil.getProperty(serviceId) + path;
        }
        return null;
    }


    default String getUrlByDiscovery(String serviceId) {
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
