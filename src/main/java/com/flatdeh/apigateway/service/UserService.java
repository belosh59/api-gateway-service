package com.flatdeh.apigateway.service;

import com.flatdeh.apigateway.web.websocket.vo.UserVO;

import java.util.Optional;

public interface UserService {
    Optional<UserVO> findByLogin(String login);

    Optional<UserVO> getUserByLogin(String userName);
}
