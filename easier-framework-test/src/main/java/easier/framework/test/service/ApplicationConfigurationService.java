package easier.framework.test.service;

import easier.framework.core.util.ExtensionCore;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.eo.ApplicationConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 应用配置服务
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Service
@RequiredArgsConstructor
@ExtensionMethod(ExtensionCore.class)
public class ApplicationConfigurationService {
    private final Repo<ApplicationConfiguration> _application_configuration = Repos.of(ApplicationConfiguration.class);

    public Map<String, Map<String, List<ApplicationConfiguration>>> all() {
        Map<String, Map<String, List<ApplicationConfiguration>>> result = new HashMap<>();
        Map<String, List<ApplicationConfiguration>> profileMap = this._application_configuration
                .newQuery()
                .eq(ApplicationConfiguration::getHistory, false)
                .orderByDesc(ApplicationConfiguration::getUpdateTime)
                .stream()
                .collect(Collectors.groupingBy(ApplicationConfiguration::getConfigurationProfile));
        for (String profile : profileMap.keySet()) {
            List<ApplicationConfiguration> profileList = profileMap.get(profile);
            Map<String, List<ApplicationConfiguration>> groupMap = profileList
                    .stream()
                    .collect(Collectors.groupingBy(ApplicationConfiguration::getConfigurationGroup));
            result.put(profile, groupMap);
        }
        return result;
    }
}
