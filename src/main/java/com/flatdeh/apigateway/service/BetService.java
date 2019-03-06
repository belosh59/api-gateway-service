package com.flatdeh.apigateway.service;

import com.flatdeh.apigateway.entity.Bet;

public interface BetService {
    void processBetRequest(Bet betVO, MessageService client);
}
