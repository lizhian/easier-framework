# 1 复制`LambdaUpdateChainWrapper`类

# 2 继承接口增加拓展方法

```
        , LambdaExpandWhenMethod<T, LambdaUpdate<T>>
        , LambdaExpandUpdateMethod<T, LambdaUpdate<T>>
```

# 3 增加关联更新功能

```
    private boolean relatedUpdate;

    public final LambdaUpdate<T> relatedUpdate() {
        this.relatedUpdate = true;
        return this;
    }

    @Override
    public boolean update() {
        Map<SFunction, Object> updateSets = MybatisPlusUtil.tryUpdateSets(this);
        int update = this.getBaseMapper().update(null, this.getWrapper());
        if (update > 0 && this.relatedUpdate) {
            MybatisPlusUtil.relatedUpdate(this, updateSets);
        }
        return SqlHelper.retBool(update);
    }
```

# 4 禁用实体更新功能

```
    @Override
    public boolean update(T entity) {
        throw new MybatisPlusException("【LambdaUpdate】不允许使用【update(T entity)】方法");
    }
```

# 5 增加关联删除功能

```
    private boolean relatedDelete;

    private List<Class<?>> relatedDeleteEntityClasses;

    public final LambdaUpdate<T> relatedDelete(Class<?>... classes) {
        this.relatedDelete = true;
        this.relatedDeleteEntityClasses = CollUtil.newArrayList(classes);
        return this;
    }

    @Override
    public boolean remove() {
        MybatisPlusUtil.tryUpdateSets(this);
        List<T> list = null;
        if (this.relatedDelete) {
            list = this.getBaseMapper().selectList(this.getWrapper());
        }
        int delete = this.getBaseMapper().delete(this.getWrapper());
        if (delete > 0 && this.relatedDelete) {
            MybatisPlusUtil.relatedDelete(list, this.getEntityClass(), this.relatedDeleteEntityClasses);
        }
        return SqlHelper.retBool(delete);
    }
```

# 6 完整类

```java
public class LambdaUpdate<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaUpdate<T>, LambdaUpdateWrapper<T>>
        implements ChainUpdate<T>, Update<LambdaUpdate<T>, SFunction<T, ?>>
        , LambdaExpandWhenMethod<T, LambdaUpdate<T>> {

    private final BaseMapper<T> baseMapper;
    @Getter
    private final Class<T> entityClass;

    public LambdaUpdate(BaseMapper<T> baseMapper) {
        super();
        Class<?> clazz = ReflectionKit.getSuperClassGenericType(baseMapper.getClass(), BaseMapper.class, 0);
        this.baseMapper = baseMapper;
        this.entityClass = (Class<T>) clazz;
        super.wrapperChildren = new LambdaUpdateWrapper<>() {
            @Override
            protected String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
                if (column instanceof ColumnSFunction columnSFunction) {
                    ColumnCache cache = columnSFunction.toColumnCache();
                    return onlyColumn ? cache.getColumn() : cache.getColumnSelect();
                }
                return this.columnToString(column, onlyColumn);
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
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getSqlSet");
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return this.baseMapper;
    }

    private boolean relatedUpdate;

    public final LambdaUpdate<T> relatedUpdate() {
        this.relatedUpdate = true;
        return this;
    }

    @Override
    public boolean update() {
        Map<SFunction, Object> updateSets = MybatisPlusUtil.tryUpdateSets(this);
        int update = this.getBaseMapper().update(null, this.getWrapper());
        if (update > 0 && this.relatedUpdate) {
            MybatisPlusUtil.relatedUpdate(this, updateSets);
        }
        return SqlHelper.retBool(update);
    }


    @Override
    public boolean update(T entity) {
        throw new MybatisPlusException("【LambdaUpdate】不允许使用【update(T entity)】方法");
    }


    private boolean relatedDelete;

    private List<Class<?>> relatedDeleteEntityClasses;

    public final LambdaUpdate<T> relatedDelete(Class<?>... classes) {
        this.relatedDelete = true;
        this.relatedDeleteEntityClasses = CollUtil.newArrayList(classes);
        return this;
    }

    @Override
    public boolean remove() {
        MybatisPlusUtil.tryUpdateSets(this);
        List<T> list = null;
        if (this.relatedDelete) {
            list = this.getBaseMapper().selectList(this.getWrapper());
        }
        int delete = this.getBaseMapper().delete(this.getWrapper());
        if (delete > 0 && this.relatedDelete) {
            MybatisPlusUtil.relatedDelete(list, this.getEntityClass(), this.relatedDeleteEntityClasses);
        }
        return SqlHelper.retBool(delete);
    }
}
```