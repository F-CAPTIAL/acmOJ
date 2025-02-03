package com.swang.acmOJ.judge;


import com.swang.acmOJ.model.entity.QuestionSubmit;
import com.swang.acmOJ.model.vo.QuestionSubmitVO;

public interface JudgeService {
    QuestionSubmit doJudge(long questionSubmitId);
}