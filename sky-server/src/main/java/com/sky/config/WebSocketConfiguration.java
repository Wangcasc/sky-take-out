package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类，用于注册WebSocket的Bean 用于注册websocket 服务端组件
 * 通过这段代码配置，Spring会扫描项目中使用 ServerEndpoint注解声明的WebSocket端点，并自动注册这些端点，使它们能够被正确地映射和使用
 * 用于实现来单提醒
 */
@Configuration
public class WebSocketConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter(); // 注册websocket 服务端组件
    }

}
