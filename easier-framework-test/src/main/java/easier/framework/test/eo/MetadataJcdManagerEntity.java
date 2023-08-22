package easier.framework.test.eo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * jcd文件管理对象 metadata_jcd_manager
 *
 * @author ruoyi
 * @date 2023-06-06
 */

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder(toBuilder = true)
@TableName("metadata_jcd_manager")
public class MetadataJcdManagerEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @Schema(description = "版本")
    private String jcdVersion;

    /**
     * 演习编号
     */
    @Schema(description = "演习编号")
    private String exerciseNo;

    /**
     * 演习名称
     */
    @Schema(description = "演习名称")
    private String exerciseName;

    /**
     * 演习开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "演习开始时间")
    private Date exerciseStart;

    /**
     * 演习结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "演习结束时间")
    private Date exerciseEnd;

    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    private String fileName;

    /**
     * 文件名称
     */
    @Schema(description = "文件大小")
    private String fileSize;

    /**
     * 文件地址
     */
    @Schema(description = "文件地址")
    private String fileUrl;

    /**
     * 文件内容
     */
    @Schema(description = "文件内容")
    private String fileContent;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "上传时间")
    private Date uploadTime;

    @Schema(description = "上传人")
    private String uploadBy;

    /**
     * 是否生成 0是1否
     */
    @Schema(description = "是否生成表 0是1否")
    private Integer flag;

    @Schema(description = "数据源id")
    private String sourceId;

    @Schema(description = "资源id")
    private String resourceId;


}
