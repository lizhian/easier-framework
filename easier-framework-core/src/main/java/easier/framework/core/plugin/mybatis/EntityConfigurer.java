package easier.framework.core.plugin.mybatis;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import easier.framework.core.domain.TreeBuilder;
import lombok.Getter;

@Getter
public class EntityConfigurer<T> {

    private boolean init = false;

    /**
     * 编码字段
     */
    private SFunction<T, String> codeColumn;
    /**
     * 排序字段
     */
    private SFunction<T, Object> orderColumn;
    /**
     * 是否正序
     */
    private boolean isAsc = true;

    /**
     * 树结构
     */
    private TreeBuilder<T> treeBuilder;

    public EntityConfigurer<T> codeColumn(SFunction<T, String> codeColumn) {
        if (this.init) {
            return this;
        }
        this.codeColumn = codeColumn;
        return this;
    }

    public EntityConfigurer<T> orderColumn(SFunction<T, Object> orderColumn) {
        if (this.init) {
            return this;
        }
        this.orderColumn = orderColumn;
        return this;
    }


    public EntityConfigurer<T> isAsc(boolean isAsc) {
        if (this.init) {
            return this;
        }
        this.isAsc = isAsc;
        return this;
    }

    public EntityConfigurer<T> treeBuilder(TreeBuilder<T> treeBuilder) {
        if (this.init) {
            return this;
        }
        this.treeBuilder = treeBuilder;
        return this;
    }

    public void finish() {
        if (this.init) {
            return;
        }
        this.init = true;
    }

    public LambdaQueryChainWrapper<T> wrapOrderBy(LambdaQueryChainWrapper<T> query) {
        if (this.orderColumn == null) {
            return query;
        }
        if (this.isAsc) {
            return query.orderByAsc(this.orderColumn);
        } else {
            return query.orderByDesc(this.orderColumn);
        }
    }
}
