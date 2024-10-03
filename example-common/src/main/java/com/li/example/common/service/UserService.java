package com.li.example.common.service;

import com.li.example.common.model.User;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);


    /**
     * 获取数字
     * @return
     */
    default short getNumber(){
        return 1;
    }
}

