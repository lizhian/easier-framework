package easier.framework.starter.mybatis.repo.expand.repo.holder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.starter.mybatis.repo.Repo;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class KeyHolder<T, K> {
    private final Repo<T> repo;
    private final SFunction<T, K> keyColumn;

    public long count(K key) {
        if (StrUtil.isBlankIfStr(key)) {
            return 0;
        }
        return this.repo.newQuery()
                .eq(this.keyColumn, key)
                .count();
    }

    public long count(Collection<K> keys) {
        if (CollUtil.isEmpty(keys)) {
            return 0;
        }
        return this.repo.newQuery()
                .in(this.keyColumn, keys)
                .count();
    }

    public boolean exists(K key) {
        if (StrUtil.isBlankIfStr(key)) {
            return false;
        }
        return this.repo.newQuery()
                .eq(this.keyColumn, key)
                .exists();
    }

    public <V> boolean isUnique(V key) {
        if (StrUtil.isBlankIfStr(key)) {
            return false;
        }
        return this.repo.newQuery()
                .eq(this.keyColumn, key)
                .count() < 1;
    }

    public <V> boolean isNotUnique(V key) {
        if (StrUtil.isBlankIfStr(key)) {
            return false;
        }
        return this.repo.newQuery()
                .eq(this.keyColumn, key)
                .count() > 0;
    }

    @Nullable
    public T any(K key) {
        if (StrUtil.isBlankIfStr(key)) {
            return null;
        }
        return this.repo.newQuery()
                .eq(this.keyColumn, key)
                .any();
    }

    @Nonnull
    public List<T> list(K key) {
        if (StrUtil.isBlankIfStr(key)) {
            return new ArrayList<>();
        }
        return this.repo.newQuery()
                .eq(this.keyColumn, key)
                .list();
    }

    @Nonnull
    public List<T> list(Collection<K> keys) {
        if (CollUtil.isEmpty(keys)) {
            return new ArrayList<>();
        }
        return this.repo.newQuery()
                .in(this.keyColumn, keys)
                .list();
    }

    public boolean delete(K key) {
        if (StrUtil.isBlankIfStr(key)) {
            return false;
        }
        return this.repo.newUpdate()
                .eq(this.keyColumn, key)
                .remove();
    }

    public boolean delete(Collection<K> keys) {
        if (CollUtil.isEmpty(keys)) {
            return false;
        }
        return this.repo.newUpdate()
                .in(this.keyColumn, keys)
                .remove();
    }
}