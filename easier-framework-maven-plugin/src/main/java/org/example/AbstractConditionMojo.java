package org.example;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.Properties;

public abstract class AbstractConditionMojo extends AbstractMojo {
    @Parameter(readonly = true, defaultValue = "${project}")
    protected MavenProject project;

    @Parameter(readonly = true, defaultValue = "${session}")
    protected MavenSession mavenSession;
    /**
     * 构建条件:当存在文件,多个时用,隔开
     */
    @Parameter(property = "conditionOnFile")
    protected String conditionOnFile;

    /**
     * 构建条件:当存在配置且配置不为空,多个时用,隔开
     */
    @Parameter(property = "conditionOnProperty")
    protected String conditionOnProperty;

    /**
     * 构建条件:当启用profile,多个时用,隔开
     */
    @Parameter(property = "conditionOnProfile")
    protected String conditionOnProfile;

    /**
     * 构建条件:当启用Plugin,多个时用,隔开
     */
    @Parameter(property = "conditionOnPlugin")
    protected String conditionOnPlugin;

    protected boolean shouldExecute() {
        List<String> conditionOnFiles = StrUtil.splitTrim(this.conditionOnFile, ",");
        for (String onFile : conditionOnFiles) {
            if (!FileUtil.exist(onFile)) {
                return false;
            }
        }
        List<String> conditionOnProperties = StrUtil.splitTrim(this.conditionOnProperty, ",");
        Properties properties = project.getProperties();
        for (String property : conditionOnProperties) {
            String value = properties.getProperty(property);
            if (StrUtil.isBlank(value)) {
                return false;
            }
        }
        List<String> conditionOnProfiles = StrUtil.splitTrim(this.conditionOnProfile, ",");
        for (String profile : conditionOnProfiles) {
            if (!project.getActiveProfiles().contains(profile)) {
                return false;
            }
        }
        List<Plugin> buildPlugins = project.getBuild().getPlugins();
        /*for (Plugin buildPlugin : buildPlugins) {
             System.out.println("buildPlugin "+buildPlugin.toString());
        }*/
        List<String> conditionOnPlugins = StrUtil.splitTrim(this.conditionOnPlugin, ",");
        //System.out.println("conditionOnPlugins " + conditionOnPlugins.toString());

        for (String plugin : conditionOnPlugins) {
            if (buildPlugins.stream().noneMatch(it -> it.getArtifactId().equals(plugin))) {
                return false;
            }
        }
        return true;
    }
}
