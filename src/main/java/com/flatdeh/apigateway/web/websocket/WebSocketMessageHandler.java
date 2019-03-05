package com.flatdeh.apigateway.web.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flatdeh.apigateway.service.MessageService;
import com.flatdeh.apigateway.service.BetService;
import com.flatdeh.apigateway.web.vo.BetVO;
import com.flatdeh.apigateway.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketMessageHandler extends TextWebSocketHandler implements MessageService {
    private Logger logger = LoggerFactory.getLogger(WebSocketMessageHandler.class);
    private List<WebSocketSession> clients = new CopyOnWriteArrayList<>();
    private BetService betService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        clients.remove(session);
        logger.info("WebSocket connection has been closed for user: {}", session.getAttributes().get("user"));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        clients.add(session);
        logger.info("WebSocket connection has been opened for user: {}", session.getAttributes().get("user"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        String json = textMessage.getPayload();
        BetVO betVO = parseJson(json);
        User user = (User) session.getAttributes().get("user");
        user=new User(); //todo: remove this

        if (user == null) {
            betVO.setSuccessfulBet(false);
            betVO.setMessage("Вы не можете сделать ставку. Пожалуйста выполните вход на сайт");
            String data = writeJson(betVO);
            session.sendMessage(new TextMessage(data));
        } else {
            betVO.setUser(user);
            betService.processBetRequest(betVO, this);
        }
    }

    @Override
    public void replyToAllUsers(BetVO betVO) {
        String data = writeJson(betVO);
        clients.forEach(client -> sendMessage(client, data));
    }

    @Override
    public void replyToCurrentUser(BetVO betVO) {
        User user = betVO.getUser();
        replyToUser(user, betVO);
    }

    @Override
    public void replyToUser(User user, BetVO betVO) {
        logger.info("Replying to user: {}", user);
        String data = writeJson(betVO);

        Optional<WebSocketSession> targetClient = clients.stream()
            .filter(client -> {
                User userFromSession = (User) client.getAttributes().get("user");
                if (userFromSession != null) {
                    return userFromSession.getId() == user.getId();
                } else {
                    return false;
                }
            })
            .findFirst();

        targetClient.ifPresent(client -> sendMessage(client, data));
        logger.info("Replying to user: {}, finished successfully", user);
    }

    private void sendMessage(WebSocketSession client, String data) {
        try {
            client.sendMessage(new TextMessage(data));
        } catch (IOException e) {
            logger.error("Unable to send message via WebSocket");
            throw new RuntimeException("Unable to send message via WebSocket", e);
        }
    }

    private BetVO parseJson(String json) {
        try {
            return objectMapper.readValue(json, BetVO.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse websocket json to BetVO", e);
        }
    }

    private String writeJson(BetVO betVO) {
        try {
            return objectMapper.writeValueAsString(betVO);
        } catch (IOException e) {
            throw new RuntimeException("Unable to generate websocket json for BetVO", e);
        }
    }

    @Autowired
    public void setBetService(BetService betService) {
        this.betService = betService;
    }
}
