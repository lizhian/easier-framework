package easier.framework.test.eo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseEntity;
import easier.framework.core.plugin.dict.DictItemDetail;
import easier.framework.core.plugin.dict.ShowDictDetail;
import easier.framework.core.plugin.validation.UpdateGroup;
import easier.framework.core.util.TreeUtil;
import easier.framework.test.enums.EnableStatus;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@Table(value = "sys_dict_item", comment = "字典项表")
public class SysDictItem extends BaseEntity {

    @Column(comment = "字典项主键")
    @TableId
    @NotBlank(groups = UpdateGroup.class)
    private String dictItemId;

    @Column(comment = "所属字典编码", notNull = true)
    @NotBlank
    private String dictCode;

    @Column(comment = "字典项键值", notNull = true)
    @NotBlank
    @Size(max = 100)
    private String value;

    @Column(comment = "字典项标签", notNull = true)
    @NotBlank
    @Size(max = 100)
    private String label;

    @Column(comment = "排序", notNull = true, defaultValue = TreeUtil.DEFAULT_SORT_STR)
    @NotNull
    private Integer sort;

    @Column(comment = "字典项样式")
    @Size(max = 100)
    @ShowDictDetail("dict_item_style")
    private String style;

    @Column(comment = "字典项状态", notNull = true, defaultValue = EnableStatus.defaultValue)
    @NotNull
    @ShowDictDetail
    private EnableStatus status;

    @Column(comment = "备注")
    private String remark;

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
