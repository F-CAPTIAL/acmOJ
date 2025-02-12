package com.swang.acmojbackendjudgeservice.judge.codesandbox.impl;


import com.swang.acmojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.swang.acmojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.swang.acmojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 第三方代码沙箱
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
