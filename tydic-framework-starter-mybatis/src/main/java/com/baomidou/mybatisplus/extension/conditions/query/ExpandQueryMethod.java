package com.baomidou.mybatisplus.extension.conditions.query;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import tydic.framework.core.proxy.TypedSelf;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 拓展查询方法
 */
public interface ExpandQueryMethod<T> extends ChainQuery<T>, TypedSelf<LambdaQueryChainWrapper<T>> {

    /**
     * 查询后置处理
     */
    void afterQuery(T t);

    /**
     * 查询后置处理
     */
    void afterQuery(List<T> list);

    /**
     * 查询后置处理
     */
    void afterQuery(IPage<T> page);

    @Override
    default Long count() {
        Long result = this.getBaseMapper().selectCount(this.getWrapper());
        return SqlHelper.retCount(result);
    }

    @Override
    default T one() {
        T one = this.getBaseMapper().selectOne(this.getWrapper());
        this.afterQuery(one);
        return one;
    }

    @Override
    default List<T> list() {
        List<T> list = this.getBaseMapper().selectList(this.getWrapper());
        this.afterQuery(list);
        return list;
    }

    @Override
    default <P extends IPage<T>> P page(P page) {
        P selectPage = this.getBaseMapper().selectPage(page, this.getWrapper());
        this.afterQuery(selectPage);
        return selectPage;
    }

    //page方法
    default Page<T> page(int current, int size) {
        return this.page(current, size, true);
    }

    default Page<T> page(long current, long size) {
        return this.page(current, size, true);
    }

    default Page<T> page(int current, int size, boolean searchCount) {
        return this.page(new Page<>(current, size, searchCount));
    }

    default Page<T> page(long current, long size, boolean searchCount) {
        return this.page(new Page<>(current, size, searchCount));
    }

    //limit方法
    default List<T> limit(int size) {
        return this.page(1, size, false)
                   .getRecords();
    }

    default List<T> limit(long size) {
        return this.page(1, size, false)
                   .getRecords();
    }

    //stream方法
    default Stream<T> stream() {
        return this.list()
                   .stream();
    }

    default Stream<T> stream(int size) {
        return this.limit(size)
                   .stream();
    }

    default Stream<T> stream(long size) {
        return this.limit(size)
                   .stream();
    }

    default Stream<T> stream(int current, int size) {
        return this.page(current, size, false)
                   .getRecords()
                   .stream();
    }

    default Stream<T> stream(long current, long size) {
        return this.page(current, size, false)
                   .getRecords()
                   .stream();
    }

    default <P extends IPage<T>> Stream<T> stream(P page) {
        return this.page(page)
                   .getRecords()
                   .stream();
    }

    //any方法
    default T any() {
        return this.anyOrElse(null);
    }

    default Optional<T> anyOpt() {
        return this.stream(1)
                   .findAny();
    }

    default T anyOrElse(T other) {
        return this.anyOpt()
                   .orElse(other);
    }

    default <V> T anyBy(SFunction<T, V> column, V value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String strValue && StrUtil.isBlank(strValue)) {
            return null;
        }
        return this.self()
                   .eq(column, value)
                   .any();
    }

    //toCollection方法
    default <V> List<V> toList(SFunction<T, V> column) {
        return this.stream()
                   .map(column)
                   .collect(Collectors.toList());
    }

    default <V> Set<V> toSet(SFunction<T, V> column) {
        return this.stream()
                   .map(column)
                   .collect(Collectors.toSet());
    }

    default <K, V> Map<K, V> toMap(SFunction<T, K> keyColumn, SFunction<T, V> valueColumn) {
        return this.stream()
                   .collect(Collectors.toMap(keyColumn, valueColumn));
    }
}
