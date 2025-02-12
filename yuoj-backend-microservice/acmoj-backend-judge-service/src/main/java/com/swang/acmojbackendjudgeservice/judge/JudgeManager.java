package com.swang.acmojbackendjudgeservice.judge;

import com.swang.acmojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.swang.acmojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.swang.acmojbackendjudgeservice.judge.strategy.JudgeContext;
import com.swang.acmojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.swang.acmojbackendmodel.model.codesandbox.JudgeInfo;
import com.swang.acmojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy=new DefaultJudgeStrategy();
        if("java".equals(language)){
            judgeStrategy=new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
