package com.mwu.backend.service;

import com.mwu.backend.pojo.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    User getLoginUser(HttpServletRequest request);

    User getById(long userId);
}
