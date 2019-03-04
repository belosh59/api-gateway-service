package com.flatdeh.apigateway.service.impl;

import com.flatdeh.apigateway.service.BetService;
import com.flatdeh.apigateway.service.MessageService;
import com.flatdeh.apigateway.web.vo.BetVO;
import com.flatdeh.apigateway.web.vo.UserVO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultBetService implements BetService {
    private Map<Integer, BetVO> lotBetCache = new ConcurrentHashMap<>();

    @Override
    public void processBetRequest(BetVO betVO, MessageService client) {
        BetVO cachedBet = lotBetCache.get(betVO.getLotId());
        UserVO user = betVO.getUser();

        if (cachedBet == null) {
            processSuccessfulBet(betVO);
            client.replyToAllUsers(betVO);

        } else if (cachedBet.getPrice() > betVO.getPrice()) {
            betVO.setSuccessfulBet(false);
            betVO.setMessage("Кто-то сделал эту ставку до Вас. Попробуйте еще раз");
            client.replyToCurrentUser(betVO);

        } else if (betVO.getPrice() > cachedBet.getPrice() * 1.05) {
            betVO.setSuccessfulBet(false);
            betVO.setMessage("Вы не можете поднять ставку больше чем на 5%");
            client.replyToCurrentUser(betVO);

        } else if (cachedBet.getUser().getId() == user.getId()) {
            betVO.setSuccessfulBet(false);
            betVO.setMessage("Ваша ставка уже наивысшая.");
            client.replyToCurrentUser(betVO);

        } else {
            UserVO cachedUser = cachedBet.getUser();
            processSuccessfulBet(betVO);
            client.replyToAllUsers(betVO);

//            processBeatenUser(betVO, cachedUser);
            betVO.setSuccessfulBet(false);
            client.replyToUser(cachedUser, betVO);
        }
    }

    private void processSuccessfulBet(BetVO betVO) {

    }

}
