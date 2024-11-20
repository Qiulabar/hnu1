package com.smtpmail.service;

import com.smtpmail.entity.Advice;
import com.smtpmail.mapper.AdviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdviceService {
    @Autowired
    AdviceMapper adviceMapper;
    public void saveAdvice(Advice advice)
    {
        adviceMapper.save(advice);
    }
}
