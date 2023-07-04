package easier.framework.test.eo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.bind.metadata.annotation.BindEntity;
import com.tangzc.mpe.bind.metadata.annotation.JoinCondition;
import com.tangzc.mpe.bind.metadata.annotation.JoinOrderBy;
import easier.framework.core.domain.BaseEntity;
import easier.framework.core.plugin.dict.DictDetail;
import easier.framework.core.plugin.dict.DictItemDetail;
import easier.framework.core.plugin.dict.ShowDictDetail;
import easier.framework.core.plugin.mybatis.TableCode;
import easier.framework.core.plugin.validation.UpdateGroup;
import easier.framework.test.enums.DictType;
import easier.framework.test.enums.EnableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典类型表 sys_dict_type
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@Table(value = "sys_dict", comment = "字典表")
public class SysDict extends BaseEntity {

    @Column(comment = "字典主键")
    @TableId
    @NotBlank(groups = UpdateGroup.class)
    private String dictId;

    @Column(comment = "字典编码", notNull = true)
    @TableCode
    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^[a-z][a-z0-9_:]*$", message = "必须以字母开头,且只能由小写字母/数字/下滑线/冒号组成")
    private String dictCode;

    @Column(comment = "字典名称", notNull = true)
    @NotBlank
    @Size(max = 100)
    private String dictName;

    @Column(comment = "字典类型", notNull = true)
    @ShowDictDetail
    private DictType dictType;

    @Column(comment = "字典状态", notNull = true, defaultValue = EnableStatus.defaultValue)
    @NotNull
    @ShowDictDetail
    private EnableStatus status;

    @Column(comment = "字典样式")
    @ShowDictDetail("dict_style")
    private String style;

    @Column(comment = "备注")
    private String remark;

    @Schema(description = "字典项列表", hidden = true)
    @BindEntity(
            conditions = @JoinCondition(
                    selfField = SysDict.Fields.dictCode
                    , joinField = SysDictItem.Fields.dictCode
            )
            , orderBy = @JoinOrderBy(field = SysDictItem.Fields.sort)
    )
    private List<SysDictItem> items;

    public DictDetail toDictDetail() {
        List<DictItemDetail> itemDetails = null;
        if (items != null) {
            itemDetails = items.stream()
                    .map(SysDictItem::toDictItemDetail)
                    .collect(Collectors.toList());
        }
        return DictDetail.builder()
                .selected(null)
                .code(this.dictCode)
                .name(this.dictName)
                .enable(EnableStatus.isEnable(this.status))
                .style(this.style)
                .remark(this.remark)
                .items(itemDetails)
                .build();
    }
}
