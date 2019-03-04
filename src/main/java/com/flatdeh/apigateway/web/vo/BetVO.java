package com.flatdeh.apigateway.web.vo;

import lombok.Data;

@Data
public class BetVO {
    private int lotId;
    private String lotName;
    private Double price;
    private Double newCurrentPrice;
    private Double newBetPrice;

    private UserVO user;

    private String message;
    private boolean successfulBet;
}
