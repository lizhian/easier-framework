

/*
 *
 * 此类来自 https://gitee.com/geek_qi/cloud-platform/blob/master/ace-common/src/main/java/com/github/wxiaoqi/security/common/util/TreeUtil.java
 * @ Apache-2.0
 */

package tydic.framework.core.util;

import cn.hutool.core.collection.CollUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import tydic.framework.core.domain.TreeBuilder;
import tydic.framework.core.domain.TreeNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@UtilityClass
@Slf4j
public class TreeUtil {

    public static <T> List<TreeNode<T>> build(TreeBuilder<T> treeBuilder, List<T> all, Serializable rootKey) {
        if (StrUtil.isBlankIfStr(rootKey) || CollUtil.isEmpty(all) || treeBuilder.disable()) {
            return new ArrayList<>();
        }
        return all.stream()
                .filter(Objects::nonNull)
                //匹配
                .filter(nodeData -> TreeUtil.isChildren(treeBuilder, rootKey, nodeData))
                //转换节点数据
                .map(nodeData -> TreeUtil.toTreeNode(treeBuilder, nodeData, all))
                //过滤空数据
                .filter(Objects::nonNull)
                //排序
                //.sorted(Comparator.comparingInt(treeBuilder::getSort))
                .collect(Collectors.toList());
    }

    private static <T> boolean isChildren(TreeBuilder<T> treeBuilder, Serializable key, T nodeData) {
        Serializable parentKey = treeBuilder.getParentKey().apply(nodeData);
        return key.equals(parentKey);
    }

    private static <T> TreeNode<T> toTreeNode(TreeBuilder<T> treeBuilder, T nodeData, List<T> all) {
        //当前节点key
        Serializable key = treeBuilder.getKey().apply(nodeData);
        //递归获取子节点
        List<TreeNode<T>> children = treeBuilder.build(all, key);
        //构造节点数据
        return treeBuilder.createTreeNode(nodeData, children);
    }
}
