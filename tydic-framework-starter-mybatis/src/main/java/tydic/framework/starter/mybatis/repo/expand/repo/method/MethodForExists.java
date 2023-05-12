package tydic.framework.starter.mybatis.repo.expand.repo.method;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import tydic.framework.core.proxy.TypedSelf;
import tydic.framework.starter.mybatis.repo.BaseRepo;
import tydic.framework.starter.mybatis.repo.expand.lambda.method.ColumnMethod;

import java.io.Serializable;


/*
 * 查询单条数据
 */
public interface MethodForExists<T, SELF extends BaseRepo<T>> extends TypedSelf<SELF>, ColumnMethod<T> {


    /**
     * 根据主键判断
     */
    default boolean existsById(Serializable id) {
        SFunction<T, ?> tableIdColumn = this.getTableIdColumnFor("existsById");
        if (StrUtil.isBlankIfStr(id)) {
            return false;
        }
        return this.self()
                .newQuery()
                .eq(tableIdColumn, id)
                .exists();
    }

    /**
     * 根据编码判断
     */
    default boolean existsByCode(String code) {
        SFunction<T, ?> tableColeColumn = this.getTableColeColumnFor("existsByCode");
        if (StrUtil.isBlank(code)) {
            return false;
        }
        return this.self()
                .newQuery()
                .eq(tableColeColumn, code)
                .exists();
    }

    /**
     * 根据字段判断
     */
    default <V> boolean existsBy(SFunction<T, V> field, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.self()
                .newQuery()
                .eq(field, value)
                .exists();
    }
}
