package com.swang.acmojbackendjudgeservice.judge.codesandbox;


import com.swang.acmojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.swang.acmojbackendmodel.model.codesandbox.ExecuteCodeResponse;

public interface CodeSandbox {
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
