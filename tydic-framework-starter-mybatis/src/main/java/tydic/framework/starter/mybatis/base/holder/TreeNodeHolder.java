package tydic.framework.starter.mybatis.base.holder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.tangzc.mpe.bind.Binder;
import lombok.RequiredArgsConstructor;
import tydic.framework.core.domain.TreeBuilder;
import tydic.framework.core.domain.TreeNode;
import tydic.framework.starter.mybatis.base.BaseRepo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TreeNodeHolder<T> {
    private final BaseRepo<T> repository;
    private final TreeBuilder<T> treeBuilder;
    private boolean bind;
    private List<SFunction<T, ?>> bindFields;
    private boolean withChildren;
    private boolean childrenNullToEmpty;

    @SafeVarargs
    public final TreeNodeHolder<T> bind(SFunction<T, ?>... bindFields) {
        this.bind = true;
        this.bindFields = CollUtil.newArrayList(bindFields);
        return this.withChildren(true);
    }

    /**
     * 查询子节点
     */
    public TreeNodeHolder<T> withChildren() {
        this.withChildren = true;
        return this;
    }

    /**
     * 查询子节点
     */
    public TreeNodeHolder<T> withChildren(boolean withChildren) {
        this.withChildren = withChildren;
        return this;
    }

    /**
     * 子节点使用非null空集合
     */
    public TreeNodeHolder<T> childrenNullToEmpty() {
        this.childrenNullToEmpty = true;
        return this;
    }

    /**
     * 子节点使用非null空集合
     */
    public TreeNodeHolder<T> childrenNullToEmpty(boolean childrenNullToEmpty) {
        this.childrenNullToEmpty = childrenNullToEmpty;
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
        if (this.childrenNullToEmpty && treeNode.getChildren() == null) {
            treeNode.setChildren(new ArrayList<>());
        }
        //bind
        if (this.bind && CollUtil.isEmpty(this.bindFields)) {
            Binder.bind(treeNode.getNodeData());
        }
        if (this.bind && CollUtil.isNotEmpty(this.bindFields)) {
            Binder.bindOn(treeNode.getNodeData(), this.bindFields);
        }
        return treeNode;
    }

    /**
     * 查询完整的树
     */
    @Nonnull
    public List<TreeNode<T>> tree() {
        List<T> all = this.repository.all();
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
        SFunction<T, String> keyFunction = this.treeBuilder.getKeyFunction();
        if (this.withChildren) {
            //包含子节点,全表查询一次效率>嵌套查询N次效率
            List<T> all = this.repository.all();
            TreeNode<T> rootNode = this.treeBuilder.buildNode(all, key);
            //后置处理
            return this.after(rootNode);
        }
        //查询当前节点数据
        T entity = this.repository.newQuery().anyBy(keyFunction, key);
        //查询当前节点是否包含子节点
        boolean hasChildren = this.repository.hasChildren(key);
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
            List<T> list = this.repository.all();
            //构建树结构
            List<TreeNode<T>> treeNodes = this.treeBuilder.build(list, key);
            //后置处理
            return this.after(treeNodes);
        }

        //获取子节点数据
        List<T> list = this.repository.getChildren(key);
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        SFunction<T, String> keyFunction = this.treeBuilder.getKeyFunction();
        SFunction<T, String> parentKeyFunction = this.treeBuilder.getParentKeyFunction();
        //获取子节点的树主键集合
        List<String> childrenKeys = list.stream()
                                        .map(keyFunction)
                                        .toList();
        //拥有子节点的后代节点
        Set<String> hasChildrenKeys = this.repository.newQuery()
                                                     .select(parentKeyFunction)
                                                     .in(parentKeyFunction, childrenKeys)
                                                     .list()
                                                     .stream()
                                                     .map(parentKeyFunction)
                                                     .filter(Objects::nonNull)
                                                     .collect(Collectors.toSet());
        List<TreeNode<T>> treeNodes = list.stream()
                                          .map(entity -> {
                                              String thisKey = keyFunction.apply(entity);
                                              //判断节点是否包含后代节点
                                              boolean hasChildren = hasChildrenKeys.contains(thisKey);
                                              return this.treeBuilder.createTreeNode(entity, hasChildren);
                                          })
                                          .collect(Collectors.toList());
        //后置处理
        return this.after(treeNodes);
    }


}