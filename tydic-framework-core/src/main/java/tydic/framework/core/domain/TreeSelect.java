package tydic.framework.core.domain;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import tydic.framework.core.plugin.jackson.annotation.JsonID;
import tydic.framework.core.plugin.jackson.annotation.JsonSerializeAlias;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 树节点
 * 简单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
public class TreeSelect implements Serializable {


    @ApiModelProperty("树节点主键")
    @JsonID
    private String key;

    @ApiModelProperty("树节点名称")
    @JsonSerializeAlias("label")
    private String name;

    /**
     * 子节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public <T> TreeSelect(TreeNode<T> treeNode) {
        //this.key = treeNode.getKey();
        this.name = treeNode.getName();
        if (CollUtil.isNotEmpty(treeNode.getChildren())) {
            this.children = treeNode.getChildren()
                                    .stream()
                                    .map(TreeSelect::new)
                                    .collect(Collectors.toList());

        }
    }


}
