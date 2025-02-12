package com.swang.codesandbox;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.swang.codesandbox.model.ExecuteCodeRequest;
import com.swang.codesandbox.model.ExecuteCodeResponse;
import com.swang.codesandbox.model.ExecuteMessage;
import com.swang.codesandbox.model.JudgeInfo;
import com.swang.codesandbox.utils.ProcessUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class JavaDockerCodeSandbox extends JavaCodeSandboxTemplate {
    private static final long TIME_OUT = 5000L;
    private static final Boolean FIRST_INIT = true;


    public static void main(String[] args) {
        JavaDockerCodeSandbox javaNativeCodeSandbox = new JavaDockerCodeSandbox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "3 4"));
        String code = ResourceUtil.readStr("testCode/simpleComputeACM/Main.java", StandardCharsets.UTF_8);
//        String code= ResourceUtil.readStr("testCode/unsafeCode/RunFileError.java",StandardCharsets.UTF_8);
//        String code= ResourceUtil.readStr("testCode/simpleCompute/Main.java",StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    /**
     * 3.创建容器，把文件复制到容器内
     *
     * @param userCodeFile
     * @param inputList
     * @return
     */
    @Override
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        //获取默认的Docker Client
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        //拉取镜像
        String image = "openjdk:8-alpine";
        if (FIRST_INIT) {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载镜像" + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
        }
        System.out.println("下载完成");
        //创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(100 * 1000 * 1000L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
//        hostConfig.withSecurityOpts(Arrays.asList("seccomp=安全管理配置字符串"));
        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app")));
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withReadonlyRootfs(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        System.out.println(createContainerResponse);
        String containerId = createContainerResponse.getId();
        //docker exec name java -cp /app Main 1 3
        //启动容器
        dockerClient.startContainerCmd(containerId).exec();
        //执行命令并获取结果
        ArrayList<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            StopWatch stopWatch = new StopWatch();
            ExecuteMessage executeMessage = new ExecuteMessage();
            long time = 0L;

            String runCmd = String.format("docker exec -i %s java -cp /app Main", containerId);
            System.out.println(runCmd);

            // 启动进程并等待命令完成
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(runCmd);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                //向控制台输入程序
                OutputStream outputStream = process.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

                String join = inputArgs;
                if (!inputArgs.endsWith("\n")) {
                    join += "\n";
                }
                outputStreamWriter.write(join);
                //相当于按下回车，执行发送
                outputStreamWriter.flush();
                stopWatch.start();
                //等待程序执行，获取错误码
                int exitValue = process.waitFor();

                //正常退出
                if (exitValue == 0) {
                    //分批获取进程的正常输出
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    StringJoiner compileOutputStringBuilder = new StringJoiner("\n");
                    //逐行读取
                    String compileOutputLine;
                    while ((compileOutputLine = bufferedReader.readLine()) != null) {
                        compileOutputStringBuilder.add(compileOutputLine);
                    }
                    executeMessage.setMessage(compileOutputStringBuilder.toString());
                    System.out.println(compileOutputStringBuilder);
                } else {
                    //异常退出
                    //分批获取进程的正常输出
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    StringJoiner compileOutputStringBuilder = new StringJoiner("\n");
                    //逐行读取
                    String compileOutputLine;
                    while ((compileOutputLine = bufferedReader.readLine()) != null) {
                        compileOutputStringBuilder.add(compileOutputLine);
                    }
                    executeMessage.setMessage(compileOutputStringBuilder.toString());

                    System.out.println(compileOutputStringBuilder);
                    //分批获取进程的错误输出
                    BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    StringBuilder errorCompileOutputStringBuilder = new StringBuilder();
                    //逐行读取
                    String errorCompileOutputLine;
                    while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
                        errorCompileOutputStringBuilder.append(errorCompileOutputLine);
                    }
                    executeMessage.setMessage(errorCompileOutputStringBuilder.toString());
                    System.out.println(errorCompileOutputStringBuilder);

                }
                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
                executeMessage.setTime(time);
                //记得资源释放，否则会卡死
                outputStreamWriter.close();
                outputStream.close();
//            inputStream.close();
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }


            final long[] maxMemory = {0L};
            //获取占用内存
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                }

                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void close() throws IOException {

                }
            });
            statsCmd.exec(statisticsResultCallback);
            statsCmd.close();
            executeMessage.setMemory(maxMemory[0]);
            executeMessageList.add(executeMessage);
//            try {
//                stopWatch.start();
//                stopWatch.stop();
//                time= stopWatch.getLastTaskTimeMillis();
//                statsCmd.close();
//            } catch (InterruptedException e) {
//                System.out.println("程序执行异常");
//                throw new RuntimeException(e);
//            }
//            executeMessage.setMessage(message[0]);
//            executeMessage.setErrorMessage(errorMessage[0]);
//            executeMessage.setTime(time);

        }
        return executeMessageList;
    }

}