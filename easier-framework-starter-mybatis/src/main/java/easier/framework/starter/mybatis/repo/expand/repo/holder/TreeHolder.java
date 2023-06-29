package easier.framework.starter.mybatis.repo.expand.repo.holder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.tangzc.mpe.bind.Binder;
import easier.framework.core.domain.TreeBuilder;
import easier.framework.core.domain.TreeNode;
import easier.framework.starter.mybatis.repo.Repo;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TreeHolder<T> {
    private final Repo<T> repo;
    private final TreeBuilder<T> treeBuilder;
    private boolean bind;
    private List<SFunction<T, ?>> bindFields;
    /*private boolean withChildren;
    private boolean childrenNullToEmpty;*/

    @SafeVarargs
    public final TreeHolder<T> bind(SFunction<T, ?>... bindFields) {
        this.bind = true;
        this.bindFields = CollUtil.newArrayList(bindFields);
        return this;
    }

    @Nonnull
    private List<TreeNode<T>> after(List<TreeNode<T>> treeNodes) {
        if (CollUtil.isEmpty(treeNodes)) {
            return treeNodes;
        }
        treeNodes.forEach(this::after);
        return treeNodes;
    }

    @Nullable
    private TreeNode<T> after(TreeNode<T> treeNode) {
        if (treeNode == null) {
            return null;
        }
        //childrenNullToEmpty
        if (treeNode.getChildren() == null) {
            treeNode.setChildren(new ArrayList<>());
        } else {
            treeNode.getChildren().forEach(this::after);
        }
        if (this.bind) {
            Binder.bindOn(treeNode.getData(), this.bindFields);
        }
        return treeNode;
    }

    /**
     * 查询是否包含子节点
     */
    public boolean hasChildren(Serializable key) {
        if (StrUtil.isBlankIfStr(key)) {
            return false;
        }
        return this.repo.newQuery()
                .eq(this.treeBuilder.getParentKey(), key)
                .exists();
    }

    public List<T> listChildren(Serializable key) {
        if (StrUtil.isBlankIfStr(key)) {
            return new ArrayList<>();
        }
        return this.repo.newQuery()
                .eq(this.treeBuilder.getParentKey(), key)
                .list();
    }

    /**
     * 查询完整的树
     */
    @Nonnull
    public List<TreeNode<T>> listTreeNode() {
        List<T> listAll = this.repo.listAll();
        List<TreeNode<T>> treeNodes = this.treeBuilder.listTreeNode(listAll);
        return this.after(treeNodes);
    }

    /**
     * 查询子节点数据
     */
    @Nonnull
    public List<TreeNode<T>> listTreeNode(String parentKey) {
        if (StrUtil.isBlank(parentKey)) {
            return new ArrayList<>();
        }
        //包含子节点,全表查询一次效率>嵌套查询N次效率
        List<T> list = this.repo.listAll();
        //构建树结构
        List<TreeNode<T>> treeNodes = this.treeBuilder.listTreeNode(parentKey, list);
        //后置处理
        return this.after(treeNodes);
    }

    /**
     * 查询子节点数据,懒加载方式
     */
    @Nonnull
    public List<TreeNode<T>> listTreeNodeLazy(String parentKey) {
        if (StrUtil.isBlank(parentKey)) {
            return new ArrayList<>();
        }
        //包含子节点,全表查询一次效率>嵌套查询N次效率
        List<T> list = this.listChildren(parentKey);
        //构建树结构
        List<TreeNode<T>> treeNodes = this.treeBuilder.listTreeNode(parentKey, list);
        for (TreeNode<T> treeNode : treeNodes) {
            treeNode.setHasChildren(true);
            treeNode.setChildren(null);
        }
        //后置处理
        return this.after(treeNodes);
    }

    /**
     * 查询节点数据
     */
    @Nullable
    public TreeNode<T> treeNode(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        //包含子节点,全表查询一次效率>嵌套查询N次效率
        List<T> all = this.repo.listAll();
        TreeNode<T> rootNode = this.treeBuilder.treeNode(all, key);
        //后置处理
        return this.after(rootNode);
    }

    /**
     * 查询节点数据,懒加载方式
     */
    @Nullable
    public TreeNode<T> treeNodeLazy(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        T data = this.repo.getById(key);
        if (data == null) {
            return null;
        }
        List<T> listChildren = this.listChildren(key);
        //构建树结构
        List<TreeNode<T>> children = this.treeBuilder.listTreeNode(key, listChildren);
        for (TreeNode<T> treeNode : children) {
            treeNode.setHasChildren(true);
            treeNode.setChildren(null);
        }
        TreeNode<T> rootNode = this.treeBuilder.treeNode(data, children);
        //后置处理
        return this.after(rootNode);
    }
}