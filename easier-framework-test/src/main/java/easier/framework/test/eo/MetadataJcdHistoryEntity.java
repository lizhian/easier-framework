package easier.framework.test.eo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * jcd文件管理对象 metadata_jcd_history
 *
 * @author ruoyi
 * @date 2023-06-06
 */
@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("metadata_jcd_history")
public class MetadataJcdHistoryEntity {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @Schema(description = "版本")
    private String jcdVersion;

    @Schema(description = "管理jcd主表")
    private String jcdId;


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


}
