package tydic.framework.starter.mybatis.init;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.actable.ProfileCondition;
import com.tangzc.mpe.actable.annotation.DsName;
import com.tangzc.mpe.actable.annotation.TablePrimary;
import com.tangzc.mpe.actable.constants.Constants;
import com.tangzc.mpe.actable.manager.handler.TableInitHandler;
import com.tangzc.mpe.actable.utils.ColumnUtils;
import com.tangzc.mpe.base.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotatedElementUtils;
import tydic.framework.core.util.SpringUtil;
import tydic.framework.core.util.StrUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Conditional(ProfileCondition.class)
public class InitEntityTable implements ApplicationListener<ContextRefreshedEvent> {
    /**
     * 数据库类型：mysql
     */
    public static final String MYSQL = "mysql";

    /**
     * 数据库类型
     */
    @Value(Constants.ACTABLE_DATABASE_TYPE_KEY_VALUE)
    private String databaseType;
    /**
     * 自动创建模式：update表示更新，create表示删除原表重新创建
     */
    @Value(Constants.ACTABLE_TABLE_AUTO_KEY_VALUE)
    private String tableAuto;

    /**
     * 要扫描的model所在的pack
     */
    @Value(Constants.ACTABLE_MODEL_PACK_KEY_VALUE)
    private String pack;

    @Resource
    private TableInitHandler tableInitHandler;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (StrUtil.isNotBlank(this.pack)) {
            return;
        }
        Set<Class<?>> classes = SpringUtil.getBeansOfType(BaseMapper.class)
                                          .values()
                                          .stream()
                                          .map(baseMapper -> (Class<?>) ReflectionUtil.getEntityClass(baseMapper))
                                          .collect(Collectors.toSet());
        if (this.checkRunEnd()) {
            return;
        }
        // 处理重名表，并根据数据源分类
        Map<String, Set<Class<?>>> needCreateTableMap = this.filterRepeatTable(classes);
        this.tableInitHandler.initTable(needCreateTableMap);
    }


    private boolean checkRunEnd() {
        // 执行mysql的处理方法
        if (!MYSQL.equals(this.databaseType)) {
            log.warn("{}数据库暂不支持！无法自动创建表", this.databaseType);
            return true;
        }

        // 不做任何事情
        if (!"none".equals(this.tableAuto) && !"update".equals(this.tableAuto) && !"create".equals(this.tableAuto) && !"add".equals(this.tableAuto)) {
            log.warn("配置mybatis.table.auto错误无法识别，当前配置只支持[none/update/create/add]三种类型!");
            return true;
        }

        // 不做任何事情
        if ("none".equals(this.tableAuto)) {
            log.info("配置mybatis.table.auto=none，不需要做任何事情");
            return true;
        }
        return false;
    }

    /**
     * 处理重名表
     */
    private Map<String, Set<Class<?>>> filterRepeatTable(Set<Class<?>> classes) {

        Map<String, List<Class<?>>> classMap = classes.stream().collect(Collectors.groupingBy(ColumnUtils::getTableName));
        // <数据源，List<表>>
        Map<String, Set<Class<?>>> needCreateTable = new HashMap<>(classMap.size());
        classMap.forEach((tableName, sameClasses) -> {
            final Class<?> primaryClass;
            // 挑选出重名的表，找到其中标记primary的，用作生成数据表的依据
            if (sameClasses.size() > 1) {
                List<Class<?>> primaryClasses = sameClasses.stream()
                                                           .filter(clazz -> AnnotatedElementUtils.findMergedAnnotation(clazz, TablePrimary.class).value())
                                                           .collect(Collectors.toList());
                if (primaryClasses.isEmpty()) {
                    throw new RuntimeException("表名[" + tableName + "]出现重复，必须指定一个为@TablePrimary！");
                }
                if (primaryClasses.size() > 1) {
                    throw new RuntimeException("表名[" + tableName + "]出现重复，有且只能有一个为@TablePrimary！");
                }
                primaryClass = primaryClasses.get(0);
            } else {
                primaryClass = sameClasses.get(0);
            }
            final DsName mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(primaryClass, DsName.class);
            String dsName = mergedAnnotation != null ? mergedAnnotation.value() : "";
            needCreateTable.computeIfAbsent(dsName, k -> new HashSet<>()).add(primaryClass);
        });
        return needCreateTable;
    }
}
