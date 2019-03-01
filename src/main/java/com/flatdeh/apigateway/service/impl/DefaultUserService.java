package com.flatdeh.apigateway.service.impl;

import com.flatdeh.apigateway.service.UserService;
import com.flatdeh.apigateway.web.websocket.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class DefaultUserService implements UserService {
    private static final String USER = "/bets/lot/13";

    @Override
    public Optional<UserVO> findByLogin(String login) {
        RestTemplate restTemplate = new RestTemplate();

        String result = restTemplate.getForObject(USER, String.class);

        return Optional.empty();
    }

    @Override
    public Optional<UserVO> getUserByLogin(String userName) {
        return Optional.empty();
    }
}
