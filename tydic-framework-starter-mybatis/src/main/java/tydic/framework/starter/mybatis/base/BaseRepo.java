package tydic.framework.starter.mybatis.base;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.tangzc.mpe.base.event.EntityUpdateEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import tydic.framework.core.domain.TreeBuilder;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.starter.mybatis.base.entity.EntityConfigurer;
import tydic.framework.starter.mybatis.base.holder.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 实体仓库
 * 提供完整的CURD功能
 */
@Slf4j
public abstract class BaseRepo<T> extends SuperRepo<T> {

    /**
     * 实体配置
     */
    @Getter
    private final EntityConfigurer<T> entityConfigurer = this.initEntityConfigurer();


    private EntityConfigurer<T> initEntityConfigurer() {
        EntityConfigurer<T> configurer = new EntityConfigurer<>();
        this.init(configurer);
        configurer.finish();
        return configurer;
    }

    /**
     * 构建实体配置
     */
    protected abstract void init(EntityConfigurer<T> configurer);

    /**
     * 绑定注入字段
     */
    @SafeVarargs
    public final BindHolder<T> withBind(SFunction<T, ?>... bindFields) {
        return new BindHolder<>(this, CollUtil.newArrayList(bindFields));
    }

    /**
     * 绑定关联删除的类
     */
    @SafeVarargs
    public final RelatedDeleteHolder<T> withRelateDelete(Class<?>... classes) {
        return new RelatedDeleteHolder<>(this, CollUtil.newArrayList(classes));
    }

    public final TreeNodeHolder<T> withTreeNode() {
        TreeBuilder<T> treeBuilder = this.getEntityConfigurer().getTreeBuilder();
        if (treeBuilder == null) {
            throw new MybatisPlusException("[treeBuilder]未配置,不可使用[withTreeNode]方法");
        }
        return new TreeNodeHolder<>(this, treeBuilder)
                .withChildren(true)
                .childrenNullToEmpty(false);
    }

    public final <K> KeyHolder<T, K> withKey(SFunction<T, K> keyColumn) {
        return new KeyHolder<>(this, keyColumn);
    }

    public final <K, V> PairHolder<T, K, V> withPair(SFunction<T, K> keyColumn, SFunction<T, V> valueColumn) {
        return new PairHolder<>(this, keyColumn, valueColumn);
    }

    /*
     * ┏━━━━━━━━━━━━━━━━━━━┓
     * ┃ select one method ┃
     * ┗━━━━━━━━━━━━━━━━━━━┛
     */

    /**
     * 根据主键查询
     */
    @Nullable
    public T getById(Serializable id) {
        if (StrUtil.isBlankIfStr(id)) {
            return null;
        }
        return this.getMapper().selectById(id);
    }

    /**
     * 根据编码查询
     */
    @Nullable
    public T getByCode(String code) {
        SFunction<T, String> codeColumn = this.getEntityConfigurer().getCodeColumn();
        if (codeColumn == null) {
            throw new MybatisPlusException("[codeColumn]未配置,不可使用[getByCode]方法");
        }
        if (StrUtil.isBlank(code)) {
            return null;
        }
        return this.newQuery()
                   .anyBy(codeColumn, code);
    }

    /**
     * 根据字段查询单条数据
     */
    @Nullable
    public <V> T anyBy(SFunction<T, V> column, V value) {
        if (column == null) {
            throw new MybatisPlusException("[column]未配置,不可使用[anyBy]方法");
        }
        if (value == null) {
            return null;
        }
        if (StrUtil.isBlankIfStr(value)) {
            return null;
        }
        return this.newQuery()
                   .anyBy(column, value);
    }

    /*
     * ┏━━━━━━━━━━━━━━━━━━━━┓
     * ┃ select list method ┃
     * ┗━━━━━━━━━━━━━━━━━━━━┛
     */

    /**
     * 查询全表
     */
    @Override
    @Nonnull
    public final List<T> all() {
        return this.getEntityConfigurer()
                   .wrapOrderBy(this.newQuery())
                   .list();
    }

    /**
     * 根据主键查询列表
     */
    @Nonnull
    public List<T> getByIds(Collection<String> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        List<T> list = this.getMapper().selectBatchIds(ids);
        return list == null ? new ArrayList<>() : list;
    }

    /**
     * 根据编码查询列表
     */
    @Nonnull
    public List<T> getByCodes(Collection<String> codes) {
        SFunction<T, String> codeColumn = this.getEntityConfigurer().getCodeColumn();
        if (codeColumn == null) {
            throw new MybatisPlusException("[codeColumn]未配置,不可使用[getByCodes]方法");
        }
        if (CollUtil.isEmpty(codes)) {
            return new ArrayList<>();
        }
        return this.getEntityConfigurer()
                   .wrapOrderBy(this.newQuery())
                   .in(codeColumn, codes)
                   .list();
    }

