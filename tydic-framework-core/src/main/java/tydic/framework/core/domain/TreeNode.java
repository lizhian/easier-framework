package tydic.framework.core.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import tydic.framework.core.plugin.jackson.annotation.JsonID;
import tydic.framework.core.plugin.jackson.annotation.JsonReverse;
import tydic.framework.core.plugin.jackson.annotation.JsonSerializeAlias;

import java.io.Serializable;
import java.util.List;

/**
 * 树节点
 */
@Data
public class TreeNode<T> implements Serializable {

    public static final String ROOT_KEY = "-1";

    /**
     * 默认排序
     */
    public static final Integer DEFAULT_SORT = 100;
    public static final String DEFAULT_SORT_STR = "100";


    @JsonID
    @ApiModelProperty("树节点主键")
    private String key;

    @ApiModelProperty("树节点名称")
    private String name;

    @ApiModelProperty("树节点节点数据")
    private T nodeData;

    @JsonSerializeAlias("parentId")
    @ApiModelProperty("树父节点主键")
    private String parentKey;

    @JsonReverse("isLeaf")
    @ApiModelProperty("是否有子节点")
    private Boolean hasChildren;

    @ApiModelProperty("子节点集合")
    private List<TreeNode<T>> children;

}
