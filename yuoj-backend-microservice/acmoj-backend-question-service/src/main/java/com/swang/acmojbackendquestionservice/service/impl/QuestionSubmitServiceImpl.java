package com.swang.acmojbackendquestionservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swang.acmojbackendcommon.common.ErrorCode;
import com.swang.acmojbackendcommon.constant.CommonConstant;
import com.swang.acmojbackendcommon.exception.BusinessException;
import com.swang.acmojbackendcommon.utils.SqlUtils;
import com.swang.acmojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.swang.acmojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.swang.acmojbackendmodel.model.entity.Question;
import com.swang.acmojbackendmodel.model.entity.QuestionSubmit;
import com.swang.acmojbackendmodel.model.entity.User;
import com.swang.acmojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.swang.acmojbackendmodel.model.enums.QuestionSubmitLanguageEnum;
import com.swang.acmojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.swang.acmojbackendmodel.model.vo.QuestionSubmitVO;
import com.swang.acmojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.swang.acmojbackendquestionservice.rabbitmq.MyMessageProducer;
import com.swang.acmojbackendquestionservice.service.QuestionSubmitService;
import com.swang.acmojbackendserviceclient.service.JudgeFeignClient;
import com.swang.acmojbackendserviceclient.service.UserFeignClient;
import com.swang.acmojbackendserviceclient.service.QuestionFeignClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author 27506
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2024-10-20 15:33:09
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {
    @Resource
    private QuestionFeignClient questionService;
    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;
    @Resource
    private MyMessageProducer myMessageProducer;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        // 锁必须要包裹住事务方法
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WATING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据输入失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        //发送消息
        myMessageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmitId));
        //执行判题服务
//        CompletableFuture.runAsync(() -> {
//            judgeFeignClient.doJudge(questionSubmitId);
//        });
        return questionSubmitId;
    }

    /**
     * 获取查询包装类
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        //仅本人和管理员能看见自己的答案
        Long userId = loginUser.getId();
        //处理脱敏
        if (!Objects.equals(userId, questionSubmit.getUserId()) && !userFeignClient.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit ->
                getQuestionSubmitVO(questionSubmit, loginUser)
        ).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    @Override
    public List<QuestionSubmit> acceptedListByUserIdAndQuestionId(long userId, long questionId) {

        LambdaQueryWrapper<QuestionSubmit> query_wrapper = new LambdaQueryWrapper<>();
        query_wrapper.eq(QuestionSubmit::getUserId, userId)
                .eq(QuestionSubmit::getQuestionId, questionId)
                .like(QuestionSubmit::getJudgeInfo, JudgeInfoMessageEnum.ACCEPT.getValue());

        return list(query_wrapper);
    }

}




