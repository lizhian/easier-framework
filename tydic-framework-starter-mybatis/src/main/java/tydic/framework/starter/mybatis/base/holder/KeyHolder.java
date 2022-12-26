package tydic.framework.starter.mybatis.base.holder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import tydic.framework.starter.mybatis.base.BaseRepo;

import javax.annotation.Nonnull;
import java.util.*;

public class KeyHolder<T, K> {
    private final BaseRepo<T> repository;
    private final SFunction<T, K> keyColumn;

    public KeyHolder(BaseRepo<T> repository,
                     SFunction<T, K> keyColumn) {
        if (keyColumn == null) {
            throw new MybatisPlusException("[keyColumn]不能为空");
        }
        this.repository = repository;
        this.keyColumn = keyColumn;
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


    public boolean exists(K key) {
        if (StrUtil.isBlankIfStr(key)) {
            return false;
        }
        return this.repository.newQuery()
                              .eq(this.keyColumn, key)
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


}