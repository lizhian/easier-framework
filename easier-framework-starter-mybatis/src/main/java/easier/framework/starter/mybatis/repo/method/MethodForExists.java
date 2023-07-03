package easier.framework.starter.mybatis.repo.method;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.core.proxy.TypedSelf;
import easier.framework.starter.mybatis.lambda.method.ColumnMethod;
import easier.framework.starter.mybatis.repo.Repo;

import java.io.Serializable;


/*
 * 查询单条数据
 */
public interface MethodForExists<T> extends TypedSelf<Repo<T>>, ColumnMethod<T> {


    /**
     * 根据主键判断
     */
    default boolean existsById(Serializable id) {
        SFunction<T, ?> tableIdColumn = this.getTableIdColumn();
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
        SFunction<T, ?> tableColeColumn = this.getTableColeColumn();
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
