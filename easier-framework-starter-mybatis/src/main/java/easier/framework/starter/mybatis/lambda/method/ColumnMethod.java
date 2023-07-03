package easier.framework.starter.mybatis.lambda.method;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.core.plugin.mybatis.TableCode;
import easier.framework.starter.mybatis.lambda.ColumnSFunction;

public interface ColumnMethod<T> {
    Class<T> getEntityClass();

    default SFunction<T, ?> getTableIdColumn() {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        if (tableInfo != null) {
            return new ColumnSFunction<>(tableInfo.getKeyColumn(), tableInfo.getKeySqlSelect());
        }
        String methodName = ThreadUtil.getStackTraceElement(3).getMethodName();
        throw new MybatisPlusException(this.getEntityClass().getSimpleName() + "未配置[@TableCode]字段,不可使用[" + methodName + "]方法");
    }

    default SFunction<T, ?> getTableColeColumn() {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        TableFieldInfo tableFieldInfo = tableInfo.getFieldList()
                .stream()
                .filter(it -> it.getField().getAnnotation(TableCode.class) != null)
                .findAny()
                .orElse(null);
        if (tableFieldInfo != null) {
            return new ColumnSFunction<>(tableFieldInfo.getColumn(), tableFieldInfo.getSqlSelect());
        }
        String methodName = ThreadUtil.getStackTraceElement(3).getMethodName();
        throw new MybatisPlusException(this.getEntityClass().getSimpleName() + "未配置[@TableCode]字段,不可使用[" + methodName + "]方法");
    }
}
