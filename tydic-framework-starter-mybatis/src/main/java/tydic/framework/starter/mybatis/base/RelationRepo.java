package tydic.framework.starter.mybatis.base;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import tydic.framework.starter.mybatis.base.holder.RelationHolder;

/**
 * 关联实体仓库
 * 提供基础的CURD功能
 */
public abstract class RelationRepo<T> extends SuperRepo<T> {

    public <K, V> RelationHolder<T, K, V> forRelation(SFunction<T, K> keyColumn, SFunction<T, V> valueColumn) {
        return new RelationHolder<>(this, keyColumn, valueColumn);
    }
}
