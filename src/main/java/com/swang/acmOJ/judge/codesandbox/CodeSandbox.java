package com.swang.acmOJ.judge.codesandbox;

import com.swang.acmOJ.judge.codesandbox.model.ExecuteCodeRequest;
import com.swang.acmOJ.judge.codesandbox.model.ExecuteCodeResponse;

public interface CodeSandbox {

    /**
     * 执行代
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
