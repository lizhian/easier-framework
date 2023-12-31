package tydic.user.center.interfaces.eo;

import cn.hutool.core.lang.RegexPool;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseLogicEntity;
import easier.framework.core.plugin.jackson.annotation.ShowDictDetail;
import easier.framework.core.plugin.mybatis.TableCode;
import easier.framework.core.plugin.validation.UpdateGroup;
import easier.framework.core.util.TreeUtil;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import tydic.user.center.interfaces.enums.EnableStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 应用表
 * t_app
 *
 * @author lizhian
 * @date 2023年07月11日
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@Table(value = "t_app", comment = "应用表")
public class App extends BaseLogicEntity {

    @Column(comment = "应用主键")
    @TableId
    @NotBlank(groups = UpdateGroup.class)
    private String appId;

    @Column(comment = "应用编码", notNull = true)
    @TableCode
    @NotBlank
    @Size(min = 2, max = 30)
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "必须以小写字母开头,且只能由小写字母、数字和下划线组成")
    private String appCode;

    @Column(comment = "应用名称")
    @NotBlank
    @Size(min = 2, max = 20)
    @Pattern(regexp = RegexPool.CHINESES, message = "必须由中文组成")
    private String appName;

    @Column(comment = "状态", notNull = true, defaultValue = EnableStatus.defaultValue)
    @NotNull
    @ShowDictDetail
    private EnableStatus status;

    @Column(comment = "排序", notNull = true, defaultValue = TreeUtil.DEFAULT_SORT_STR)
    @NotNull
    private Integer sort;

    @Column(comment = "访问地址")
    private String url;

    @Column(comment = "负责人")
    private String leader;

    @Column(comment = "手机号码")
    @Size(max = 11)
    private String phone;

    @Column(comment = "负责人")
    private String remark;
}
