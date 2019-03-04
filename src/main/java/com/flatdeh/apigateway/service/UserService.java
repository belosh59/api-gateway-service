package com.flatdeh.apigateway.service;

import com.flatdeh.apigateway.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByLogin(String login);

    Optional<User> getUserByLogin(String userName);
}
