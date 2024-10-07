package com.li.examplespringbootconsumer;

import com.li.example.common.model.User;
import com.li.example.common.service.UserService;
import com.li.lirpcspringbootstarter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("韩天尊");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}
