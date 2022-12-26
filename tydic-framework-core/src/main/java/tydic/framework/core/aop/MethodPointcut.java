package tydic.framework.core.aop;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ClassFilters;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.MethodMatchers;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 常用方法切点
 */
public class MethodPointcut {
    /**
     * rest方法切点
     * <p/>
     * 1 @Controller(类) + @ResponseBody(类) + @RequestMapping(方法)
     * <p/>
     * 2 @RequestMapping(类) + @ResponseBody(方法)
     */
    public static Pointcut restMethodPointcut;

    static {
        AnnotationClassFilter controllerClass = new AnnotationClassFilter(Controller.class, true);
        AnnotationClassFilter responseBodyClass = new AnnotationClassFilter(ResponseBody.class, true);
        AnnotationMethodMatcher requestMappingMethod = new AnnotationMethodMatcher(RequestMapping.class, true);
        AnnotationMethodMatcher responseBodyMethod = new AnnotationMethodMatcher(ResponseBody.class, true);
        ComposablePointcut pointcut1 = new ComposablePointcut(ClassFilters.intersection(controllerClass, responseBodyClass), requestMappingMethod);
        ComposablePointcut pointcut2 = new ComposablePointcut(MethodMatchers.intersection(requestMappingMethod, responseBodyMethod));
        restMethodPointcut = org.springframework.aop.support.Pointcuts.union(pointcut1, pointcut2);
    }

}
