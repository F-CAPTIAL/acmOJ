package com.swang.acmOJ.judge.codesandbox.impl;

import com.swang.acmOJ.judge.codesandbox.CodeSandbox;
import com.swang.acmOJ.judge.codesandbox.CodeSandboxFactory;
import com.swang.acmOJ.judge.codesandbox.CodeSandboxProxy;
import com.swang.acmOJ.judge.codesandbox.model.ExecuteCodeRequest;
import com.swang.acmOJ.judge.codesandbox.model.ExecuteCodeResponse;
import com.swang.acmOJ.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RemoteCodeSandboxTest {
    @Value("${codesandbox.type:example}")
    private String type;

    @Test
    void executeCodeByProxy() {
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String code = "import java.util.Scanner;\n" +
                "\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        Scanner cin = new Scanner(System.in);\n" +
                "        int a = cin.nextInt();\n" +
                "        int b = cin.nextInt();\n" +
                "        System.out.println(\"ans:\" + (a * b));\n" +
                "\n" +
                "    }\n" +
                "}\n";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }
}