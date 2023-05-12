# 1 复制`LambdaQueryChainWrapper`类

# 2 继承接口增加拓展方法

```
    , LambdaExpandWhenMethod<T, LambdaQuery<T>>
    , LambdaExpandQueryMethod<T>
```

# 3 增加关联注入功能

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

# 4 重写查询方法实现关联查询功能

```
    @Override
    public List<T> list() {
        List<T> list = this.getBaseMapper().selectList(this.getWrapper());
        if (!this.bindField || CollUtil.isEmpty(list)) {
            return list;
        }
        if (CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(list, this.bindFields);

        } else {
            Binder.bind(list);
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

# 4 完整类

```java
public class LambdaQuery<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaQuery<T>, LambdaQueryWrapper<T>>
        implements ChainQuery<T>, Query<LambdaQuery<T>, T, SFunction<T, ?>>
        , LambdaExpandWhenMethod<T, LambdaQuery<T>>
        , LambdaExpandQueryMethod<T> {

    private final BaseMapper<T> baseMapper;

    public LambdaQuery(BaseMapper<T> baseMapper) {
        super();
        this.baseMapper = baseMapper;
        super.wrapperChildren = new LambdaQueryWrapper<>();
    }

    @SafeVarargs
    @Override
    public final LambdaQuery<T> select(SFunction<T, ?>... columns) {
        this.wrapperChildren.select(columns);
        return this.typedThis;
    }

    @Override
    public LambdaQuery<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        this.wrapperChildren.select(entityClass, predicate);
        return this.typedThis;
    }

    @Override
    public String getSqlSelect() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getSqlSelect");
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return this.baseMapper;
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
        List<T> list = this.getBaseMapper().selectList(this.getWrapper());
        if (!this.bindField || CollUtil.isEmpty(list)) {
            return list;
        }
        if (CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(list, this.bindFields);

        } else {
            Binder.bind(list);
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
}
```