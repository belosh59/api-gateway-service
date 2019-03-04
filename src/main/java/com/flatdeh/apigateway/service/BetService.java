package com.flatdeh.apigateway.service;

import com.flatdeh.apigateway.web.vo.BetVO;

public interface BetService {
    void processBetRequest(BetVO betVO, MessageService client);
}
