package tydic.framework.starter.mybatis.relatedDelete;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.tangzc.mpe.base.MapperScanner;
import com.tangzc.mpe.base.event.InitScanEntityEvent;
import com.tangzc.mpe.base.util.BeanClassUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotatedElementUtils;
import tydic.framework.core.plugin.mybatis.RelatedDeleteCondition;
import tydic.framework.core.util.TableUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 关联删除控制器
 */
@Slf4j
public class RelatedDeleteManager {
    private static final List<Description> CACHE = new ArrayList<>();

    private static void addRelatedDeleteCondition(Class<?> entityClass, Field entityField, RelatedDeleteCondition relatedDeleteCondition) {
        if (relatedDeleteCondition.source() == Void.class || StrUtil.isBlank(relatedDeleteCondition.sourceField())) {
            log.error("{}类上的{}字段，@RelatedDeleteCondition缺少`source`或`sourceField`属性，自动关联删除数据功能将被忽略", entityClass, entityField.getName());
            return;
        }
        Class<?> sourceClass = relatedDeleteCondition.source();
        String sourceField = relatedDeleteCondition.sourceField();
        String column = TableUtil.fieldToColumn(entityClass, entityField);
        if (StrUtil.isBlank(column)) {
            log.error("{}类上的{}字段，@RelatedDeleteCondition上的`sourceField`属性{}，并非数据库字段,自动关联删除数据功能将被忽略", sourceField, entityClass, entityField.getName());
            return;
        }
        CACHE.add(new Description(sourceClass, sourceField, entityClass, entityField.getName(), column));
    }

    @EventListener
    public void onApplicationEvent(InitScanEntityEvent event) {
        Class<?> entityClass = event.getEntityClass();
        List<Field> allEntityFields = BeanClassUtil.getAllDeclaredFields(entityClass);
        for (Field entityField : allEntityFields) {
            // 扫描所有的Entity中的RelatedDeleteCondition注解
            RelatedDeleteCondition relatedDeleteCondition = AnnotatedElementUtils.findMergedAnnotation(entityField, RelatedDeleteCondition.class);
            if (relatedDeleteCondition != null) {
                RelatedDeleteManager.addRelatedDeleteCondition(entityClass, entityField, relatedDeleteCondition);
            }
        }
    }

    @EventListener
    public void onApplicationEvent(EntityDeleteEvent<?> entityDeleteEvent) {
        List<?> list = entityDeleteEvent.getList();
        Class<?> entityClass = entityDeleteEvent.getEntityClass();
        Set<Class<?>> relatedDeleteClasses = entityDeleteEvent.getRelatedDeleteClasses();

        //获取需要删除的类(表)
        Map<? extends Class<?>, List<Description>> filter = CACHE.stream()
                                                                 .filter(it -> it.getSourceClass().equals(entityClass))
                                                                 .filter(it -> CollUtil.isEmpty(relatedDeleteClasses) || relatedDeleteClasses.contains(it.getEntityClass()))
                                                                 .collect(Collectors.groupingBy(Description::getEntityClass));
        for (List<Description> conditions : filter.values()) {
            if (conditions.size() == 1) {
                this.executeDeleteWithSingleCondition(list, conditions.get(0));
                continue;
            }
            for (Object sourceObject : list) {
                this.executeDeleteWithMultiCondition(sourceObject, conditions);
            }
        }

    }


    /**
     * 只有一个关联字段,可以使用批量删除
     */
    private <E> void executeDeleteWithSingleCondition(List<?> list, Description condition) {
        String sourceField = condition.getSourceField();
        Class<?> entityClass = condition.getEntityClass();
        String entityField = condition.getEntityField();
        List<Object> values = list.stream()
                                  .map(data -> ReflectUtil.getFieldValue(data, sourceField))
                                  .filter(Objects::nonNull)
                                  .distinct()
                                  .collect(Collectors.toList());
        String column = TableUtil.fieldToColumn(entityClass, entityField);
        BaseMapper<E> mapper = MapperScanner.getMapper((Class<E>) entityClass);
        ChainWrappers.updateChain(mapper)
                     .in(column, values)
                     .remove();

    }

    /**
     * 多个关联字段,逐条删除
     */
    private <E> void executeDeleteWithMultiCondition(Object sourceObject, List<Description> conditions) {
        Class<?> entityClass = conditions.get(0).getEntityClass();
        BaseMapper<E> mapper = MapperScanner.getMapper((Class<E>) entityClass);
        UpdateChainWrapper<E> wrapper = ChainWrappers.updateChain(mapper);
        boolean doRemove = true;
        for (Description condition : conditions) {
            String sourceField = condition.getSourceField();
            Object sourceFieldValue = ReflectUtil.getFieldValue(sourceObject, sourceField);
            if (sourceFieldValue == null) {
                doRemove = false;
                break;
            }
            if (StrUtil.isBlankIfStr(sourceFieldValue)) {
                doRemove = false;
                break;
            }
            String column = condition.getEntityColumn();
            if (StrUtil.isBlank(column)) {
                doRemove = false;
                break;
            }
            wrapper.eq(column, sourceFieldValue);
        }
        if (doRemove) {
            wrapper.remove();
        }
    }


    @Data
    public static class Description {
        private final Class<?> sourceClass;
        private final String sourceField;
        private final Class<?> entityClass;
        private final String entityField;
        private final String entityColumn;
    }

}
