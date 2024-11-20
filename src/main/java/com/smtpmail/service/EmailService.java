package com.smtpmail.service;

import com.smtpmail.mapper.EmailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class EmailService {
    @Autowired
    private EmailMapper emailMapper;

    public void removeBatchByRcptTo(String userName)
    {
        emailMapper.removeBatchByRcptTo(Collections.singletonList(userName));
    }





}
