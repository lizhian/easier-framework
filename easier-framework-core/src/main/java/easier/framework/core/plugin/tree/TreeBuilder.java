package easier.framework.core.plugin.tree;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.core.util.StrUtil;
import easier.framework.core.util.TreeUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.function.BiConsumer;

@Getter
@Builder
@RequiredArgsConstructor
public class TreeBuilder<T> {
    /**
     * 树跟节点值
     */
    @Builder.Default
    private final Serializable rootKey = TreeUtil.ROOT_KEY;
    /**
     * 树节点主键
     */
    private final SFunction<T, Serializable> key;
    /**
     * 树节点名称
     */
    @Builder.Default
    private final SFunction<T, String> name = it -> null;
    /**
     * 树父节点主键
     */
    private final SFunction<T, Serializable> parentKey;
    /**
     * 树排序
     * 正序
     */
    @Builder.Default
    private final SFunction<T, Number> sort = it -> TreeUtil.DEFAULT_SORT;
    /**
     * 是否启用
     */
    @Builder.Default
    private final SFunction<T, Boolean> enable = it -> true;
    /**
     * 风格
     */
    @Builder.Default
    private final SFunction<T, String> style = it -> StrUtil.EMPTY;
    /**
     * 子节点字段
     */
    @Builder.Default

    private final BiConsumer<T, List<T>> children = (it, list) -> {
    };

    public static <T> TreeBuilder.TreeBuilderBuilder<T> of(Class<T> clazz) {
        return TreeBuilder.builder();
    }


    public int getSortValue(T data) {
        if (data == null || this.sort == null) {
            return TreeUtil.DEFAULT_SORT;
        }
        Number sort = this.sort.apply(data);
        if (sort == null) {
            return TreeUtil.DEFAULT_SORT;
        }
        return sort.intValue();
    }

    public int getSortValue(TreeNode<T> node) {
        if (node == null || this.sort == null) {
            return TreeUtil.DEFAULT_SORT;
        }
        return getSortValue(node.getData());
    }

    @Nonnull
    public List<TreeNode<T>> listTreeNode(List<T> list) {
        return TreeUtil.listTreeNode(this, this.rootKey, list);
    }

    @Nonnull
    public List<T> asList(List<TreeNode<T>> treeNodes) {
        return TreeUtil.asList(this, treeNodes);
    }

    @Nonnull
    public List<TreeNode<T>> listTreeNode(Serializable parentKey, List<T> list) {
        return TreeUtil.listTreeNode(this, parentKey, list);
    }

    @Nullable
    public TreeNode<T> treeNode(List<T> list, Serializable key) {
        return TreeUtil.treeNode(this, key, list);
    }

    @Nullable
    public TreeNode<T> treeNode(T data, List<TreeNode<T>> children) {
        return TreeUtil.treeNode(this, data, children);
    }

}
