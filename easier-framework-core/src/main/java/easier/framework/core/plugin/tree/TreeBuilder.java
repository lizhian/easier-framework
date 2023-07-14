package easier.framework.core.plugin.tree;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import easier.framework.core.util.TreeUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        return this.getSortValue(node.getData());
    }

    @Nonnull
    public List<TreeNode<T>> build(List<T> list) {
        return TreeUtil.build(this, this.rootKey, list);
    }

    @Nonnull
    public List<TreeNode<T>> build(Serializable parentKey, List<T> list) {
        return TreeUtil.build(this, parentKey, list);
    }

    @Nullable
    public TreeNode<T> buildAsTreeNode(Serializable key, List<T> list) {
        return TreeUtil.buildAsTreeNode(this, key, list);
    }

    @Nullable
    public TreeNode<T> createTreeNode(T data, List<TreeNode<T>> children) {
        return TreeUtil.createTreeNode(this, data, children);
    }

    @Nonnull
    public List<T> asList(List<TreeNode<T>> treeNodes) {
        return TreeUtil.asList(this, treeNodes);
    }


    /**
     * 匹配过滤
     * 一旦命中,当前节点和子节点都保留
     *
     * @param tree  树
     * @param match 匹配
     * @return {@link List}<{@link TreeNode}<{@link T}>>
     */
    @Nonnull
    public List<TreeNode<T>> include(List<TreeNode<T>> tree, Predicate<T> match) {
        if (tree == null) {
            return new ArrayList<>();
        }
        return tree.stream()
                .map(it -> {
                    T data = it.getData();
                    if (data == null) {
                        return null;
                    }
                    //当前节点匹配,返回整条数据
                    boolean isMatched = match.test(data);
                    if (isMatched) {
                        return it;
                    }
                    List<TreeNode<T>> matchedChildren = this.include(it.getChildren(), match);
                    if (CollUtil.isEmpty(matchedChildren)) {
                        return null;
                    }
                    it.setChildren(matchedChildren);
                    it.setHasChildren(CollUtil.isNotEmpty(matchedChildren));
                    return it;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    /**
     * 排除过滤
     * 一旦符合,当前节点和子节点都不要
     *
     * @param tree    树
     * @param exclude 匹配
     * @return {@link List}<{@link TreeNode}<{@link T}>>
     */
    @Nonnull
    public List<TreeNode<T>> exclude(List<TreeNode<T>> tree, Predicate<T> exclude) {
        if (tree == null) {
            return new ArrayList<>();
        }
        return tree.stream()
                .map(it -> {
                    T data = it.getData();
                    if (data == null) {
                        return null;
                    }
                    //当前节点匹配,返回整条数据
                    boolean isExclude = exclude.test(data);
                    if (isExclude) {
                        return null;
                    }
                    List<TreeNode<T>> excludedChildren = this.exclude(it.getChildren(), exclude);
                    it.setChildren(excludedChildren);
                    it.setHasChildren(CollUtil.isNotEmpty(excludedChildren));
                    return it;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
