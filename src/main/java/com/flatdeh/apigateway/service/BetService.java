package com.flatdeh.apigateway.service;

import com.flatdeh.apigateway.MessageService;
import com.flatdeh.apigateway.web.websocket.vo.BetVO;

public interface BetService {
    void processBetRequest(BetVO betVO, MessageService client);
}
