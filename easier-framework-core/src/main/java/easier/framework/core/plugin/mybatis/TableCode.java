package easier.framework.core.plugin.mybatis;


import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 编码字段标识
 * 作用在 xxxByCode 方法上
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
@Constraint(validatedBy = TableCode.Validator.class)
public @interface TableCode {
    String regex() default "";


    /**
     * 特殊字符
     *
     * @return {@link String}
     */
    String specialCharacters() default "_";

    /**
     * 使用大写
     *
     * @return boolean
     */
    boolean useUpperCase() default false;

    /**
     * 最小
     *
     * @return int
     */
    int min() default 3;

    /**
     * 最大
     *
     * @return int
     */
    int max() default 100;


    Class<? extends Payload>[] payload() default {};

    String message() default "";

    Class<?>[] groups() default {};

    class Validator implements ConstraintValidator<TableCode, String> {
        private int min;

        private int max;
        private String regex;
        private String message;

        @Override
        public void initialize(TableCode tableCode) {
            this.min = tableCode.min();
            this.max = tableCode.max();
            this.regex = tableCode.regex();
            this.message = tableCode.message();
            if (StrUtil.isBlank(this.regex)) {
                String specialCharacters = tableCode.specialCharacters();
                this.regex = StrUtil.format("^[a-z{}][a-z{}0-9{}]*$"
                        , tableCode.useUpperCase() ? "A-Z" : ""
                        , tableCode.useUpperCase() ? "A-Z" : ""
                        , specialCharacters
                );
                this.message = StrUtil.format("必须以{}开头,且只能由{}、数字{}组成,长度在{}和{}之间"
                        , tableCode.useUpperCase() ? "字母" : "小写字母"
                        , tableCode.useUpperCase() ? "字母" : "小写字母"
                        , StrUtil.isBlank(specialCharacters) ? "" : "和特殊字符`" + specialCharacters + "`组成"
                        , this.min, this.max
                );
            }

        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (StrUtil.isBlank(value)) {
                return true;
            }
            int length = value.length();
            if (ReUtil.isMatch(this.regex, value) && length >= this.min && length <= this.max) {
                return true;
            }
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(this.message)
                    .addConstraintViolation();
            return false;
        }
    }


}
