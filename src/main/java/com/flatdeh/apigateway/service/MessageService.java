package com.flatdeh.apigateway.service;

import com.flatdeh.apigateway.entity.Bet;
import com.flatdeh.apigateway.entity.User;

public interface MessageService {

    void replyToAllUsers(Bet betVO);

    void replyToCurrentUser(Bet betVO);

    void replyToUser(User user, Bet betVO);
}
