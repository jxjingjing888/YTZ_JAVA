package com.ytz.scoket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket // 开启websocket
public class WebSocketConfig implements WebSocketConfigurer {
    /**
     * 摇骰子
     */
    @Autowired
    @Qualifier("diceSocketHander")
    private DiceSocketHander webSocketHander;

    /**
     * 动物
     *
     * @param registry
     */
/*    @Autowired
    @Qualifier("animalSocketHander")
    private WebSocketHandler animalSocketHander;*/
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHander, "/echo");
        // registry.addHandler(animalSocketHander, "/animal");

    }
}