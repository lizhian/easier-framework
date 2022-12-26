package tydic.framework.starter.mybatis.relatedDelete;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.*;


/**
 * 数据删除事件
 *
 * @author don
 */
@Getter
public class EntityDeleteEvent<E> extends ApplicationEvent {
    /**
     * 已删除的数据
     */
    private final List<E> list;

    /**
     * 已删除数据的类型
     */
    private final Class<E> entityClass;

    /**
     * 关联删除数据的类型
     */
    private final Set<Class<?>> relatedDeleteClasses;

    private EntityDeleteEvent(List<E> list, Class<E> entityClass, Collection<Class<?>> relatedDeleteClasses) {
        super("");
        this.list = list;
        this.entityClass = entityClass;
        if (relatedDeleteClasses == null) {
            this.relatedDeleteClasses = Collections.emptySet();
        } else {
            this.relatedDeleteClasses = new HashSet<>(relatedDeleteClasses);
        }
    }

    public static <E> EntityDeleteEvent<E> create(List<E> list, Class<E> entityClass, Collection<Class<?>> classes) {
        return new EntityDeleteEvent<>(list, entityClass, classes);
    }
}