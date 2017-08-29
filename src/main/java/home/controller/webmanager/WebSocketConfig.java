package home.controller.webmanager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * Created by Will on 1/16/2017.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    org.springframework.web.socket.WebSocketHandler getHandler() {
        return new PerConnectionWebSocketHandler(WebsocketHandler.class);
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        container.setAsyncSendTimeout(10000);
        return container;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getHandler(), "/ws").setAllowedOrigins("*");
    }
}