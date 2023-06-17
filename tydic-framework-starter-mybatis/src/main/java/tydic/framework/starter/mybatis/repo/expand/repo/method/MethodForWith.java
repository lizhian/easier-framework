package tydic.framework.starter.mybatis.repo.expand.repo.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import tydic.framework.core.domain.TreeBuilder;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.starter.mybatis.repo.Repo;
import tydic.framework.starter.mybatis.repo.expand.repo.holder.BindHolder;
import tydic.framework.starter.mybatis.repo.expand.repo.holder.KeyHolder;
import tydic.framework.starter.mybatis.repo.expand.repo.holder.PairHolder;
import tydic.framework.starter.mybatis.repo.expand.repo.holder.TreeHolder;


/*
 * 查询单条数据
 */
public interface MethodForWith<T> extends TypedSelf<Repo<T>> {
    /**
     * 绑定注入字段
     */
    default BindHolder<T> withBind(SFunction<T, Object>... bindFields) {
        return new BindHolder<>(this.self(), CollUtil.newArrayList(bindFields));
    }

    default TreeHolder<T> withTreeNode(TreeBuilder<T> treeBuilder) {
        return new TreeHolder<>(this.self(), treeBuilder)
                .withChildren(true)
                .childrenNullToEmpty(false);
    }

    default <K> KeyHolder<T, K> withKey(SFunction<T, K> keyColumn) {
        return new KeyHolder<>(this.self(), keyColumn);
    }

    default <K, V> PairHolder<T, K, V> withPair(SFunction<T, K> keyColumn, SFunction<T, V> valueColumn) {
        return new PairHolder<>(this.self(), keyColumn, valueColumn);
    }

}
