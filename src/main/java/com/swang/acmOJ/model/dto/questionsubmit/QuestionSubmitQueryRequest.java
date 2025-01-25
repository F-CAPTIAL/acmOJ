package com.swang.acmOJ.model.dto.questionsubmit;

import com.swang.acmOJ.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {


    /**
     * 编程语言
     */
    private String language;

    /**
     * 提交状态
     */
    private Integer status;


    /**
     * 题目 id
     */
    private Long questionId;
    /**
     * 用户 id
     */
    private Long userId;


    private static final long serialVersionUID = 1L;
}