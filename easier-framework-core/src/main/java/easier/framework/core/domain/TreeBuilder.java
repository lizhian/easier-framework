package easier.framework.core.domain;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.core.util.StrUtil;
import easier.framework.core.util.TreeUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.function.BiConsumer;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TreeBuilder<T> {
    /**
     * 树节点主键
     */
    private final SFunction<T, Serializable> key;
    /**
     * 树节点名称
     */
    private final SFunction<T, String> name;
    /**
     * 树父节点主键
     */
    private final SFunction<T, Serializable> parentKey;
    /**
     * 树排序
     * 正序
     */
    private final SFunction<T, Number> sort;

    private final boolean hasSort;
    /**
     * 树跟节点值
     */
    private final Serializable rootKey;

    private final BiConsumer<T, List<T>> children;


    public int getSortValue(T t) {
        try {
            return this.sort.apply(t).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getSortValue(TreeNode<T> t) {
        try {
            return this.sort.apply(t.getNodeData()).intValue();
        } catch (Exception e) {
            return 0;
        }
    }


    public boolean enable() {
        return this.key != null
                && this.name != null
                && this.parentKey != null
                && this.sort != null
                && this.rootKey != null
                && StrUtil.isNotBlank(this.rootKey.toString())
                ;
    }

    public boolean disable() {
        return !this.enable();
    }

    @Nonnull
    public List<TreeNode<T>> build(List<T> all) {
        return this.build(all, this.rootKey);
    }

    @Nonnull
    public List<TreeNode<T>> build(List<T> all, Serializable key) {
        return TreeUtil.build(this, all, key);
    }

    @Nullable
    public TreeNode<T> buildNode(List<T> all, String rootKey) {
        if (StrUtil.isBlank(rootKey)) {
            return null;
        }
        //获取当前节点数据
        T nodeData = all.stream()
                .filter(it -> rootKey.equals(this.getParentKey().apply(it)))
                .findAny()
                .orElse(null);
        if (nodeData == null) {
            return null;
        }
        //获取子节点数据
        List<TreeNode<T>> children = this.build(all, rootKey);
        //构建树结构
        return this.createTreeNode(nodeData, children);

    }

    @Nullable
    public TreeNode<T> createTreeNode(T nodeData, List<TreeNode<T>> children) {
        if (nodeData == null || this.disable()) {
            return null;
        }
        Serializable key = this.key.apply(nodeData);
        String name = this.name.apply(nodeData);
        Serializable parentKey = this.parentKey.apply(nodeData);
        boolean hasChildren = CollUtil.isNotEmpty(children);
        if (StrUtil.isBlankIfStr(key) || StrUtil.isBlankIfStr(parentKey)) {
            return null;
        }
        TreeNode<T> treeNode = new TreeNode<>();
        treeNode.setKey(key);
        treeNode.setName(name);
        treeNode.setParentKey(parentKey);
        treeNode.setNodeData(nodeData);
        treeNode.setChildren(hasChildren ? children : null);
        treeNode.setHasChildren(hasChildren);
        return treeNode;
    }


    @Nullable
    public TreeNode<T> createTreeNode(T nodeData, boolean hasChildren) {
        TreeNode<T> treeNode = this.createTreeNode(nodeData, null);
        if (treeNode == null) {
            return null;
        }
        treeNode.setChildren(null);
        treeNode.setHasChildren(hasChildren);
        return treeNode;
    }


    static class TreeConfigurer<T> {
        private SFunction<T, Serializable> key;
        private SFunction<T, String> name;
        private SFunction<T, Serializable> parentKey;
        private SFunction<T, Number> sort;
        private boolean hasSort;
        private Serializable rootKey;
        private BiConsumer<T, List<T>> children;

        public TreeConfigurer() {
            this.name = t -> null;
            this.sort = t -> 0;
            this.hasSort = false;
            this.rootKey = TreeNode.ROOT_KEY;
        }

        public TreeConfigurer<T> key(SFunction<T, Serializable> key) {
            this.key = key;
            return this;
        }

        public TreeConfigurer<T> name(SFunction<T, String> name) {
            this.name = name;
            return this;
        }

        public TreeConfigurer<T> parentKey(SFunction<T, Serializable> parentKey) {
            this.parentKey = parentKey;
            return this;
        }

        public TreeConfigurer<T> sort(SFunction<T, Number> sort) {
            this.sort = sort;
            this.hasSort = this.sort != null;
            return this;
        }

        public TreeConfigurer<T> rootKey(String rootKey) {
            this.rootKey = rootKey;
            return this;
        }

        public TreeConfigurer<T> children(BiConsumer<T, List<T>> children) {
            this.children = children;
            return this;
        }

    }
}
