package com.swang.codesandbox;


import com.swang.codesandbox.model.ExecuteCodeRequest;
import com.swang.codesandbox.model.ExecuteCodeResponse;

public interface CodeSandbox {

    /**
     * 执行代
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
