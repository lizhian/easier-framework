package easier.framework.test.controller.sidecar;


import easier.framework.core.domain.R;
import easier.framework.test.eo.ApplicationConfiguration;
import easier.framework.test.service.ApplicationConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "应用配置管理")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfigurationController {
    private final ApplicationConfigurationService applicationConfigurationService;

    @Operation(summary = "配置")
    @GetMapping("/application/configuration/all")
    public R<Map<String, Map<String, List<ApplicationConfiguration>>>> all() {
        Map<String, Map<String, List<ApplicationConfiguration>>> all = this.applicationConfigurationService.all();
        return R.success(all);
    }
}
