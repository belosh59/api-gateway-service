package com.flatdeh.apigateway;

import com.flatdeh.apigateway.web.websocket.vo.BetVO;
import com.flatdeh.apigateway.web.websocket.vo.UserVO;

public interface MessageService {

    void replyToAllUsers(BetVO betVO);

    void replyToCurrentUser(BetVO betVO);

    void replyToUser(UserVO user, BetVO betVO);
}
