package com.example.sales.chat.config;


import com.example.sales.chat.AuthChannelInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
/**
 * WebSocket configuration for chat messaging and STOMP broker setup.
 * <p>
 * Registers STOMP endpoints, configures the message broker, and attaches
 * the inbound authentication interceptor.
 * </p>
 */
@Configuration
@AllArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptor authChannelInterceptor;

    /**
     * Configures the application message broker and destination prefixes.
     *
     * @param registry message broker registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * Registers STOMP endpoints used by WebSocket clients.
     *
     * @param registry STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/chat-native")
                .setAllowedOriginPatterns("*");
    }

    /**
     * Adds inbound channel interceptors for message authentication.
     *
     * @param registration channel registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor);
    }
}
