package org.example;


import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.LineHandler;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

@Mojo(name = "exec")
public class Exec extends AbstractConditionMojo {

    @Parameter(property = "commands", required = true)
    protected List<String> commands;

    @Parameter(property = "workingDirectory", defaultValue = "${project.build.directory}")
    protected File workingDirectory;

    /**
     * 处理命令，多行命令原样返回，单行命令拆分处理
     *
     * @param cmds 命令
     * @return 处理后的命令
     */
    private static String[] handleCmds(String... cmds) {
        if (ArrayUtil.isEmpty(cmds)) {
            throw new NullPointerException("Command is empty !");
        }

        // 单条命令的情况
        if (1 == cmds.length) {
            final String cmd = cmds[0];
            if (StrUtil.isBlank(cmd)) {
                throw new NullPointerException("Command is blank !");
            }
            cmds = cmdSplit(cmd);
        }
        return cmds;
    }

    /**
     * 命令分割，使用空格分割，考虑双引号和单引号的情况
     *
     * @param cmd 命令，如 git commit -m 'test commit'
     * @return 分割后的命令
     */
    private static String[] cmdSplit(String cmd) {
        final List<String> cmds = new ArrayList<>();

        final int length = cmd.length();
        final Stack<Character> stack = new Stack<>();
        boolean inWrap = false;
        final StrBuilder cache = StrUtil.strBuilder();

        char c;
        for (int i = 0; i < length; i++) {
            c = cmd.charAt(i);
            switch (c) {
                case CharUtil.SINGLE_QUOTE:
                case CharUtil.DOUBLE_QUOTES:
                    if (inWrap) {
                        if (c == stack.peek()) {
                            // 结束包装
                            stack.pop();
                            inWrap = false;
                        }
                        cache.append(c);
                    } else {
                        stack.push(c);
                        cache.append(c);
                        inWrap = true;
                    }
                    break;
                case CharUtil.SPACE:
                    if (inWrap) {
                        // 处于包装内
                        cache.append(c);
                    } else {
                        cmds.add(cache.toString());
                        cache.reset();
                    }
                    break;
                default:
                    cache.append(c);
                    break;
            }
        }

        if (cache.hasContent()) {
            cmds.add(cache.toString());
        }

        return cmds.toArray(new String[0]);
    }

    @Override
    public void execute() {
        if (!super.shouldExecute()) {
            return;
        }
        if (!this.workingDirectory.exists()) {
            this.getLog().info("创建工作目录 '" + this.workingDirectory.getAbsolutePath() + "'.");
            if (!this.workingDirectory.mkdirs()) {
                throw new RuntimeException("无法创建工作目录: '" + this.workingDirectory.getAbsolutePath() + "'");
            }
        }
        for (String command : this.commands) {
            if (StrUtil.isBlank(command)) {
                continue;
            }
            String[] commandArgs = Arrays.stream(handleCmds(command))
                    .filter(StrUtil::isNotBlank)
                    .map(String::trim)
                    .filter(StrUtil::isNotBlank)
                    .toArray(String[]::new);
            if (commandArgs.length < 1) {
                continue;
            }
            ProcessBuilder processBuilder = new ProcessBuilder(commandArgs)
                    .directory(this.workingDirectory)
                    .redirectErrorStream(true);
            this.getLog().info("开始执行命令: " + Arrays.toString(commandArgs));
            InputStream in = null;
            Process process = null;
            int exitValue = 0;
            AtomicReference<String> lastLine = new AtomicReference<>();
            try {
                process = processBuilder.start();
                in = process.getInputStream();
                LineHandler lineHandler = line -> {
                    System.out.println(line);
                    lastLine.set(line);
                };
                IoUtil.readUtf8Lines(in, lineHandler);
                process.waitFor();
            } catch (Exception e) {
                this.getLog().info("执行命令异常:" + e.getMessage());
                throw new RuntimeException(e);
            } finally {
                if (in != null) {
                    IoUtil.close(in);
                }
                if (process != null) {
                    process.destroy();
                    try {
                        exitValue = process.exitValue();
                    } catch (Exception ignored) {
                        exitValue = 1;
                    }
                }
            }
            if (exitValue != 0) {
                throw new RuntimeException("执行命令失败:" + lastLine.get());
            }
        }

    }
}
