package tydic.framework.starter.mybatis.repo.expand.repo.method;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.starter.mybatis.repo.Repo;
import tydic.framework.starter.mybatis.repo.expand.lambda.method.ColumnMethod;

import javax.annotation.Nullable;
import java.io.Serializable;


/*
 * 查询单条数据
 */
public interface MethodForGet<T> extends TypedSelf<Repo<T>>, ColumnMethod<T> {
    /**
     * 根据主键查询
     */
    @Nullable
    default T getById(Serializable id) {
        if (StrUtil.isBlankIfStr(id)) {
            return null;
        }
        return this.self().getBaseMapper().selectById(id);
    }

    /**
     * 根据编码查询
     */
    @Nullable
    default T getByCode(String code) {
        SFunction<T, ?> tableColeColumn = this.getTableColeColumn();
        if (StrUtil.isBlank(code)) {
            return null;
        }
        return this.self()
                .newQuery()
                .eq(tableColeColumn, code)
                .any();
    }

    /**
     * 根据字段查询单条数据
     */
    @Nullable
    default <V> T anyBy(SFunction<T, V> column, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return null;
        }
        return this.self()
                .newQuery()
                .eq(column, value)
                .any();
    }

}
