package easier.framework.test.eo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import easier.framework.core.domain.BaseEntity;
import easier.framework.core.plugin.validation.UpdateGroup;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 应用配置表
 * t_application_configuration
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
@Table(value = "t_application_configuration", comment = "应用配置表")
public class ApplicationConfiguration extends BaseEntity {


    @Column(comment = "配置主键")
    @TableId
    @NotBlank(groups = UpdateGroup.class)
    private String configurationId;

    @Column(comment = "配置编码")
    @NotBlank
    private String configurationCode;

    @Column(comment = "配置注释")
    @NotBlank
    private String configurationComment;

    @Column(comment = "环境")
    @NotBlank
    private String configurationProfile;

    @Column(comment = "分组")
    @NotBlank
    private String configurationGroup;

    @Column(comment = "内容", type = "longtext")
    @NotBlank
    private String configurationContent;

    @Column(comment = "是否是历史文件")
    @NotNull
    private Boolean history;
}
