package easier.framework.test.eo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseEntity;
import easier.framework.core.plugin.dict.DictItemDetail;
import easier.framework.core.plugin.jackson.annotation.ShowDictDetail;
import easier.framework.core.plugin.validation.UpdateGroup;
import easier.framework.core.util.TreeUtil;
import easier.framework.test.enums.EnableStatus;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * 字典项
 * t_dict_item
 *
 * @author lizhian
 * @date 2023年07月05日
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@Table(value = "t_dict_item", comment = "字典项表")
public class DictItem extends BaseEntity {

    /**
     * 字典项主键
     */
    @Column(comment = "字典项主键")
    @TableId
    @NotBlank(groups = UpdateGroup.class)
    private String dictItemId;

    /**
     * 所属字典编码
     */
    @Column(comment = "所属字典编码", notNull = true)
    @NotBlank
    private String dictCode;

    /**
     * 值
     */
    @Column(comment = "字典项键值", notNull = true)
    @NotBlank
    @Size(max = 100)
    private String value;

    /**
     * 标签
     */
    @Column(comment = "字典项标签", notNull = true)
    @NotBlank
    @Size(max = 100)
    private String label;

    /**
     * 排序
     */
    @Column(comment = "排序", notNull = true, defaultValue = TreeUtil.DEFAULT_SORT_STR)
    @NotNull
    private Integer sort;

    /**
     * 样式
     */
    @Column(comment = "样式")
    @Size(max = 100)
    @ShowDictDetail("dict_item_style")
    private String style;

    /**
     * 状态
     */
    @Column(comment = "字典项状态", notNull = true, defaultValue = EnableStatus.defaultValue)
    @NotNull
    @ShowDictDetail
    private EnableStatus status;

    /**
     * 备注
     */
    @Column(comment = "备注")
    private String remark;

    /**
     * 到字典项目详情
     *
     * @return {@link DictItemDetail}
     */
    public DictItemDetail toDictItemDetail() {
        return DictItemDetail.builder()
                .value(this.value)
                .label(this.label)
                .sort(this.sort)
                .style(this.style)
                .enable(EnableStatus.isEnable(this.status))
                .style(this.style)
                .remark(this.remark)
                .build();
    }
}
