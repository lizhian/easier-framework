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
    @SuppressWarnings("unchecked")
    default BindHolder<T> withBind(SFunction<T, Object>... fields) {
        return new BindHolder<>(this.repo(), CollUtil.newArrayList(fields));
    }
}
