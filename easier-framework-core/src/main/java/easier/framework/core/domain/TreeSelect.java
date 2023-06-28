package easier.framework.core.domain;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import easier.framework.core.plugin.jackson.annotation.Alias;
import easier.framework.core.plugin.jackson.annotation.AliasId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

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


    @Schema(description = "树节点主键")
    @AliasId
    private String key;

    @Schema(description = "树节点名称")
    @Alias("label")
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
