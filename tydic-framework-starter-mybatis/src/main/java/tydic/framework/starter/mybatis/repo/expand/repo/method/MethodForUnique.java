package tydic.framework.starter.mybatis.repo.expand.repo.method;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.starter.mybatis.repo.Repo;

import java.io.Serializable;


/*
 * 查询单条数据
 */
public interface MethodForUnique<T> extends TypedSelf<Repo<T>> {


    /**
     * 根据字段是否唯一
     * <br/>
     * 用于新增的时候检验
     */
    default <V> boolean isUnique(SFunction<T, V> field, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.self()
                .newQuery()
                .eq(field, value)
                .count() < 1;
    }

    /**
     * 根据字段是否不是唯一
     * <br/>
     * 用于新增的时候检验
     */
    default <V> boolean isNotUnique(SFunction<T, V> field, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.self()
                .newQuery()
                .eq(field, value)
                .count() > 0;
    }

    /**
     * 判断字段值是否唯一
     * <br/>
     * 用于修改的时候检验
     * <br/>
     * 与旧数据作对比
     */
    default <V> boolean isUnique(SFunction<T, V> field, V value, Serializable id) {
        if (StrUtil.isBlankIfStr(id) || StrUtil.isBlankIfStr(value)) {
            return false;
        }
        if (this.isUnique(field, value)) {
            return true;
        }
        T t = this.self().getById(id);
        return t != null && value.equals(field.apply(t));
    }

    /**
     * 判断字段值是否不是唯一
     * <br/>
     * 用于修改的时候检验
     * <br/>
     * 与旧数据作对比
     */
    default <V> boolean isNotUnique(SFunction<T, V> field, V value, String id) {
        if (StrUtil.isBlank(id) || StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return !this.isUnique(field, value, id);
    }
}
