package com.flatdeh.apigateway.service;

import com.flatdeh.apigateway.web.vo.BetVO;
import com.flatdeh.apigateway.web.vo.UserVO;

public interface MessageService {

    void replyToAllUsers(BetVO betVO);

    void replyToCurrentUser(BetVO betVO);

    void replyToUser(UserVO user, BetVO betVO);
}
