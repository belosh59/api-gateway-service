package com.flatdeh.apigateway.web.websocket;

import com.flatdeh.apigateway.service.UserService;
import com.flatdeh.apigateway.web.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

public class WebSocketInterceptor implements HandshakeInterceptor {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private UserService userService;

    public WebSocketInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        Principal principal = request.getPrincipal();
        if (principal != null) {
            String userName = principal.getName();

            Optional<UserVO> userOptional = userService.getUserByLogin(userName);
            userOptional.ifPresent(user -> {
                attributes.put("user", user);
                logger.info("Connecting user: {}.", user.getLogin());
            });
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}
