package easier.framework.starter.doc;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import easier.framework.starter.doc.customizer.EasierOperationCustomizer;
import easier.framework.starter.doc.customizer.EasierPropertyParameterCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Slf4j
@EnableKnife4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({
        EasierPropertyParameterCustomizer.class
        , EasierOperationCustomizer.class
})
public class EasierDocAutoConfiguration {


}
