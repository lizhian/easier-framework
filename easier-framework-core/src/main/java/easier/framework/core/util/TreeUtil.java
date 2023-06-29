

/*
 *
 * 此类来自 https://gitee.com/geek_qi/cloud-platform/blob/master/ace-common/src/main/java/com/github/wxiaoqi/security/common/util/TreeUtil.java
 * @ Apache-2.0
 */

package easier.framework.core.util;

import cn.hutool.core.collection.CollUtil;
import easier.framework.core.domain.TreeBuilder;
import easier.framework.core.domain.TreeNode;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@UtilityClass
@Slf4j
public class TreeUtil {
    public static final String ROOT_KEY = "-1";
    public static final Integer DEFAULT_SORT = 100;
    public static final String DEFAULT_SORT_STR = "100";

    public static <T> List<TreeNode<T>> listTreeNode(TreeBuilder<T> treeBuilder, Serializable parentKey, List<T> list) {
        if (StrUtil.isBlankIfStr(parentKey) || CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream()
                .filter(Objects::nonNull)
                //匹配子节点数据
                .filter(data -> {
                    Serializable dataParentKey = treeBuilder.getParentKey().apply(data);
                    return parentKey.equals(dataParentKey);
                })
                //转换节点数据
                .map(data -> {
                    //当前节点key
                    Serializable dataKey = treeBuilder.getKey().apply(data);
                    //递归获取子节点
                    List<TreeNode<T>> children = treeBuilder.listTreeNode(dataKey, list);
                    //构造节点数据
                    return treeBuilder.treeNode(data, children);
                })
                //过滤空数据
                .filter(Objects::nonNull)
                //排序
                .sorted(Comparator.comparingInt(treeBuilder::getSortValue))
                .collect(Collectors.toList());
    }

    public static <T> TreeNode<T> treeNode(TreeBuilder<T> treeBuilder, Serializable key, List<T> list) {
        if (StrUtil.isBlankIfStr(key)) {
            return null;
        }
        //获取当前节点数据
        T data = list.stream()
                .filter(it -> key.equals(treeBuilder.getKey().apply(it)))
                .findAny()
                .orElse(null);
        if (data == null) {
            return null;
        }
        //获取子节点数据
        List<TreeNode<T>> children = treeBuilder.listTreeNode(key, list);
        //构建树结构
        return treeBuilder.treeNode(data, children);
    }

    public static <T> TreeNode<T> treeNode(TreeBuilder<T> treeBuilder, T data, List<TreeNode<T>> children) {
        if (data == null) {
            return null;
        }
        Serializable key = treeBuilder.getKey().apply(data);
        String name = treeBuilder.getName().apply(data);
        Serializable parentKey = treeBuilder.getParentKey().apply(data);
        if (StrUtil.isBlankIfStr(key) || StrUtil.isBlankIfStr(parentKey)) {
            return null;
        }
        TreeNode<T> treeNode = new TreeNode<>();
        treeNode.setKey(key);
        treeNode.setName(name);
        treeNode.setParentKey(parentKey);
        treeNode.setData(data);
        treeNode.setChildren(CollUtil.isNotEmpty(children) ? children : null);
        treeNode.setHasChildren(CollUtil.isNotEmpty(children));
        return treeNode;
    }

    public static <T> TreeNode<T> treeNode(TreeBuilder<T> treeBuilder, T data, boolean hasChildren) {
        TreeNode<T> treeNode = treeBuilder.treeNode(data, null);
        if (treeNode == null) {
            return null;
        }
        treeNode.setChildren(null);
        treeNode.setHasChildren(hasChildren);
        return treeNode;
    }

    public static <T> List<T> asList(TreeBuilder<T> treeBuilder, List<TreeNode<T>> treeNodes) {
        if (CollUtil.isEmpty(treeNodes)) {
            return new ArrayList<>();
        }
        return treeNodes.stream()
                .map(node -> {
                    T data = node.getData();
                    List<T> children = treeBuilder.asList(node.getChildren());
                    treeBuilder.getChildren().accept(data, children);
                    return data;
                })
                .collect(Collectors.toList());
    }
}


