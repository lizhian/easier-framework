package easier.framework.core.plugin.validation;

import cn.hutool.core.util.ReflectUtil;
import easier.framework.core.util.ApiDocUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import java.lang.reflect.Field;

/**
 * 字段校验错误详情
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class ValidErrorDetail {
    private String property;
    private String description;
    private String message;
    private String mergeMessage;

    public static ValidErrorDetail from(ConstraintViolation<Object> error) {
        String message = ApiDocUtil.converterMessage(error.getMessage());
        ValidErrorDetail.ValidErrorDetailBuilder builder = ValidErrorDetail.builder()
                .message(message)
                .mergeMessage(message);
        if (error.getPropertyPath() instanceof PathImpl) {
            PathImpl path = (PathImpl) error.getPropertyPath();
            Class<?> beanClass = error.getLeafBean().getClass();
            String property = path.getLeafNode().getName();
            Field field = ReflectUtil.getField(beanClass, property);
            String description = ApiDocUtil.getDescription(field);
            String mergeMessage = ApiDocUtil.mergeMessage(description, message);
            builder.property(property)
                    .description(description)
                    .message(message)
                    .mergeMessage(mergeMessage);

        }
        return builder.build();
    }

    /**
     * 从ObjectError对象中获取ValidErrorDetail对象
     *
     * @param error ObjectError对象
     * @return ValidErrorDetail对象
     */
    @SuppressWarnings("unchecked")
    public static ValidErrorDetail from(ObjectError error) {
        Object violation = ReflectUtil.getFieldValue(error, "violation");
        if (violation instanceof ConstraintViolation) {
            return ValidErrorDetail.from((ConstraintViolation<Object>) violation);
        }
        String message = ApiDocUtil.converterMessage(error.getDefaultMessage());
        ;
        return ValidErrorDetail.builder()
                .message(message)
                .mergeMessage(message)
                .build();
    }

}