    /**
     * 根据字段查询列表
     */
    @Nonnull
    public <V> List<T> getBy(SFunction<T, V> column, V value) {
        if (column == null) {
            throw new MybatisPlusException("[column]未配置,不可使用[getBy]方法");
        }
        if (StrUtil.isBlankIfStr(value)) {
            return new ArrayList<>();
        }
        return this.getEntityConfigurer()
                   .wrapOrderBy(this.newQuery())
                   .eq(column, value)
                   .list();
    }

    /**
     * 根据字段查询列表
     */
    @Nonnull
    public <V> List<T> getBy(SFunction<T, V> column, Collection<V> values) {
        if (column == null) {
            throw new MybatisPlusException("[column]未配置,不可使用[getBy]方法");
        }
        if (CollUtil.isEmpty(values)) {
            return new ArrayList<>();
        }
        return this.getEntityConfigurer()
                   .wrapOrderBy(this.newQuery())
                   .in(column, values)
                   .list();
    }

    /*
     * ┏━━━━━━━━━━━━━━━┓
     * ┃ exists method ┃
     * ┗━━━━━━━━━━━━━━━┛
     */

    /**
     * 根据主键判断
     */
    public boolean existsById(String id) {
        if (StrUtil.isBlank(id)) {
            return false;
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        return ChainWrappers.queryChain(this.getMapper())
                            .eq(tableInfo.getKeyProperty(), id)
                            .exists();
    }

    /**
     * 根据编码判断
     */
    public boolean existsByCode(String code) {
        SFunction<T, String> codeColumn = this.getEntityConfigurer().getCodeColumn();
        if (codeColumn == null) {
            throw new MybatisPlusException("[codeColumn]未配置,不可使用[existsByCode]方法");
        }
        if (StrUtil.isBlank(code)) {
            return false;
        }
        return this.newQuery()
                   .eq(codeColumn, code)
                   .exists();
    }

    /**
     * 根据字段判断
     */
    public <V> boolean existsBy(SFunction<T, V> field, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.newQuery()
                   .eq(field, value)
                   .exists();
    }

    /*
     * ┏━━━━━━━━━━━━━━━┓
     * ┃ unique method ┃
     * ┗━━━━━━━━━━━━━━━┛
     */

    /**
     * 根据字段是否唯一
     * <br/>
     * 用于新增的时候检验
     */
    public <V> boolean isUnique(SFunction<T, V> field, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.newQuery()
                   .eq(field, value)
                   .count() < 1;
    }

    /**
     * 根据字段是否不是唯一
     * <br/>
     * 用于新增的时候检验
     */
    public <V> boolean isNotUnique(SFunction<T, V> field, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.newQuery()
                   .eq(field, value)
                   .count() > 0;
    }

    /**
     * 判断字段值是否唯一
     * <br/>
     * 用于修改的时候检验
     * <br/>
     * 与旧数据作对比
     */
    public <V> boolean isUnique(SFunction<T, V> field, V value, String id) {
        if (StrUtil.isBlank(id) || StrUtil.isBlankIfStr(value)) {
            return false;
        }
        if (this.isUnique(field, value)) {
            return true;
        }
        T t = this.getById(id);
        return t != null && value.equals(field.apply(t));
    }

    /**
     * 判断字段值是否不是唯一
     * <br/>
     * 用于修改的时候检验
     * <br/>
     * 与旧数据作对比
     */
    public <V> boolean isNotUnique(SFunction<T, V> field, V value, String id) {
        if (StrUtil.isBlank(id) || StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return !this.isUnique(field, value, id);
    }

    /*
     * ┏━━━━━━━━━━━━━┓
     * ┃ tree method ┃
     * ┗━━━━━━━━━━━━━┛
     */

    @Nonnull
    public List<T> getChildren(String key) {
        TreeBuilder<T> treeBuilder = this.getEntityConfigurer().getTreeBuilder();
        if (treeBuilder == null) {
            throw new MybatisPlusException("[treeBuilder]未配置,不可使用[getChildren]方法");
        }
        if (StrUtil.isBlank(key)) {
            return new ArrayList<>();
        }
        SFunction<T, String> parentKeyFunction = treeBuilder.getParentKeyFunction();
        SFunction<T, Number> sortFunction = treeBuilder.getSortFunction();
        return this.newQuery()
                   .eq(parentKeyFunction, key)
                   .orderByAsc(treeBuilder.hasSortColumn(), sortFunction)
                   .list();
    }

    public boolean hasChildren(String key) {
        TreeBuilder<T> treeBuilder = this.getEntityConfigurer().getTreeBuilder();
        if (treeBuilder == null) {
            throw new MybatisPlusException("[treeBuilder]未配置,不可使用[hasChildren]方法");
        }
        if (StrUtil.isBlank(key)) {
            return false;
        }
        SFunction<T, String> parentKeyFunction = treeBuilder.getParentKeyFunction();
        return this.newQuery()
                   .eq(parentKeyFunction, key)
                   .exists();
    }

    /*
     * ┏━━━━━━━━━━━━━━━┓
     * ┃ update method ┃
     * ┗━━━━━━━━━━━━━━━┛
     */

    /**
     * 修改
     */
    public boolean update(T entity) {
        if (entity == null) {
            return false;
        }
        boolean result = SqlHelper.retBool(this.getMapper().updateById(entity));
        if (result) {
            SpringUtil.publishEvent(EntityUpdateEvent.create(entity));
        }
        return result;
    }

    /**
     * 批量修改
     */
    public boolean updateBatch(Collection<T> entityList) {
        return this.updateBatch(entityList, this.getDefaultBatchSize());
    }

    /**
     * 批量修改
     */
    public boolean updateBatch(Collection<T> entityList, int batchSize) {
        if (CollUtil.isEmpty(entityList)) {
            return false;
        }
        String sqlStatement = SqlHelper.getSqlStatement(this.getMapperClass(), SqlMethod.UPDATE_BY_ID);
        boolean result = this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
        });
        if (result) {
            entityList.stream()
                      .map(EntityUpdateEvent::create)
                      .forEach(SpringUtil::publishEvent);
        }
        return result;
    }

