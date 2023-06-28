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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TreeHolder<T> {
    private final Repo<T> repo;
    private final TreeBuilder<T> treeBuilder;
    private boolean bind;
    private List<SFunction<T, ?>> bindFields;
    private boolean withChildren;
    private boolean childrenNullToEmpty;

    @SafeVarargs
    public final TreeHolder<T> bind(SFunction<T, ?>... bindFields) {
        this.bind = true;
        this.bindFields = CollUtil.newArrayList(bindFields);
        return this;
    }

    /**
     * 查询子节点
     */
    public TreeHolder<T> withChildren() {
        return this.withChildren(true);
    }

    /**
     * 查询子节点
     */
    public TreeHolder<T> withChildren(boolean withChildren) {
        this.withChildren = withChildren;
        return this;
    }

    /**
     * 子节点使用非null空集合
     */
    public TreeHolder<T> childrenNullToEmpty() {
        return this.childrenNullToEmpty(true);
    }

    /**
     * 子节点使用非null空集合
     */
    public TreeHolder<T> childrenNullToEmpty(boolean childrenNullToEmpty) {
        this.childrenNullToEmpty = childrenNullToEmpty;
        return this;
    }

    /**
     * 查询是否包含子节点
     */
    private boolean hasChildren(Serializable key) {
        if (StrUtil.isBlankIfStr(key)) {
            return false;
        }
        return this.repo.newQuery()
                .eq(this.treeBuilder.getParentKey(), key)
                .exists();
    }

    /**
     * 查询完整的树
     */
    @Nonnull
    public List<TreeNode<T>> tree() {
        List<T> all = this.repo.listAll();
        List<TreeNode<T>> treeNodes = this.treeBuilder.build(all);
        return this.after(treeNodes);
    }

    /**
     * 查询节点数据
     */
    @Nullable
    public TreeNode<T> getNode(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        if (this.withChildren) {
            //包含子节点,全表查询一次效率>嵌套查询N次效率
            List<T> all = this.repo.listAll();
            TreeNode<T> rootNode = this.treeBuilder.buildNode(all, key);
            //后置处理
            return this.after(rootNode);
        }
        //查询当前节点数据
        T entity = this.repo.newQuery()
                .eq(this.treeBuilder.getKey(), key)
                .any();
        //查询当前节点是否包含子节点
        boolean hasChildren = this.hasChildren(key);
        //构建树结构
        TreeNode<T> treeNode = this.treeBuilder.createTreeNode(entity, hasChildren);
        //后置处理
        return this.after(treeNode);
    }


    /**
     * 查询子节点数据
     */
    @Nonnull
    public List<TreeNode<T>> getChildren(String key) {
        if (this.withChildren) {
            //包含子节点,全表查询一次效率>嵌套查询N次效率
            List<T> list = this.repo.listAll();
            //构建树结构
            List<TreeNode<T>> treeNodes = this.treeBuilder.build(list, key);
            //后置处理
            return this.after(treeNodes);
        }

        //获取子节点数据
        //List<T> list = this.repo.getChildren(key); todo
        List<T> list = new ArrayList<>();
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        SFunction<T, Serializable> keyFunction = this.treeBuilder.getKey();
        SFunction<T, Serializable> parentKeyFunction = this.treeBuilder.getParentKey();
        //获取子节点的树主键集合
        List<Serializable> childrenKeys = list.stream()
                .map(keyFunction)
                .collect(Collectors.toList());
        //拥有子节点的后代节点
        Set<Serializable> hasChildrenKeys = this.repo.newQuery()
                .select(parentKeyFunction)
                .in(parentKeyFunction, childrenKeys)
                .list()
                .stream()
                .map(parentKeyFunction)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<TreeNode<T>> treeNodes = list.stream()
                .map(entity -> {
                    Serializable thisKey = keyFunction.apply(entity);
                    //判断节点是否包含后代节点
                    boolean hasChildren = hasChildrenKeys.contains(thisKey);
                    return this.treeBuilder.createTreeNode(entity, hasChildren);
                })
                .collect(Collectors.toList());
        //后置处理
        return this.after(treeNodes);
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
        if (this.childrenNullToEmpty && treeNode.getChildren() == null) {
            treeNode.setChildren(new ArrayList<>());
        }
        if (this.bind) {
            Binder.bindOn(treeNode.getNodeData(), this.bindFields);
        }
        return treeNode;
    }


}