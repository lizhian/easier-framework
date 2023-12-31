/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package easier.framework.starter.mybatis.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.tangzc.mpe.base.event.EntityUpdateEvent;
import easier.framework.core.plugin.mybatis.MybatisPlusEntity;
import easier.framework.core.util.SpringUtil;
import easier.framework.starter.mybatis.lambda.update.LambdaUpdate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MybatisPlusUtil {

    /**
     * 新增数据前的操作
     */
    public static <T> void preInsert(T entity) {
        if (entity instanceof MybatisPlusEntity) {
            MybatisPlusEntity mybatisPlusEntity = (MybatisPlusEntity) entity;
            mybatisPlusEntity.preInsert();
        }
    }

    /**
     * 更新数据前的操作
     */
    public static <T> void preUpdate(T entity) {
        if (entity instanceof MybatisPlusEntity) {
            MybatisPlusEntity mybatisPlusEntity = (MybatisPlusEntity) entity;
            mybatisPlusEntity.preUpdate();
        }
    }

    /**
     * 更新数据前的操作,针对LambdaUpdate
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Map<SFunction, Object> tryUpdateSets(LambdaUpdate<T> lambdaUpdate) {
        try {
            Class<T> entityClass = lambdaUpdate.getEntityClass();
            T t = ReflectUtil.newInstanceIfPossible(entityClass);
            if (t instanceof MybatisPlusEntity) {
                MybatisPlusEntity mybatisPlusEntity = (MybatisPlusEntity) t;
                Map<SFunction, Object> updateSets = new HashMap<>();
                mybatisPlusEntity.preLambdaUpdate(updateSets);
                for (SFunction column : updateSets.keySet()) {
                    Object value = updateSets.get(column);
                    lambdaUpdate.set(column, value);
                }
                return updateSets;
            }
            return new HashMap<>();
        } catch (Exception ignored) {
            return new HashMap<>();
        }
    }

    public static <T> void relatedUpdate(LambdaUpdate<T> lambdaUpdate, Map<SFunction, Object> updateSets) {
        if (CollUtil.isEmpty(updateSets)) {
            return;
        }
        LambdaQueryChainWrapper<T> wrapper = ChainWrappers.lambdaQueryChain(lambdaUpdate.getBaseMapper());
        updateSets.forEach(wrapper::eq);
        wrapper.list()
                .stream()
                .map(EntityUpdateEvent::create)
                .forEach(SpringUtil::publishEvent);
    }


    public static <T> void publishUpdateEvent(T data) {
        if (data instanceof Collection<?>) {
            Collection<?> list = (Collection<?>) data;
            list.forEach(MybatisPlusUtil::publishUpdateEvent);
            return;
        }
        SpringUtil.publishEvent(EntityUpdateEvent.create(data));
    }


}

