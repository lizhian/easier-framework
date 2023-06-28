package easier.framework.starter.mybatis.template;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.tangzc.mpe.annotation.handler.IOptionByAutoFillHandler;

import java.lang.reflect.Field;

public interface EasierMybatisTemplate extends TableNameHandler, IOptionByAutoFillHandler<String>, IdentifierGenerator {
    /**
     * 当前操作者
     */
    String currentHandler();

    @Override
    default String getVal(Object object, Class<?> clazz, Field field) {
        return this.currentHandler();
    }
}
