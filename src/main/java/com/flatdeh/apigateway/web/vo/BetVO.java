package com.flatdeh.apigateway.web.vo;

import com.flatdeh.apigateway.entity.User;
import lombok.Data;

@Data
public class BetVO {
    private int lotId;
    private String lotName;
    private Double betPrice;
    private Double newCurrentPrice;
    private Double newBetPrice;

    private User user;

    private String message;
    private boolean successfulBet;
}
