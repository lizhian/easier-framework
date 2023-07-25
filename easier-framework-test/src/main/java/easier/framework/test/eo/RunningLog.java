package easier.framework.test.eo;

import com.tangzc.mpe.autotable.annotation.*;
import com.tangzc.mpe.autotable.annotation.enums.IndexSortTypeEnum;
import lombok.*;
import lombok.experimental.FieldNameConstants;

/**
 * 运行日志表
 * t_running_log
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
@TableIndexes({
        @TableIndex(name = "sorted", fields = {}, indexFields = {
                @IndexField(field = RunningLog.Fields.dtTime, sort = IndexSortTypeEnum.DESC),
                @IndexField(field = RunningLog.Fields.seq, sort = IndexSortTypeEnum.DESC)
        }),
        @TableIndex(name = RunningLog.Fields.traceId, fields = RunningLog.Fields.traceId)
})
@Table(value = "t_running_log", comment = "运行日志表")
public class RunningLog {

    @Column(comment = "记录服务IP")
    private String serverName;

    @Column(comment = "追踪码")
    private String traceId;

    @Column(comment = "应用名")
    private String appName;

    @Column(comment = "应用环境")
    private String env;

    @Column(comment = "方法名")
    private String method;

    @Column(comment = "类名")
    private String className;

    @Column(comment = "线程名")
    private String threadName;

    @Column(comment = "时间,用来排序")
    private Long dtTime;

    @Column(comment = "当dtTime相同时服务端无法正确排序，因此需要增加一个字段保证相同毫秒的日志可正确排序")
    private Long seq;

    @Column(comment = "日志等级")
    private String logLevel;

    private String logType;

    @Column(comment = "时间")
    private String dateTime;

    @Column(comment = "内容", type = "longtext")
    private String content;
}
