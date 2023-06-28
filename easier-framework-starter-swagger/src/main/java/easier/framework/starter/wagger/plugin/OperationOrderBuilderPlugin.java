package easier.framework.starter.wagger.plugin;

import com.github.xiaoymin.knife4j.spring.extension.ApiOrderExtension;
import com.github.xiaoymin.knife4j.spring.plugin.AbstractOperationBuilderPlugin;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.OperationContext;

import java.util.ArrayList;
import java.util.List;

@Order
public class OperationOrderBuilderPlugin extends AbstractOperationBuilderPlugin {
    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(OperationContext context) {
        int order = 5;
        HttpMethod httpMethod = context.httpMethod();
        if (HttpMethod.GET.equals(httpMethod)) {
            order = 1;
        }
        if (HttpMethod.POST.equals(httpMethod)) {
            order = 2;
        }
        if (HttpMethod.PUT.equals(httpMethod)) {
            order = 3;
        }
        if (HttpMethod.DELETE.equals(httpMethod)) {
            order = 4;
        }
        List<VendorExtension> vendorExtensions = new ArrayList<>();
        vendorExtensions.add(new ApiOrderExtension(order));
        context.operationBuilder().extensions(vendorExtensions);
    }


}
