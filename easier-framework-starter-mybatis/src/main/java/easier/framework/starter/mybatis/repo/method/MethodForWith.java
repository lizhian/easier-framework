package easier.framework.starter.mybatis.repo.method;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.starter.mybatis.repo.IRepo;
import easier.framework.starter.mybatis.repo.holder.BindHolder;
import easier.framework.starter.mybatis.repo.holder.KeyHolder;
import easier.framework.starter.mybatis.repo.holder.PairHolder;


/*
 * 查询单条数据
 */
public interface MethodForWith<T> extends IRepo<T> {

    default <K> KeyHolder<T, K> withKey(SFunction<T, K> keyColumn) {
        return new KeyHolder<>(this.repo(), keyColumn);
    }

    default <K, V> PairHolder<T, K, V> withPair(SFunction<T, K> keyColumn, SFunction<T, V> valueColumn) {
        return new PairHolder<>(this.repo(), keyColumn, valueColumn);
    }

    /**
     * 绑定注入字段
     */

    default BindHolder<T> withBind() {
        return new BindHolder<>(this.repo(), CollUtil.newArrayList());
    }

    default BindHolder<T> withBind(SFunction<T, Object> field1) {
        return new BindHolder<>(this.repo(), CollUtil.newArrayList(field1));
    }

    default BindHolder<T> withBind(SFunction<T, Object> field1, SFunction<T, Object> field2) {
        return new BindHolder<>(this.repo(), CollUtil.newArrayList(field1, field2));
    }

    default BindHolder<T> withBind(SFunction<T, Object> field1, SFunction<T, Object> field2, SFunction<T, Object> field3) {
        return new BindHolder<>(this.repo(), CollUtil.newArrayList(field1, field2, field3));
    }

    default BindHolder<T> withBind(SFunction<T, Object> field1, SFunction<T, Object> field2, SFunction<T, Object> field3, SFunction<T, Object> field4) {
        return new BindHolder<>(this.repo(), CollUtil.newArrayList(field1, field2, field3, field4));
    }

    default BindHolder<T> withBind(SFunction<T, Object> field1, SFunction<T, Object> field2, SFunction<T, Object> field3, SFunction<T, Object> field4, SFunction<T, Object> field5) {
        return new BindHolder<>(this.repo(), CollUtil.newArrayList(field1, field2, field3, field4, field5));
    }
}
