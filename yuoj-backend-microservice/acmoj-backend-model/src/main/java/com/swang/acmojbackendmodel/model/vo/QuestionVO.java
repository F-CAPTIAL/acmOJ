package com.swang.acmojbackendmodel.model.vo;

import cn.hutool.json.JSONUtil;
import com.swang.acmojbackendmodel.model.dto.question.JudgeCase;
import com.swang.acmojbackendmodel.model.dto.question.JudgeConfig;
import com.swang.acmojbackendmodel.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.*;

@Data
public class QuestionVO {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;
    /**
     * 难度
     */
    private String level;


    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;


    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;

    /**
     * 测试用例
     */
    private List<JudgeCase> judgeCase;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建人
     */
    private UserVO userVO;

    /**
     * 做题状态
     */
    private String statusByUser;


    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig voJudgeConfig = questionVO.getJudgeConfig();
        if (voJudgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(voJudgeConfig));
        }
        List<JudgeCase> voJudgeCase = questionVO.getJudgeCase();
        if (voJudgeCase != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(voJudgeCase));
        }
        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        List<String> levels = Arrays.asList("简单", "中等", "困难");

        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        // 将tags中的简单，困难，中等 转移到level字段中
        List<String> tags = JSONUtil.toList(question.getTags(), String.class);
        List<String> tagList = new LinkedList<>();
        for (String tag : tags) {
            if (levels.contains(tag)) {
                questionVO.setLevel(tag);
                continue;
            }
            tagList.add(tag);
        }
        questionVO.setTags(tagList);
        String judgeConfigStr = question.getJudgeConfig();
        questionVO.setJudgeConfig(JSONUtil.toBean(judgeConfigStr, JudgeConfig.class));
        String judgeCaseStr = question.getJudgeCase();
        questionVO.setJudgeCase(JSONUtil.toList(judgeCaseStr, JudgeCase.class));
        return questionVO;
    }
}
