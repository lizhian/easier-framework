package tydic.framework.core.plugin.jackson.expland;

import java.lang.annotation.Annotation;

/**
 * json拓展器
 */
public interface JsonExpander<A extends Annotation> {
    void doExpand(A a, JsonExpandContext context);
}
