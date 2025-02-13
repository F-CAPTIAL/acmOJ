package com.swang.codesandbox.utils;


import cn.hutool.core.util.StrUtil;
import com.swang.codesandbox.model.ExecuteMessage;
import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

/**
 * 进程工具类
 */

public class ProcessUtils {
    /**
     * 执行进程，并获取信息
     *
     * @param runProcess
     * @param opName
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();


        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            //等待程序执行，获取错误码
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            //正常退出
            if (exitValue == 0) {
                System.out.println(opName + "成功");
                //分批获取进程的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), StandardCharsets.UTF_8));
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
                System.out.println(opName + "失败，错误码" + exitValue);
                //分批获取进程的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), StandardCharsets.UTF_8));
                StringJoiner compileOutputStringBuilder = new StringJoiner("\n");
                //逐行读取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    compileOutputStringBuilder.add(compileOutputLine);
                }
                executeMessage.setMessage(compileOutputStringBuilder.toString());
                //分批获取进程的错误输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
                StringBuilder errorCompileOutputStringBuilder = new StringBuilder();
                //逐行读取
                String errorCompileOutputLine;
                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
                    errorCompileOutputStringBuilder.append(errorCompileOutputLine);
                }
                executeMessage.setErrorMessage(errorCompileOutputStringBuilder.toString());
            }
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeMessage;
    }

    /**
     * 执行交互式进程，并获取信息
     *
     * @param runProcess
     * @param args
     * @return
     */
    public static ExecuteMessage runInteractProcessAndGetMessage(Process runProcess, String args, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            //向控制台输入程序
            OutputStream outputStream = runProcess.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

            String join = args;
            if (!args.endsWith("\n")) {
                join += "\n";
            }
            outputStreamWriter.write(join);
            //相当于按下回车，执行发送
            outputStreamWriter.flush();
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            //等待程序执行，获取错误码
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            //正常退出
            if (exitValue == 0) {
                System.out.println(opName + "成功");
                //分批获取进程的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
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
                System.out.println(opName + "失败，错误码" + exitValue);
                //分批获取进程的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                StringJoiner compileOutputStringBuilder = new StringJoiner("\n");
                //逐行读取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    compileOutputStringBuilder.add(compileOutputLine);
                }
                executeMessage.setMessage(compileOutputStringBuilder.toString());
                //分批获取进程的错误输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
                StringBuilder errorCompileOutputStringBuilder = new StringBuilder();
                //逐行读取
                String errorCompileOutputLine;
                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
                    errorCompileOutputStringBuilder.append(errorCompileOutputLine);
                }
                executeMessage.setErrorMessage(errorCompileOutputStringBuilder.toString());
            }
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());

            //记得资源释放，否则会卡死
            outputStreamWriter.close();
            outputStream.close();
//            inputStream.close();
            runProcess.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeMessage;
    }

}