package com.flatdeh.apigateway.web.websocket.config;

import com.flatdeh.apigateway.service.UserService;
import com.flatdeh.apigateway.web.websocket.WebSocketInterceptor;
import com.flatdeh.apigateway.web.websocket.WebSocketMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    private UserService userService;

    @Bean
    public WebSocketHandler webSocketMessageHandler() {
        return new WebSocketMessageHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketMessageHandler(), "/api/v2/ws/main").setAllowedOrigins("*").addInterceptors(new WebSocketInterceptor(userService));
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}