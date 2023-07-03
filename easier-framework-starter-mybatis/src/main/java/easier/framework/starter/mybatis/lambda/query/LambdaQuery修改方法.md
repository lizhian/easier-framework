# 1 复制`LambdaQueryChainWrapper`类

# 2 继承接口增加拓展方法

```
        , WhenMethod<T, LambdaQuery<T>>
        , QueryMethod<T>
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

# 4 增加关联注入功能

```
    private boolean bindField;
    private List<SFunction<T, ?>> bindFields;

    @SafeVarargs
    public final LambdaQuery<T> bind(SFunction<T, ?>... fields) {
        this.bindField = true;
        this.bindFields = CollUtil.newArrayList(fields);
        return this;
    }
```

# 5 重写查询方法实现关联查询功能

```
    @Override
    public List<T> list() {
        List<T> list = this.getBaseMapper().selectList(this.getWrapper());
        if (CollUtil.isEmpty(list)) {
            return list;
        }
        if (this.bindField && CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(list);
        }
        if (this.bindField && CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(list, this.bindFields);
        }
        return list;
    }

    @Override
    public T one() {
        T one = this.getBaseMapper().selectOne(this.getWrapper());
        if (this.bindField && CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(one);
        }
        if (this.bindField && CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(one, this.bindFields);
        }
        return one;
    }

    @Override
    public <E extends IPage<T>> E page(E page) {
        E selectPage = this.getBaseMapper().selectPage(page, this.getWrapper());
        if (this.bindField && CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(selectPage);
        }
        if (this.bindField && CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(selectPage, this.bindFields);
        }
        return selectPage;
    }
```

# 6 完整类

```java
public class LambdaQuery<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaQuery<T>, LambdaQueryWrapper<T>>
        implements ChainQuery<T>, Query<LambdaQuery<T>, T, SFunction<T, ?>>
        , WhenMethod<T, LambdaQuery<T>>
        , QueryMethod<T> {
    private final BaseMapper<T> baseMapper;

    public LambdaQuery(BaseMapper<T> baseMapper, Class<T> entityClass) {
        super();
        this.baseMapper = baseMapper;
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
    }

    @SafeVarargs
    @Override
    public final LambdaQuery<T> select(SFunction<T, ?>... columns) {
        wrapperChildren.select(columns);
        return typedThis;
    }

    @Override
    public LambdaQuery<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        wrapperChildren.select(entityClass, predicate);
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getSqlSelect");
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public Class<T> getEntityClass() {
        return super.wrapperChildren.getEntityClass();
    }


    private boolean bindField;
    private List<SFunction<T, ?>> bindFields;

    @SafeVarargs
    public final LambdaQuery<T> bind(SFunction<T, ?>... fields) {
        this.bindField = true;
        this.bindFields = CollUtil.newArrayList(fields);
        return this;
    }

    @Override
    public List<T> list() {
        List<T> list = this.execute(mapper -> mapper.selectList(this.getWrapper()));
        if (CollUtil.isEmpty(list)) {
            return list;
        }
        if (this.bindField && CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(list);
        }
        if (this.bindField && CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(list, this.bindFields);
        }
        return list;
    }

    @Override
    public T one() {
        T one = this.execute(mapper -> mapper.selectOne(this.getWrapper()));
        if (this.bindField && CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(one);
        }
        if (this.bindField && CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(one, this.bindFields);
        }
        return one;
    }

    @Override
    public <E extends IPage<T>> E page(E page) {
        E selectPage = this.execute(mapper -> mapper.selectPage(page, this.getWrapper()));
        if (this.bindField && CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(selectPage);
        }
        if (this.bindField && CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(selectPage, this.bindFields);
        }
        return selectPage;
    }
}
```