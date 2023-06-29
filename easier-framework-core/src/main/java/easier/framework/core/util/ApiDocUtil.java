package easier.framework.core.util;

import cn.hutool.core.collection.CollUtil;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.ColumnComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

@UtilityClass
public class ApiDocUtil {

    private final static List<String> defaultMessages = CollUtil.newArrayList(
            "不能为空"
            , "个数必须在"
    );

    public String getDescription(Field field) {
        if (field == null) {
            return StrUtil.EMPTY;
        }
        return getDescription(field.getAnnotations());
    }

    public static String getDescription(Annotation[] annotations) {
        if (annotations == null) {
            return StrUtil.EMPTY;
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof Schema) {
                Schema schema = (Schema) annotation;
                String description = schema.description();
                if (StrUtil.isNotBlank(description)) {
                    return description;
                }
            }
            if (annotation instanceof ColumnComment) {
                ColumnComment columnComment = (ColumnComment) annotation;
                String comment = columnComment.value();
                if (StrUtil.isNotBlank(comment)) {
                    return comment;
                }
            }
            if (annotation instanceof Column) {
                Column column = (Column) annotation;
                String comment = column.comment();
                if (StrUtil.isNotBlank(comment)) {
                    return comment;
                }
            }
        }
        return StrUtil.EMPTY;
    }

    public static String mergeMessage(String description, String message) {
        if (StrUtil.isBlank(description) || StrUtil.isBlank(message)) {
            return message;
        }
        if (message.contains("{}")) {
            return StrUtil.format(message, description);
        }
        if (defaultMessages.stream().anyMatch(message::startsWith)) {
            return description + message;
        }
        return message;
    }

    public static String converterMessage(String message) {
        if (StrUtil.isBlank(message)) {
            return StrUtil.EMPTY;
        }
        message = message.replaceAll("null", "空");
        return message;
    }
}
