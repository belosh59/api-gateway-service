package com.flatdeh.apigateway.web.websocket.vo;

import lombok.Data;

@Data
public class BetVO {
    private int lotId;
    private String lotName;
    private Double betPrice;
    private Double newCurrentPrice;
    private Double newBetPrice;

    private UserVO user;

    private String message;
    private boolean successfulBet;
}
