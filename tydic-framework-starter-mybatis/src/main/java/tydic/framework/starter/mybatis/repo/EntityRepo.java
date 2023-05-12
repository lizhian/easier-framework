package tydic.framework.starter.mybatis.repo;

import com.github.yulichang.base.MPJBaseMapper;

public final class EntityRepo<T> extends BaseRepo<T> {

    public EntityRepo(MPJBaseMapper<T> baseMapper) {
        super(baseMapper);
    }
}
