package com.swang.acmOJ.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.swang.acmOJ.mapper.QuestionMapper;
import com.swang.acmOJ.model.entity.Question;
import com.swang.acmOJ.service.QuestionService;
import org.springframework.stereotype.Service;

/**
 * @author 王帅
 * @description 针对表【question(题目)】的数据库操作Service实现
 * @createDate 2025-01-07 11:10:48
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

}




