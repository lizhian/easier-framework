package org.example;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.Properties;

@Mojo(name = "build-time-format", defaultPhase = LifecyclePhase.PACKAGE)
public class BuildTimeFormat extends AbstractConditionMojo {


    @Parameter(readonly = true, defaultValue = "${project}")
    protected MavenProject project;

    @Parameter(readonly = true, defaultValue = "${session}")
    protected MavenSession mavenSession;

    @Override
    public void execute() {
        Properties properties = project.getProperties();
        DateTime buildTime = DateTime.of(mavenSession.getStartTime());
        getLog().info(StrUtil.format("set ${buildTime} = {}", buildTime));
        List<String> formats = CollUtil.newArrayList("yyyy", "MM", "dd", "HH", "mm", "ss");
        for (String format : formats) {
            String value = buildTime.toString(format);
            properties.setProperty(format, value);
            getLog().info(StrUtil.format("set ${{}} = {}", format, value));
        }
    }


}
