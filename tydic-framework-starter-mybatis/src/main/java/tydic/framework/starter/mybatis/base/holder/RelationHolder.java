package tydic.framework.starter.mybatis.base.holder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import tydic.framework.starter.mybatis.base.RelationRepo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class RelationHolder<T, K, V> {

    private final RelationRepo<T> repository;
    private final SFunction<T, K> keyColumn;
    private final SFunction<T, V> valueColumn;


    public RelationHolder(RelationRepo<T> repository,
                          SFunction<T, K> keyColumn,
                          SFunction<T, V> valueColumn) {
        if (keyColumn == null || valueColumn == null) {
            throw new MybatisPlusException("[keyColumn][valueColumn]不能为空");
        }
        this.repository = repository;
        this.keyColumn = keyColumn;
        this.valueColumn = valueColumn;
    }

    public long count(K key) {
        if (StrUtil.isBlankIfStr(key)) {
            return 0;
        }
        return this.repository.newQuery()
                              .eq(this.keyColumn, key)
                              .count();
    }

    public long count(Collection<K> keys) {
        if (CollUtil.isEmpty(keys)) {
            return 0;
        }
        return this.repository.newQuery()
                              .in(this.keyColumn, keys)
                              .count();
    }

    @Nullable
    public T get(K key, V value) {
        if (StrUtil.isBlankIfStr(key) || StrUtil.isBlankIfStr(value)) {
            return null;
        }
        return this.repository.newQuery()
                              .eq(this.keyColumn, key)
                              .eq(this.valueColumn, value)
                              .any();
    }

    @Nonnull
    public List<T> toList(K key) {
        if (StrUtil.isBlankIfStr(key)) {
            return new ArrayList<>();
        }
        return this.repository.newQuery()
                              .eq(this.keyColumn, key)
                              .list();
    }

    @Nonnull
    public List<T> toList(Collection<K> keys) {
        if (CollUtil.isEmpty(keys)) {
            return new ArrayList<>();
        }
        return this.repository.newQuery()
                              .in(this.keyColumn, keys)
                              .list();
    }

    @Nonnull
    public Set<T> toSet(K key) {
        return new HashSet<>(this.toList(key));
    }

    @Nonnull
    public Set<T> toSet(Collection<K> keys) {
        return new HashSet<>(this.toList(keys));
    }

    @Nonnull
    public List<V> valueToList(K key) {
        return this.toList(key)
                   .stream()
                   .map(this.valueColumn)
                   .filter(Objects::nonNull)
                   .collect(Collectors.toList());
    }

    @Nonnull
    public List<V> valueToList(Collection<K> keys) {
        return this.toList(keys)
                   .stream()
                   .map(this.valueColumn)
                   .filter(Objects::nonNull)
                   .collect(Collectors.toList());
    }

    @Nonnull
    public Set<V> valueToSet(K key) {
        return this.toList(key)
                   .stream()
                   .map(this.valueColumn)
                   .filter(Objects::nonNull)
                   .collect(Collectors.toSet());
    }

    @Nonnull
    public Set<V> valueToSet(Collection<K> keys) {
        return this.toList(keys)
                   .stream()
                   .map(this.valueColumn)
                   .filter(Objects::nonNull)
                   .collect(Collectors.toSet());
    }

    public boolean exists(K key) {
        if (StrUtil.isBlankIfStr(key)) {
            return false;
        }
        return this.repository.newQuery()
                              .eq(this.keyColumn, key)
                              .exists();
    }

    public boolean exists(K key, V value) {
        if (StrUtil.isBlankIfStr(key) || StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.repository.newQuery()
                              .eq(this.keyColumn, key)
                              .eq(this.valueColumn, value)
                              .exists();
    }

    public boolean delete(K key) {
        if (StrUtil.isBlankIfStr(key)) {
            return false;
        }
        return this.repository.newUpdate()
                              .eq(this.keyColumn, key)
                              .remove();
    }

    public boolean delete(Collection<K> keys) {
        if (CollUtil.isEmpty(keys)) {
            return false;
        }
        return this.repository.newUpdate()
                              .in(this.keyColumn, keys)
                              .remove();
    }

    public boolean delete(K key, V value) {
        if (StrUtil.isBlankIfStr(key) || StrUtil.isBlankIfStr(value)) {
            return false;
        }
        return this.repository.newUpdate()
                              .eq(this.keyColumn, key)
                              .eq(this.valueColumn, value)
                              .remove();
    }
}