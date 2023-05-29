/*
package com.github.yulichang.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import tydic.framework.core.proxy.TypedSelf;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

*/
/**
 * 拓展查询方法
 *//*

public interface ExpandQueryMethod<T> extends TypedSelf<MPJLambdaWrapper<T>> {

    default Long count() {
        MPJLambdaWrapper<T> self = this.self();
        MPJBaseMapper<T> mapper = self.;
        Integer result = mapper.selectJoinCount(this.self());
        return result == null ? 0 : result.longValue();
    }

    default <DTO> List<DTO> list(Class<DTO> clazz) {
        MPJBaseMapper<T> mapper = this.self().getMapper();
        List<DTO> list = mapper.selectJoinList(clazz, this.self());
        return list == null ? new ArrayList<>() : list;
    }

    default <DTO> DTO one(Class<DTO> clazz) {
        MPJBaseMapper<T> mapper = this.self().getMapper();
        return mapper.selectJoinOne(clazz, this.self());
    }

    default <DTO, P extends IPage<DTO>> P page(Class<DTO> clazz, P page) {
        MPJBaseMapper<T> mapper = this.self().getMapper();
        IPage<DTO> result = mapper.selectJoinPage(page, clazz, this.self());
        return (P) result;
    }

    default <DTO> Optional<DTO> oneOpt(Class<DTO> clazz) {
        return Optional.ofNullable(this.one(clazz));
    }

    default boolean exists() {
        return this.count() > 0;
    }

    //page方法
    default <DTO> Page<DTO> page(Class<DTO> clazz, int current, int size) {
        return this.page(clazz, current, size, true);
    }

    default <DTO> Page<DTO> page(Class<DTO> clazz, int current, int size, boolean searchCount) {
        Page<DTO> page = new Page<>(current, size, searchCount);
        return this.page(clazz, page);
    }

    default <DTO> Page<DTO> page(Class<DTO> clazz, long current, long size) {
        return this.page(clazz, current, size, true);
    }

    default <DTO> Page<DTO> page(Class<DTO> clazz, long current, long size, boolean searchCount) {
        return this.page(clazz, new Page<>(current, size, searchCount));
    }

    //limit方法
    default <DTO> List<DTO> limit(Class<DTO> clazz, int size) {
        return this.page(clazz, 1, size, false).getRecords();
    }

    default <DTO> List<DTO> limit(Class<DTO> clazz, long size) {
        return this.page(clazz, 1, size, false).getRecords();
    }

    //stream方法
    default <DTO> Stream<DTO> stream(Class<DTO> clazz) {
        return this.list(clazz).stream();
    }

    default <DTO> Stream<DTO> stream(Class<DTO> clazz, int size) {
        return this.limit(clazz, size).stream();
    }

    default <DTO> Stream<DTO> stream(Class<DTO> clazz, long size) {
        return this.limit(clazz, size).stream();
    }

    default <DTO> Stream<DTO> stream(Class<DTO> clazz, int current, int size) {
        Page<DTO> page = new Page<>(current, size, false);
        return this.stream(clazz, page);
    }

    default <DTO> Stream<DTO> stream(Class<DTO> clazz, long current, long size) {
        Page<DTO> page = new Page<>(current, size, false);
        return this.stream(clazz, page);
    }

    default <DTO, P extends IPage<DTO>> Stream<DTO> stream(Class<DTO> clazz, P page) {
        return this.page(clazz, page)
                   .getRecords()
                   .stream();
    }

    //any方法
    default <DTO> Optional<DTO> anyOpt(Class<DTO> clazz) {
        return this.stream(clazz, 1)
                   .findAny();
    }

    default <DTO> DTO any(Class<DTO> clazz) {
        return this.anyOrElse(clazz, null);
    }

    default <DTO> DTO anyOrElse(Class<DTO> clazz, DTO other) {
        return this.anyOpt(clazz)
                   .orElse(other);
    }

    //toCollection方法
    default <DTO, V> List<V> toList(Class<DTO> clazz, SFunction<DTO, V> column) {
        return this.stream(clazz)
                   .map(column)
                   .collect(Collectors.toList());
    }

    default <DTO, V> Set<V> toSet(Class<DTO> clazz, SFunction<DTO, V> column) {
        return this.stream(clazz)
                   .map(column)
                   .collect(Collectors.toSet());
    }

    default <DTO, K, V> Map<K, V> toMap(Class<DTO> clazz, SFunction<DTO, K> keyColumn, SFunction<DTO, V> valueColumn) {
        return this.stream(clazz)
                   .collect(Collectors.toMap(keyColumn, valueColumn));
    }
}
*/
