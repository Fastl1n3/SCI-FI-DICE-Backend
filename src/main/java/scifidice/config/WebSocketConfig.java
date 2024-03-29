package scifidice.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@ComponentScan("scifidice")
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/admin"); // любой пункт назначения с этим префиксом будет перенаправлен обратно клиенту
        registry.setApplicationDestinationPrefixes("/app"); // любое сообщение отправленное на имя канала websocket с /app должно быть перенапр
                                                            // на MessageMapping
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOrigins("*");
        registry.addEndpoint("/websocket").setAllowedOrigins("*").withSockJS();
    }
}
