package com.flatdeh.apigateway.service;

import com.flatdeh.apigateway.web.vo.BetVO;
import com.flatdeh.apigateway.entity.User;

public interface MessageService {

    void replyToAllUsers(BetVO betVO);

    void replyToCurrentUser(BetVO betVO);

    void replyToUser(User user, BetVO betVO);
}
