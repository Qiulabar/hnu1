package com.smtpmail.service;

import com.smtpmail.entity.User;
import com.smtpmail.entity.UserShow;
import com.smtpmail.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
//
//    Long count = userMapper.pageAllCount();
//    List<User> users = userMapper.pageAll((current - 1) * limit, limit);
    public long pageAllCount(){
        return userMapper.pageAllCount();
    }
    public List<User> pageAll(long current, long limit){
        return userMapper.pageAll((current - 1) * limit, limit);
    }

    public long selectFuzzyCount(String finalUsername){
        return userMapper.selectFuzzyCount(finalUsername);
    }

    public List<UserShow> selectFuzzy(String finalUsername, long current, long limit){
        return  userMapper.selectFuzzy(finalUsername, (current - 1) * limit, limit);
    }

    public List<User> selectByLevel(int num){
        return userMapper.selectByLevel(num);
    }

    public User select(String userName){
        return userMapper.select(userName);
    }

    public User selectExist(String userName){
        return userMapper.selectExist(userName);
    }

    public void insert(User user){
        userMapper.insert(user);
    }

    public void update(User user) {
        userMapper.update(user);
    }

//    public void remove(String username) {
//    	userMapper.remove(username);
//    }

    public void  disableBatch(List<String> nameList) {
        userMapper.disableBatch(nameList);
    }

    public void ableBatch(List<String> nameList) {
    	userMapper.ableBatch(nameList);
    }

    public void removeBatch(List<String> nameList){
        userMapper.removeBatch(nameList);
    }

    public void saveAvatar(String username, String url){
        userMapper.saveAvatar(username, url);
    }

    public Map<Long, Map<String, Long>> statistics(){
        return userMapper.statistics();
    }


}
