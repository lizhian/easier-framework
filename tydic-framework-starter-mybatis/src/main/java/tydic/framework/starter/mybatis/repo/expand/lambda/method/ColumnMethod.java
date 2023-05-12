package tydic.framework.starter.mybatis.repo.expand.lambda.method;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import tydic.framework.core.plugin.mybatis.TableCode;
import tydic.framework.core.util.StrUtil;
import tydic.framework.starter.mybatis.repo.ColumnSFunction;

public interface ColumnMethod<T> {
    Class<T> getEntityClass();

    default SFunction<T, ?> getTableColeColumn() {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        TableFieldInfo tableFieldInfo = tableInfo.getFieldList()
                .stream()
                .filter(it -> it.getField().getAnnotation(TableCode.class) != null)
                .findAny()
                .orElse(null);
        if (tableFieldInfo == null) {
            return null;
        }
        return new ColumnSFunction<>(tableFieldInfo.getColumn(), tableFieldInfo.getSqlSelect());
    }

    default SFunction<T, ?> getTableIdColumn() {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        if (tableInfo == null) {
            return null;
        }
        String keyColumn = tableInfo.getKeyColumn();
        String keySqlSelect = tableInfo.getKeySqlSelect();
        if (StrUtil.isBlank(keyColumn) || StrUtil.isBlank(keySqlSelect)) {
            return null;
        }
        return new ColumnSFunction<>(keyColumn, keySqlSelect);
    }

    default SFunction<T, ?> getTableColeColumnFor(String methodName) {
        SFunction<T, ?> tableColeColumn = this.getTableColeColumn();
        if (tableColeColumn != null) {
            return tableColeColumn;
        }
        throw new MybatisPlusException(this.getEntityClass().getSimpleName() + "未配置[@TableCode]字段,不可使用[" + methodName + "]方法");
    }

    default SFunction<T, ?> getTableIdColumnFor(String methodName) {
        SFunction<T, ?> tableIdColumn = this.getTableIdColumn();
        if (tableIdColumn != null) {
            return tableIdColumn;
        }
        throw new MybatisPlusException(this.getEntityClass().getSimpleName() + "未配置[@TableId]字段,不可使用[" + methodName + "]方法");
    }

}
