package easier.framework.core.domain;

import easier.framework.core.plugin.jackson.annotation.Alias;
import easier.framework.core.plugin.jackson.annotation.AliasId;
import easier.framework.core.plugin.jackson.annotation.BoolReverse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 树节点
 */
@Data
public class TreeNode<T> implements Serializable {
    @AliasId
    @Schema(description = "树节点主键")
    private Serializable key;

    @Schema(description = "树节点名称")
    private String name;

    @Schema(description = "树节点数据")
    private T data;

    @Alias("parentId")
    @Schema(description = "树父节点主键")
    private Serializable parentKey;

    @BoolReverse("isLeafNode")
    @Schema(description = "是否有子节点")
    private Boolean hasChildren;

    @Schema(description = "子节点集合")
    private List<TreeNode<T>> children;

}
