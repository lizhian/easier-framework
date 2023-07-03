# 1 复制`LambdaUpdateChainWrapper`类

# 2 继承接口增加拓展方法

```
        , WhenMethod<T, LambdaUpdate<T>>
        , UpdateMethod<T, LambdaUpdate<T>>
```

# 3 重写`columnToString`方法

```
        super.wrapperChildren = new LambdaQueryWrapper<T>(entityClass) {
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
```

# 4 增加关联更新功能

```
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
```

# 4 禁用实体更新功能

```
    @Override
    public boolean update(T entity) {
        throw FrameworkException.of("【LambdaUpdate】不允许使用【update(T entity)】方法");
    }
```

# 6 完整类

```java
public final class LambdaUpdate<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaUpdate<T>, LambdaUpdateWrapper<T>>
        implements ChainUpdate<T>, Update<LambdaUpdate<T>, SFunction<T, ?>>
        , WhenMethod<T, LambdaUpdate<T>>
        , UpdateMethod<T, LambdaUpdate<T>> {
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
        wrapperChildren.set(condition, column, val, mapping);
        return typedThis;
    }

    @Override
    public LambdaUpdate<T> setSql(boolean condition, String sql) {
        wrapperChildren.setSql(condition, sql);
        return typedThis;
    }

    @Override
    public String getSqlSet() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getSqlSet");
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
}
```