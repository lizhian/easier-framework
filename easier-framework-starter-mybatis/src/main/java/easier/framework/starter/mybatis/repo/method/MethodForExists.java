package easier.framework.starter.mybatis.repo.method;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.starter.mybatis.repo.IRepo;

import java.io.Serializable;


/*
 * 查询单条数据
 */
public interface MethodForExists<T> extends IRepo<T> {

    /**
     * 根据主键判断
     */
    default boolean existsById(Serializable id) {
        SFunction<T, ?> tableId = this.repo().getTableId();
        if (StrUtil.isBlankIfStr(id)) {
            return false;
        }
        return this.repo()
                .newQuery()
                .eq(tableId, id)
                .exists();
    }

    /**
     * 根据编码判断
     */
    default boolean existsByCode(String code) {
        SFunction<T, ?> tableCole = this.repo().getTableCole();
        if (StrUtil.isBlank(code)) {
            return false;
        }
        return this.repo()
                .newQuery()
                .eq(tableCole, code)
                .exists();
    }

    /**
     * 根据字段判断
     */
    default <V> boolean existsBy(SFunction<T, V> field, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.repo()
                .newQuery()
                .eq(field, value)
                .exists();
    }
}
