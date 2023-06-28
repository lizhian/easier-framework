package easier.framework.core.util;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class TableUtil {
    public static String fieldToColumn(Class<?> clazz, Field field) {
        if (clazz == null || field == null) {
            return null;
        }
        return fieldToColumn(clazz, field.getName());
    }

    public static String fieldToColumn(Class<?> clazz, String fieldName) {
        if (clazz == null || StrUtil.isBlank(fieldName)) {
            return null;
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        if (tableInfo == null) {
            return null;
        }
        String keyProperty = tableInfo.getKeyProperty();
        if (StrUtil.equals(keyProperty, fieldName)) {
            return tableInfo.getKeyColumn();
        }
        return tableInfo.getFieldList()
                        .stream()
                        .filter(tableFieldInfo -> tableFieldInfo.getField().getName().equals(fieldName))
                        .map(TableFieldInfo::getColumn)
                        .findAny()
                        .orElse(null);
    }
}
