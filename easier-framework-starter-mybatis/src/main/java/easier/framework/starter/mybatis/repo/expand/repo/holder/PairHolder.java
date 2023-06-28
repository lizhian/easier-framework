package easier.framework.starter.mybatis.repo.expand.repo.holder;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.starter.mybatis.repo.Repo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class PairHolder<T, K, V> extends KeyHolder<T, K> {

    private final Repo<T> repo;
    private final SFunction<T, K> keyColumn;
    private final SFunction<T, V> valueColumn;

    public PairHolder(Repo<T> repo, SFunction<T, K> keyColumn, SFunction<T, V> valueColumn) {
        super(repo, keyColumn);
        this.repo = repo;
        this.keyColumn = keyColumn;
        this.valueColumn = valueColumn;
    }

    public long count(K key, V value) {
        if (StrUtil.isBlankIfStr(key) || StrUtil.isBlankIfStr(value)) {
            return 0;
        }
        return this.repo.newQuery()
                .eq(this.keyColumn, key)
                .eq(this.valueColumn, value)
                .count();
    }

    public boolean exists(K key, V value) {
        if (StrUtil.isBlankIfStr(key) || StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.repo.newQuery()
                .eq(this.keyColumn, key)
                .eq(this.valueColumn, value)
                .exists();
    }

    public boolean isUnique(K key, V value) {
        if (StrUtil.isBlankIfStr(key) || StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.repo.newQuery()
                .eq(this.keyColumn, key)
                .eq(this.valueColumn, value)
                .count() < 1;
    }

    public boolean isNotUnique(K key, V value) {
        if (StrUtil.isBlankIfStr(key) || StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.repo.newQuery()
                .eq(this.keyColumn, key)
                .eq(this.valueColumn, value)
                .count() > 0;
    }

    @Nullable
    public T any(K key, V value) {
        if (StrUtil.isBlankIfStr(key) || StrUtil.isBlankIfStr(value)) {
            return null;
        }
        return this.repo.newQuery()
                .eq(this.keyColumn, key)
                .eq(this.valueColumn, value)
                .any();
    }

    @Nonnull
    public List<V> toValueList(K key) {
        return this.list(key)
                .stream()
                .map(this.valueColumn)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Nonnull
    public List<V> toValueList(Collection<K> keys) {
        return this.list(keys)
                .stream()
                .map(this.valueColumn)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Nonnull
    public Set<V> toValueSet(K key) {
        return new HashSet<>(this.toValueList(key));
    }

    @Nonnull
    public Set<V> toValueSet(Collection<K> keys) {
        return new HashSet<>(this.toValueList(keys));
    }

    public boolean delete(K key, V value) {
        if (StrUtil.isBlankIfStr(key) || StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.repo.newUpdate()
                .eq(this.keyColumn, key)
                .eq(this.valueColumn, value)
                .remove();
    }
}