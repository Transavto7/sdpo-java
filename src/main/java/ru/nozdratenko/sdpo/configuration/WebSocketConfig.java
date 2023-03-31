package ru.nozdratenko.sdpo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import ru.nozdratenko.sdpo.websocket.VideoEndpoint;

@Configuration
@EnableWebSocket
public class WebSocketConfig {

    @Bean
    public VideoEndpoint videoWebSocket() {
        return new VideoEndpoint();
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
