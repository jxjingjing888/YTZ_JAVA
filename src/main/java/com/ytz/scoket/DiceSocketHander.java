package com.ytz.scoket;


import com.ytz.rooms.RoomHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@Component("diceSocketHander")
public class DiceSocketHander implements WebSocketHandler {
    @Autowired
    private RoomHandler roomHandler;
    //private ArrayList<WebSocketSession> allSession = new ArrayList<WebSocketSession>();

    /**
     * 初次链接成功执行
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("初次链接成功......");
        //allSession.add(session);
    }

    /*
     * private void sendMessageToAllSession(TextMessage message) throws
     * IOException { // TODO Auto-generated method stub //发送信息给全部在线用户 for
     * (WebSocketSession session : allSession) { session.sendMessage(message); }
     * }
     */

    // 接受消息处理消息

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage)
            throws Exception {
        //处理用户请求
        roomHandler.receiveMessage(webSocketSession, webSocketMessage.getPayload() + "");
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        log.debug("链接出错，关闭链接......");
        error(webSocketSession);
    }

    private void error(WebSocketSession session) throws IOException, InterruptedException {
        // TODO Auto-generated method stub
        //allSession.remove(session);
        // 发送在线人数
        //sendMessageToAllSession(new TextMessage("o" + allSession.size()));
        // 处理异常用户
        roomHandler.error(session);


    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        log.debug("链接关闭......" + closeStatus.toString());
        error(webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        // TODO Auto-generated method stub
        return false;
    }

}