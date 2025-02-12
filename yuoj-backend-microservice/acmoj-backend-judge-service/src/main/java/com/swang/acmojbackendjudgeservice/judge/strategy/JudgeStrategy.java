package com.swang.acmojbackendjudgeservice.judge.strategy;


import com.swang.acmojbackendmodel.model.codesandbox.JudgeInfo;

/**
 * 判题策略
 */
public interface JudgeStrategy {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
