package easier.framework.test.job;

import easier.framework.core.plugin.cache.CacheBuilder;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.core.plugin.job.LoopJob;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.cache.DictCache;
import easier.framework.test.eo.Dict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 字典作业
 *
 * @author lizhian
 * @date 2023年07月12日
 */
@Slf4j
@Component
public class DictJob {
    private final Repo<Dict> _sys_dict = Repos.of(Dict.class);
    private final DictCache dictCache = CacheBuilder.build(DictCache.class);


    @LoopJob(delay = 60, timeUnit = TimeUnit.SECONDS)
    public void updateDictCache() {
        List<Dict> dictList = this._sys_dict.withBind().listAll();
        List<DictDetail> details = dictList.stream()
                .map(Dict::toDictDetail)
                .collect(Collectors.toList());
        for (DictDetail detail : details) {
            String dictCode = detail.getCode();
            this.dictCache.updateDictDetail(dictCode, detail);
        }
        log.info("已更新字典缓存:{}", details.size());
    }
}
