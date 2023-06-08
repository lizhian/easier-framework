package tydic.framework.core.util;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import tydic.framework.core.domain.Update;
import tydic.framework.core.domain.ValidErrorDetail;
import tydic.framework.core.plugin.exception.biz.BizException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: tgd
 * @Date: 2022/5/23 5:03 PM
 */
@Slf4j
public class ValidUtil {

    private static final Validator validator;
    private final static List<String> defaultMessages = CollUtil.newArrayList(
            "不能为空"
            , "个数必须在"
    );

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    public static void validOnUpdate(Object object) {
        ValidUtil.valid(object, Update.class, Default.class);
    }

    /**
     * 校验参数
     *
     * @param object
     */
    public static void valid(Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> errors = validator.validate(object, groups);
        if (CollUtil.isEmpty(errors)) {
            return;
        }
        List<ValidErrorDetail> details = errors.stream()
                .map(ValidUtil::formatMessage)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(details)) {
            return;
        }
        String mergeMessage = details.stream()
                .map(ValidErrorDetail::getMergeMessage)
                .collect(Collectors.joining(StrPool.COMMA));
        BizException bizException = BizException.of(mergeMessage);
        bizException.setExpandData(details);
        throw bizException;
    }

    private static ValidErrorDetail formatMessage(ConstraintViolation<Object> error) {
        String message = error.getMessage();
        message = message.replaceAll("null", "空");
        ValidErrorDetail.ValidErrorDetailBuilder builder = ValidErrorDetail.builder()
                .message(message)
                .mergeMessage(message);
        if (error.getPropertyPath() instanceof PathImpl) {
            PathImpl path = (PathImpl) error.getPropertyPath();
            Class<?> beanClass = error.getLeafBean().getClass();
            String fieldName = path.getLeafNode().getName();
            builder.fieldName(fieldName);
            Field field = ReflectUtil.getField(beanClass, fieldName);
            ApiModelProperty apiModelProperty = AnnotationUtil.getAnnotation(field, ApiModelProperty.class);
            if (apiModelProperty != null) {
                String fieldLabel = apiModelProperty.value();
                String mergeMessage = mergeMessage(fieldLabel, message);
                builder.fieldLabel(fieldLabel)
                        .mergeMessage(mergeMessage);
            }
        }
        return builder.build();
    }

    private static String mergeMessage(String fieldLabel, String message) {
        if (StrUtil.isBlank(fieldLabel) || StrUtil.isBlank(message)) {
            return message;
        }
        if (message.contains("{}")) {
            return StrUtil.format(message, fieldLabel);
        }
        if (defaultMessages.stream().anyMatch(message::startsWith)) {
            return fieldLabel + message;
        }
        return message;
    }


    public static void mustTrue(Boolean bool, String message) {
        if (Boolean.TRUE.equals(bool)) {
            return;
        }
        throw BizException.of(message);
    }

    //value不能为空字符
    public static void notBlank(String value, String message) {
        if (StrUtil.isBlank(value)) {
            throw BizException.of(message);
        }
    }

    //value长度必须为length
    public static void strLength(String value, int length, String message) {
        if (StrUtil.isNotBlank(value) && value.length() == length) {
            return;
        }
        throw BizException.of(message);
    }

    //value必须为数字类型
    public static void isNumber(String value, String message) {
        if (NumberUtil.isNumber(value)) {
            return;
        }
        throw BizException.of(message);
    }

    //value不能为空
    public static void notNull(Object value, String message) {
        if (value == null) {
            throw BizException.of(message);
        }
        if (value instanceof String && StrUtil.isBlank((String) value)) {
            throw BizException.of(message);
        }
        if (value instanceof Collection<?> && CollUtil.isEmpty((Collection<?>) value)) {
            throw BizException.of(message);
        }
    }

    // value必须为空
    public static void isNull(Object value, String message) {
        if (value != null) {
            throw BizException.of(message);
        }
    }

    //value等于target  就抛出异常
    public static void notEquals(Object value, Object target, String message) {
        if (ObjectUtil.equals(value, target)) {
            throw BizException.of(message);
        }
    }

    public static void equals(Object value, Object target, String message) {
        if (ObjectUtil.equals(value, target)) {
            return;
        }
        throw BizException.of(message);

    }

    // 字符长度必须在length之内
    public static void strBeyondLength(String value, int length, String message) {
        if (StrUtil.isNotBlank(value) && value.length() < length) {
            return;
        }
        throw BizException.of(message);
    }

    public static void notContains(String value, String target, String message) {
        if (StrUtil.isBlank(value) || StrUtil.isBlank(target)) {
            return;
        }
        if (value.contains(target)) {
            throw BizException.of(message);
        }
    }


    public static void mustFalse(Boolean bool, String message) {
        if (Boolean.FALSE.equals(bool)) {
            return;
        }
        throw BizException.of(message);
    }


    //6-20位包含大小写字母,数字和特殊字符
    public static void password(String password, String message) {
        String xxStr = "abcdefghijklmnopqrstuvwxyz";
        String dxStr = xxStr.toUpperCase();
        String szStr = "1234567890";
        String tsStr = "!@#$%^&*()_+-=";
        if (StrUtil.isBlank(password)) {
            throw BizException.of(message);
        }
        String[] strings = password.split("");
        if (strings.length < 6 || strings.length > 20) {
            throw BizException.of(message);
        }
        boolean xx = false, dx = false, sz = false, ts = false;
        for (String aChar : strings) {
            if (xxStr.contains(aChar)) {
                xx = true;
                continue;
            }
            if (dxStr.contains(aChar)) {
                dx = true;
                continue;
            }
            if (szStr.contains(aChar)) {
                sz = true;
                continue;
            }
            if (tsStr.contains(aChar)) {
                ts = true;
                continue;
            }
            throw BizException.of(message);
        }
        if (xx && dx && sz && ts) {
            return;
        }
        throw BizException.of(message);
    }
}