    /*
     * ┏━━━━━━━━━━━━━━━┓
     * ┃ delete method ┃
     * ┗━━━━━━━━━━━━━━━┛
     */

    /**
     * 根据主键删除
     */
    public boolean deleteById(String id) {
        if (StrUtil.isBlank(id)) {
            return false;
        }
        return SqlHelper.retBool(this.getMapper().deleteById(id));
    }

    /**
     * 根据主键批量删除
     */
    public boolean deleteByIds(Collection<String> ids) {
        return this.deleteByIds(ids, this.getDefaultBatchSize());
    }

    /**
     * 根据主键批量删除
     */
    public boolean deleteByIds(Collection<String> ids, int batchSize) {
        if (CollUtil.isEmpty(ids)) {
            return true;
        }
        String sqlStatement = SqlHelper.getSqlStatement(this.getMapperClass(), SqlMethod.DELETE_BY_ID);
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        return this.executeBatch(ids, batchSize, (sqlSession, e) -> {
            if (tableInfo.isWithLogicDelete()) {
                if (this.getEntityClass().isAssignableFrom(e.getClass())) {
                    sqlSession.update(sqlStatement, e);
                } else {
                    T instance = tableInfo.newInstance();
                    tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), e);
                    sqlSession.update(sqlStatement, instance);
                }
            } else {
                sqlSession.update(sqlStatement, e);
            }
        });
    }

    /**
     * 根据编码删除
     */
    public boolean deleteByCode(String code) {
        SFunction<T, String> codeColumn = this.getEntityConfigurer().getCodeColumn();
        if (codeColumn == null) {
            throw new MybatisPlusException("[codeColumn]为空,不可使用[deleteByCode]方法");
        }
        if (StrUtil.isBlank(code)) {
            return false;
        }
        return this.newUpdate()
                   .eq(codeColumn, code)
                   .remove();
    }

    /**
     * 根据编码批量删除
     */
    public boolean deleteByCodes(Collection<String> codes) {
        return this.deleteByCodes(codes, this.getDefaultBatchSize());
    }

    /**
     * 根据编码批量删除
     */
    public boolean deleteByCodes(Collection<String> codes, int batchSize) {
        SFunction<T, String> codeColumn = this.getEntityConfigurer().getCodeColumn();
        if (codeColumn == null) {
            throw new MybatisPlusException("[codeColumn]为空,不可使用[realDeleteByCode]方法");
        }
        if (CollUtil.isEmpty(codes)) {
            return false;
        }
        for (List<String> temp : CollUtil.split(codes, batchSize)) {
            this.newUpdate()
                .in(codeColumn, temp)
                .remove();
        }
        return true;
    }

    /**
     * 根据字段删除
     */
    public <V> boolean deleteBy(SFunction<T, V> column, V value) {
        if (StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.newUpdate()
                   .eq(column, value)
                   .remove();
    }

    /**
     * 根据字段批量删除
     */
    public <V> boolean deleteBy(SFunction<T, V> column, Collection<V> values) {
        return this.deleteBy(column, values, this.getDefaultBatchSize());
    }

    /**
     * 根据字段批量删除
     */
    public <V> boolean deleteBy(SFunction<T, V> column, Collection<V> values, int batchSize) {
        if (CollUtil.isEmpty(values)) {
            return false;
        }
        for (List<V> temp : CollUtil.split(values, batchSize)) {
            this.newUpdate()
                .in(column, temp)
                .remove();
        }
        return true;
    }
}
