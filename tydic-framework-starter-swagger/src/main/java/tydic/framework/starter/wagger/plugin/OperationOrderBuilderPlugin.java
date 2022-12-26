package tydic.framework.starter.wagger.plugin;

import com.github.xiaoymin.knife4j.spring.extension.ApiOrderExtension;
import com.github.xiaoymin.knife4j.spring.plugin.AbstractOperationBuilderPlugin;
import org.springframework.core.annotation.Order;
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
        int order = switch (context.httpMethod()) {
            case GET -> 1;
            case POST -> 2;
            case PUT -> 3;
            case DELETE -> 4;
            default -> 5;
        };
        List<VendorExtension> vendorExtensions = new ArrayList<>();
        vendorExtensions.add(new ApiOrderExtension(order));
        context.operationBuilder().extensions(vendorExtensions);
    }


}
