package easier.framework.starter.mybatis.lambda.update;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.ChainUpdate;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import easier.framework.core.plugin.exception.biz.FrameworkException;
import easier.framework.starter.mybatis.lambda.ColumnSFunction;
import easier.framework.starter.mybatis.lambda.method.UpdateMethod;
import easier.framework.starter.mybatis.lambda.method.WhenMethod;
import easier.framework.starter.mybatis.repo.IRepo;
import easier.framework.starter.mybatis.repo.Repo;
import easier.framework.starter.mybatis.repo.Repos;
import easier.framework.starter.mybatis.util.MybatisPlusUtil;

import java.util.Map;

public final class LambdaUpdate<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaUpdate<T>, LambdaUpdateWrapper<T>>
        implements ChainUpdate<T>, Update<LambdaUpdate<T>, SFunction<T, ?>>
        , WhenMethod<T, LambdaUpdate<T>>
        , UpdateMethod<T>
        , IRepo<T> {
    private final BaseMapper<T> baseMapper;

    public LambdaUpdate(BaseMapper<T> baseMapper, Class<T> entityClass) {
        super();
        this.baseMapper = baseMapper;
        super.wrapperChildren = new LambdaUpdateWrapper<T>(entityClass) {
            @Override
            protected String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
                if (column instanceof ColumnSFunction) {
                    ColumnSFunction columnSFunction = (ColumnSFunction) column;
                    ColumnCache cache = columnSFunction.toColumnCache();
                    return onlyColumn ? cache.getColumn() : cache.getColumnSelect();
                }
                return super.columnToString(column, onlyColumn);
            }
        };
    }

    @Override
    public LambdaUpdate<T> set(boolean condition, SFunction<T, ?> column, Object val, String mapping) {
        this.wrapperChildren.set(condition, column, val, mapping);
        return this.typedThis;
    }

    @Override
    public LambdaUpdate<T> setSql(boolean condition, String sql) {
        this.wrapperChildren.setSql(condition, sql);
        return this.typedThis;
    }

    @Override
    public String getSqlSet() {
        return super.getSqlSet();
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return this.baseMapper;
    }

    @Override
    public Class<T> getEntityClass() {
        return super.wrapperChildren.getEntityClass();
    }

    private boolean relatedUpdate;

    public LambdaUpdate<T> relatedUpdate() {
        this.relatedUpdate = true;
        return this;
    }

    @Override
    public boolean update() {
        Map<SFunction, Object> updateSets = MybatisPlusUtil.tryUpdateSets(this);
        boolean update = this.execute(mapper -> SqlHelper.retBool(mapper.update(null, this.getWrapper())));
        if (update && this.relatedUpdate) {
            MybatisPlusUtil.relatedUpdate(this, updateSets);
        }
        return update;
    }


    @Override
    public boolean update(T entity) {
        throw FrameworkException.of("【LambdaUpdate】不允许使用【update(T entity)】方法");
    }

    @Override
    public boolean remove() {
        TableInfo tableInfo = this.repo().getTableInfo();
        if (tableInfo.isWithLogicDelete()) {
            MybatisPlusUtil.tryUpdateSets(this);
            String logicDeleteSql = tableInfo.getLogicDeleteSql(false, false);
            return this.self()
                    .setSql(logicDeleteSql)
                    .update();
        }
        return this.execute(mapper -> SqlHelper.retBool(mapper.delete(this.getWrapper())));
    }

    @Override
    public Repo<T> repo() {
        return Repos.of(this.getEntityClass());
    }
}


