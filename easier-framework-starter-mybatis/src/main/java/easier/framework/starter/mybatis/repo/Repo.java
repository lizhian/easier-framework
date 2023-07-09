package easier.framework.starter.mybatis.repo;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.core.plugin.mybatis.TableCode;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.mybatis.lambda.ColumnSFunction;
import easier.framework.starter.mybatis.repo.method.*;
import lombok.Getter;

import java.io.Serializable;

/**
 * 实体仓库
 * 提供完整的CURD功能
 */
public final class Repo<T> implements
        MethodForAdd<T>,
        MethodForCount<T>,
        MethodForDelete<T>,
        MethodForExists<T>,
        MethodForGet<T>,
        MethodForLambda<T>,
        MethodForList<T>,
        MethodForUnique<T>,
        MethodForUpdate<T>,
        MethodForWith<T> {

    @Getter
    private final Class<T> entityClass;
    @Getter
    private final int defaultBatchSize;


    public Repo(Class<T> entityClass) {
        this(entityClass, 1000);
    }

    public Repo(Class<T> entityClass, int defaultBatchSize) {
        this.entityClass = entityClass;
        this.defaultBatchSize = defaultBatchSize;
    }


    @Override
    public Repo<T> repo() {
        return this;
    }


    public TableInfo getTableInfo() {
        return TableInfoHelper.getTableInfo(this.entityClass);
    }

    @SuppressWarnings("unchecked")
    public Class<BaseMapper<T>> getMapperClass() {
        TableInfo tableInfo = this.getTableInfo();
        return (Class<BaseMapper<T>>) ClassUtils.toClassConfident(tableInfo.getCurrentNamespace());
    }

    public BaseMapper<T> getBaseMapper() {
        return SpringUtil.getAndCache(this.getMapperClass());
    }

    public String getSqlStatement(SqlMethod sqlMethod) {
        Class<BaseMapper<T>> mapperClass = this.getMapperClass();
        return SqlHelper.getSqlStatement(mapperClass, sqlMethod);
    }

    public SFunction<T, Serializable> getTableId() {
        TableInfo tableInfo = this.getTableInfo();
        if (tableInfo != null) {
            return new ColumnSFunction<>(tableInfo.getKeyColumn(), tableInfo.getKeySqlSelect());
        }
        throw FrameworkException.of("{}未配置[@TableId]字段", this.entityClass.getSimpleName());
    }

    public SFunction<T, String> getTableCole() {
        TableInfo tableInfo = this.getTableInfo();
        TableFieldInfo tableFieldInfo = tableInfo.getFieldList()
                .stream()
                .filter(it -> it.getField().getAnnotation(TableCode.class) != null)
                .findAny()
                .orElse(null);
        if (tableFieldInfo != null) {
            return new ColumnSFunction<>(tableFieldInfo.getColumn(), tableFieldInfo.getSqlSelect());
        }
        throw FrameworkException.of("{}未配置[@TableCode]字段", this.entityClass.getSimpleName());
    }
}
