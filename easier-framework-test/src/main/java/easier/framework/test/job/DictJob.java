package easier.framework.test.job;

import com.tangzc.mpe.bind.Binder;
import easier.framework.core.plugin.cache.CacheBuilder;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.test.cache.DictCache;
import easier.framework.test.eo.SysDict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DictJob {
    private final Repo<SysDict> _sys_dict = Repos.of(SysDict.class);
    private final DictCache dictCache = CacheBuilder.build(DictCache.class);


    // @LoopJob(delay = 60, timeUnit = TimeUnit.SECONDS)
    @Scheduled(fixedDelay = 10000)
    public void updateDictCache() {
        //        List<SysDict> dictList = this._sys_dict.withBind().listAll();
        List<SysDict> dictList = this._sys_dict.listAll();
        Binder.bind(dictList);
        List<DictDetail> details = dictList.stream()
                .map(SysDict::toDictDetail)
                .collect(Collectors.toList());
        for (DictDetail detail : details) {
            String dictCode = detail.getCode();
            this.dictCache.updateDictDetail(dictCode, detail);
        }
        log.info("已更新字典缓存:{}", details.size());
    }
}
