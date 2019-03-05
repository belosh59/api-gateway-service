package com.flatdeh.apigateway.service.impl;

import com.flatdeh.apigateway.entity.User;
import com.flatdeh.apigateway.service.BetService;
import com.flatdeh.apigateway.service.MessageService;
import com.flatdeh.apigateway.web.vo.BetVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultBetService implements BetService {
    private static final String BET_SERVICE = "bet-service";
    private RestTemplate restTemplate;
    private Map<Integer, BetVO> lotBetCache = new ConcurrentHashMap<>();
    private DiscoveryClient discoveryClient;

    @Override
    public void processBetRequest(BetVO betVO, MessageService client) {
        BetVO cachedBet = lotBetCache.get(betVO.getLotId());
        User user = betVO.getUser();

        if (cachedBet == null) {
            if (processSuccessfulBet(betVO)) {
                client.replyToAllUsers(betVO);
            }
        } else if (cachedBet.getBetPrice() > betVO.getBetPrice()) {
            betVO.setSuccessfulBet(false);
            betVO.setMessage("Кто-то сделал эту ставку до Вас. Попробуйте еще раз");
            client.replyToCurrentUser(betVO);

        } else if (betVO.getBetPrice() > cachedBet.getBetPrice() * 1.05) {
            betVO.setSuccessfulBet(false);
            betVO.setMessage("Вы не можете поднять ставку больше чем на 5%");
            client.replyToCurrentUser(betVO);

        } else if (cachedBet.getUser().getId() == user.getId()) {
            betVO.setSuccessfulBet(false);
            betVO.setMessage("Ваша ставка уже наивысшая.");
            client.replyToCurrentUser(betVO);

        } else {
            if (processSuccessfulBet(betVO)) {
                User cachedUser = cachedBet.getUser();
                client.replyToAllUsers(betVO);
//            processBeatenUser(betVO, cachedUser);
                betVO.setSuccessfulBet(false);
                client.replyToUser(cachedUser, betVO);
            }
        }
    }

    private boolean processSuccessfulBet(BetVO betVO) {
        List<ServiceInstance> clientInstances = discoveryClient.getInstances(BET_SERVICE);
        URI uri = clientInstances.get(0).getUri();

        ResponseEntity<String> response = restTemplate.postForEntity(uri, betVO, String.class);
        HttpStatus status = response.getStatusCode();

        if (status == HttpStatus.OK) {
            lotBetCache.put(betVO.getLotId(), betVO);
            return true;
        } else {
            betVO.setSuccessfulBet(false);
            betVO.setMessage("Во время ставки возникла ошибка!");
            return false;
        }
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setDiscoveryClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }
}
