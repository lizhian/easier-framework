package tydic.framework.core.domain;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.Getter;
import tydic.framework.core.util.StrUtil;
import tydic.framework.core.util.TreeUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Getter
public class TreeBuilder<T> {
    /**
     * 树节点主键
     */
    private SFunction<T, String> keyFunction;
    /**
     * 树节点名称
     */
    private SFunction<T, String> nameFunction;
    /**
     * 树父节点主键
     */
    private SFunction<T, String> parentKeyFunction;
    /**
     * 树排序
     * 正序
     */
    private SFunction<T, Number> sortFunction;

    private boolean hasSortColumn;
    /**
     * 树跟节点值
     */
    private String rootKey;

    public TreeBuilder() {
        this.nameFunction = t -> null;
        this.sortFunction = t -> 0;
        this.hasSortColumn = false;
        this.rootKey = TreeNode.ROOT_KEY;
    }

    public TreeBuilder<T> key(SFunction<T, String> keyFunction) {
        this.keyFunction = keyFunction;
        return this;
    }

    public TreeBuilder<T> name(SFunction<T, String> nameFunction) {
        this.nameFunction = nameFunction;
        return this;
    }

    public TreeBuilder<T> parentKey(SFunction<T, String> parentKeyFunction) {
        this.parentKeyFunction = parentKeyFunction;
        return this;
    }

    public TreeBuilder<T> sort(SFunction<T, Number> sortFunction) {
        this.sortFunction = sortFunction;
        this.hasSortColumn = this.sortFunction != null;
        return this;
    }

    public TreeBuilder<T> rootKey(String rootKey) {
        this.rootKey = rootKey;
        return this;
    }

    public boolean hasSortColumn() {
        return false;
    }

    public int getSort(T t) {
        try {
            return this.sortFunction.apply(t).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getSort(TreeNode<T> t) {
        try {
            return this.sortFunction.apply(t.getNodeData()).intValue();
        } catch (Exception e) {
            return 0;
        }
    }


    public boolean enable() {
        return this.keyFunction != null
                && this.nameFunction != null
                && this.parentKeyFunction != null
                && this.sortFunction != null
                && StrUtil.isNotBlank(this.rootKey)
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
    public List<TreeNode<T>> build(List<T> all, String key) {
        return TreeUtil.build(this, all, key);
    }

    @Nullable
    public TreeNode<T> buildNode(List<T> all, String rootKey) {
        if (StrUtil.isBlank(rootKey)) {
            return null;
        }
        //获取当前节点数据
        T nodeData = all.stream()
                        .filter(it -> rootKey.equals(this.keyFunction.apply(it)))
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
        String key = this.keyFunction.apply(nodeData);
        String name = this.nameFunction.apply(nodeData);
        String parentKey = this.parentKeyFunction.apply(nodeData);
        boolean hasChildren = CollUtil.isNotEmpty(children);
        if (StrUtil.isBlank(key) || StrUtil.isBlank(parentKey)) {
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
}
