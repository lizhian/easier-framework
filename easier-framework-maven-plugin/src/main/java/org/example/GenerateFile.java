package org.example;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import kotlin.text.StringsKt;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "generate-file")
public class GenerateFile extends AbstractConditionMojo {


    /**
     * 文件内容
     */
    @Parameter(property = "content", required = true)
    protected String content;
    /**
     * 文件名称
     */
    @Parameter(property = "fileName", required = true)
    protected String fileName;
    /**
     * 文件输出地址
     */
    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}")
    protected String outputDirectory;


    @Override
    public void execute() {
        if (!super.shouldExecute()) {
            return;
        }
        String file = outputDirectory + "/" + fileName;
        getLog().info("创建文件: " + file);
        String line1 = StrUtil.subBefore(content, "\n", false);
        String lineN = StrUtil.subAfter(content, "\n", false);
        String trimIndent = StringsKt.trimIndent(lineN);
        String trimContent = line1 + "\n" + trimIndent;
        System.out.println(trimContent);
        FileUtil.writeString(trimContent, file, CharsetUtil.UTF_8);
    }
}
