package com.ytz.core;

import com.ytz.enums.ActionEnums;
import com.ytz.pojo.MessageDTO;
import com.ytz.rooms.RoomHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;


/**
 * @author Bob
 */
@Slf4j
@Component
public class TimeOutMonitor {


    @Autowired
    private RoomHandler roomHandler;

    public TimeOutMonitor() {
        log.info("启动超时监控");
        new TimeOutHandler().start();
    }


    class TimeOutHandler extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(2000);// 每3秒扫一次

                    Map<String, GameRoom> rooms = roomHandler.getRooms();
                    Iterator<String> it = rooms.keySet().iterator();
                    while (it.hasNext()) {
                        String roomKey = it.next();
                        GameRoom room = rooms.get(roomKey);
                        //检查游戏中的焦点是否是超时了
                        if (room.getCallPointTimeOut() != null && room.isStart() && (System.currentTimeMillis() - room.getCallPointTimeOut()) > 22000) {
                            if (room.isDealerRound()) {//庄家回合就庄家开
                                log.debug("庄家超时操作");
                                room.userTimeOut(room.getDealerSession(), new MessageDTO(ActionEnums.USER_TIME_OUT.getType(), room.getRound() == 0 ? "isFirst" : ""));
                            } else {
                                log.debug("闲家超时操作");
                                room.userTimeOut(room.getPlayerSession(), new MessageDTO(ActionEnums.USER_TIME_OUT.getType(), room.getRound() == 0 ? "isFirst" : ""));
                            }
                        }


                    }


                } catch (Exception e) {
                    log.error("超时监控异常", e);
                }

            }
        }


    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        System.out.println((System.currentTimeMillis() - 1545670266603L));
        // > 20000
    }

}