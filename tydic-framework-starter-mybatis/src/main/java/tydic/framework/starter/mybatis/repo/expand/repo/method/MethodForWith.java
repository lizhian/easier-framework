package tydic.framework.starter.mybatis.repo.expand.repo.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import tydic.framework.core.domain.TreeBuilder;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.starter.mybatis.repo.BaseRepo;
import tydic.framework.starter.mybatis.repo.expand.repo.holder.*;


/*
 * 查询单条数据
 */
public interface MethodForWith<T, SELF extends BaseRepo<T>> extends TypedSelf<SELF> {
    /**
     * 绑定注入字段
     */
    default BindHolder<T> withBind(SFunction<T, Object>... bindFields) {
        return new BindHolder<>(this.self(), CollUtil.newArrayList(bindFields));
    }

    /**
     * 绑定关联删除的类
     */
    default RelatedDeleteHolder<T> withRelateDelete(Class<?>... classes) {
        return new RelatedDeleteHolder<>(this.self(), CollUtil.newArrayList(classes));
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
