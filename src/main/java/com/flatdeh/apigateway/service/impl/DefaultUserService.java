package com.flatdeh.apigateway.service.impl;

import com.flatdeh.apigateway.service.UserService;
import com.flatdeh.apigateway.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class DefaultUserService implements UserService {
    private static final String USER = "/bets/lot/13";

    @Override
    public Optional<User> findByLogin(String login) {
        RestTemplate restTemplate = new RestTemplate();

        String result = restTemplate.getForObject(USER, String.class);

        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByLogin(String userName) {
        return Optional.empty();
    }
}
