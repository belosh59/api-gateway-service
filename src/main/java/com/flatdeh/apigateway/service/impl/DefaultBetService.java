package com.flatdeh.apigateway.service.impl;

import com.flatdeh.apigateway.entity.Bet;
import com.flatdeh.apigateway.entity.User;
import com.flatdeh.apigateway.service.BetService;
import com.flatdeh.apigateway.service.MessageService;
import com.flatdeh.apigateway.web.vo.BetVO;
import org.modelmapper.ModelMapper;
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
    private Map<Integer, Bet> lotBetCache = new ConcurrentHashMap<>();
    private DiscoveryClient discoveryClient;
    private ModelMapper modelMapper;

    @Override
    public void processBetRequest(Bet bet, MessageService client) {
        Bet cachedBet = lotBetCache.get(bet.getLotId());
        User user = bet.getUser();

        if (cachedBet == null) {
            if (processSuccessfulBet(bet)) {
                client.replyToAllUsers(bet);
            }
        } else if (cachedBet.getBetPrice() > bet.getBetPrice()) {
            bet.setSuccessfulBet(false);
            bet.setMessage("Кто-то сделал эту ставку до Вас. Попробуйте еще раз");
            client.replyToCurrentUser(bet);

        } else if (bet.getBetPrice() > cachedBet.getBetPrice() * 1.05) {
            bet.setSuccessfulBet(false);
            bet.setMessage("Вы не можете поднять ставку больше чем на 5%");
            client.replyToCurrentUser(bet);

        } else if (cachedBet.getUser().getId() == user.getId()) {
            bet.setSuccessfulBet(false);
            bet.setMessage("Ваша ставка уже наивысшая.");
            client.replyToCurrentUser(bet);

        } else {
            User cachedUser = cachedBet.getUser();
            if (processSuccessfulBet(bet, cachedUser.getId())) {
                client.replyToAllUsers(bet);

                processBeatenUser(bet);
                bet.setSuccessfulBet(false);
                client.replyToUser(cachedUser, bet);
            }
        }
    }

    private boolean processSuccessfulBet(Bet bet) {
        return processSuccessfulBet(bet, 0);
    }

    private boolean processSuccessfulBet(Bet bet, int cachedUserId) {
        List<ServiceInstance> clientInstances = discoveryClient.getInstances(BET_SERVICE);
        URI uri = clientInstances.get(0).getUri();

        BetVO betVO = modelMapper.map(bet, BetVO.class);
        betVO.setPreviousBetUserId(cachedUserId);

        ResponseEntity<String> response = restTemplate.postForEntity(uri, betVO, String.class);
        HttpStatus status = response.getStatusCode();

        if (status == HttpStatus.OK) {
            lotBetCache.put(bet.getLotId(), bet);
            return true;
        } else {
            bet.setSuccessfulBet(false);
            bet.setMessage("Ставка не принята! Во время обработки возникла ошибка!");
            return false;
        }
    }

    private void processBeatenUser(Bet bet) {
        User user = bet.getUser();

        String message = "Пользователь " + user.getLogin() + " перебил Вашу ставку на лот " + bet.getLotName();
        bet.setMessage(message);
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setDiscoveryClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
