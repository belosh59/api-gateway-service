package com.flatdeh.apigateway.entity;

import lombok.Data;

@Data
public class Bet {
    private int lotId;
    private String lotName;
    private Double betPrice;
    private Double newCurrentPrice;
    private Double newBetPrice;

    private User user;

    private String message;
    private boolean successfulBet;
}
