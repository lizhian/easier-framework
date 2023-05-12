package tydic.framework.core.plugin.mybatis;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.Map;

/**
 * 实体数据操作定义
 */
public interface MybatisPlusEntity {

    /**
     * 数据新增前操作
     */
    void preInsert();

    /**
     * 数据更新前操作
     */
    void preUpdate();

    /**
     * lambda更新前操作
     */
    void preLambdaUpdate(Map<SFunction, Object> updateSets);

}
