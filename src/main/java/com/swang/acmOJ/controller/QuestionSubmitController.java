package com.swang.acmOJ.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swang.acmOJ.common.BaseResponse;
import com.swang.acmOJ.common.ErrorCode;
import com.swang.acmOJ.common.ResultUtils;
import com.swang.acmOJ.exception.BusinessException;
import com.swang.acmOJ.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.swang.acmOJ.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.swang.acmOJ.model.entity.QuestionSubmit;
import com.swang.acmOJ.model.entity.User;
import com.swang.acmOJ.model.vo.QuestionSubmitVO;
import com.swang.acmOJ.service.QuestionSubmitService;
import com.swang.acmOJ.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
//@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return 提交记录id
     */
//    @PostMapping("/")
//    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
//                                              HttpServletRequest request) {
//        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        // 登录才能点赞
//        final User loginUser = userService.getLoginUser(request);
//        long questionId = questionSubmitAddRequest.getQuestionId();
//        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
//        return ResultUtils.success(questionSubmitId);
//    }
//    /**
//     * 分页获取题目提交列表（仅管理员）
//     *
//     * @param questionSubmitQueryRequest
//     * @return
//     */
//    @PostMapping("/list/page")
//    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
//        long current = questionSubmitQueryRequest.getCurrent();
//        long size = questionSubmitQueryRequest.getPageSize();
//        //从数据提取到原始的分页信息
//        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
//                                                                             questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
//        final User loginUser=userService.getLoginUser(request);
//        //返回脱敏信息
//        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage,loginUser));
//    }
}