package com.li.example.provider;

import com.li.example.common.model.User;
import com.li.example.common.service.UserService;

/**
 * 服务实现类
 */
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名"+user.getName());
        return user;
    }
}
