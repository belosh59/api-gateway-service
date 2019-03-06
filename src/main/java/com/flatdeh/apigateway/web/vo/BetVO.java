package com.flatdeh.apigateway.web.vo;

import lombok.Data;

@Data
public class BetVO {
    private int lotId;
    private int userId;
    private Double betPrice;
    private Double newCurrentPrice;
    private Double newBetPrice;
    private int previousBetUserId;
}
