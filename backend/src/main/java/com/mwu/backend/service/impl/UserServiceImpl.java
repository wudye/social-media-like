package com.mwu.backend.service.impl;

import com.mwu.backend.constant.UserConstant;
import com.mwu.backend.pojo.entity.User;
import com.mwu.backend.repository.UserRepository;
import com.mwu.backend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;
    @Override
    public User getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return  (User) session.getAttribute(UserConstant.LOGIN_USER);



    }

    @Override
    public User getById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
