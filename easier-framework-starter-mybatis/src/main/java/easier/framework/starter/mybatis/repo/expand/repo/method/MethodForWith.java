package easier.framework.starter.mybatis.repo.expand.repo.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.core.domain.TreeBuilder;
import easier.framework.core.proxy.TypedSelf;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.expand.repo.holder.BindHolder;
import easier.framework.starter.mybatis.repo.expand.repo.holder.KeyHolder;
import easier.framework.starter.mybatis.repo.expand.repo.holder.PairHolder;
import easier.framework.starter.mybatis.repo.expand.repo.holder.TreeHolder;


/*
 * 查询单条数据
 */
public interface MethodForWith<T> extends TypedSelf<Repo<T>> {
    /**
     * 绑定注入字段
     */

    default BindHolder<T> withBind() {
        return new BindHolder<>(this.self(), CollUtil.newArrayList());
    }

    default BindHolder<T> withBind(SFunction<T, Object> bindField1) {
        return new BindHolder<>(this.self(), CollUtil.newArrayList(bindField1));
    }

    default BindHolder<T> withBind(SFunction<T, Object> field1, SFunction<T, Object> field2) {
        return new BindHolder<>(this.self(), CollUtil.newArrayList(field1, field2));
    }

    default BindHolder<T> withBind(SFunction<T, Object> field1, SFunction<T, Object> field2, SFunction<T, Object> field3) {
        return new BindHolder<>(this.self(), CollUtil.newArrayList(field1, field2, field3));
    }

    default BindHolder<T> withBind(SFunction<T, Object> field1, SFunction<T, Object> field2, SFunction<T, Object> field3, SFunction<T, Object> field4) {
        return new BindHolder<>(this.self(), CollUtil.newArrayList(field1, field2, field3, field4));
    }

    default BindHolder<T> withBind(SFunction<T, Object> field1, SFunction<T, Object> field2, SFunction<T, Object> field3, SFunction<T, Object> field4, SFunction<T, Object> field5) {
        return new BindHolder<>(this.self(), CollUtil.newArrayList(field1, field2, field3, field4, field5));
    }


    default TreeHolder<T> withTreeNode(TreeBuilder<T> treeBuilder) {
        return new TreeHolder<>(this.self(), treeBuilder);
        //                .withChildren(true)
        //                .childrenNullToEmpty(false);
    }

    default <K> KeyHolder<T, K> withKey(SFunction<T, K> keyColumn) {
        return new KeyHolder<>(this.self(), keyColumn);
    }

    default <K, V> PairHolder<T, K, V> withPair(SFunction<T, K> keyColumn, SFunction<T, V> valueColumn) {
        return new PairHolder<>(this.self(), keyColumn, valueColumn);
    }

}
