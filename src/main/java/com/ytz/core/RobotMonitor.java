package com.ytz.core;

import com.ytz.bean.SysUser;
import com.ytz.enums.ActionEnums;
import com.ytz.pojo.MessageDTO;
import com.ytz.pojo.UserInfos;
import com.ytz.rooms.RoomHandler;
import com.ytz.service.UserInfoService;
import com.ytz.util.MobileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 机器人陪玩
 *
 * @author Bob
 */
@Slf4j
@Component
public class RobotMonitor {


    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RoomHandler roomHandler;

    public RobotMonitor() {
        log.info("初始化机器人");
        init();
    }

    public void init() {
        BufferedReader br = null;
        try {
            InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream("/robotToken.txt"));
            br = new BufferedReader(reader);
            String s = null;
            //使用readLine方法，一次读一行
            while ((s = br.readLine()) != null) {
                queue.put(s.trim());
            }
            new RoomMonitor().start();
        } catch (Throwable e) {
            log.error("初始化机器人失败", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private UserInfos selectUserInfo(String token) {
        SysUser u = userInfoService.getUserByToken(token);
        UserInfos userInfo = null;
        if (u != null) {
            userInfo = new UserInfos();
            userInfo.setUserId(u.getId());
            userInfo.setTokenid(u.getToken());
            userInfo.setRealName(MobileUtil.mobileEncrypt(u.getMobilePhone()));
            userInfo.setMoney(u.getBalance() == null ? "0" : u.getBalance().intValue() + "");
            userInfo.setPhone(u.getMobilePhone());
            userInfo.setImg(u.getFaceImg());
        }
        return userInfo;
    }


    //机器人用户

    private static BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100);

    class RoomMonitor extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(8000);// 每5秒扫一次
                    if (queue.size() <= 0) {//机器人用完了，不执行
                        continue;
                    }

                    Map<String, GameRoom> rooms = roomHandler.getRooms();
                    Iterator<String> it = rooms.keySet().iterator();
                    while (it.hasNext()) {
                        String roomKey = it.next();
                        GameRoom room = rooms.get(roomKey);
                        if (room.getRoomOnline() == 0) {// 如果没有人了就移除掉
                            log.debug("移除房间" + room.getGameNumber());
                            it.remove();
                            continue;
                        }
                        if (room.getRoomOnline() == 1 && room.isHasRobot()) {
                            log.debug("回收机器人");
                            queue.put(room.getDealerUser().getTokenid());//回收机器人
                            it.remove();
                            continue;
                        }
                        //用户空闲时间必须大于8秒
                        if (room.getUserFreeTime() == null || (System.currentTimeMillis() - room.getUserFreeTime()) < 8000) {
                            continue;
                        }


                        // 1 只有一个用户在，2 不能是用户创建的房间 3,里面不能是机器人
                        if (room.getRoomOnline() == 1 && !room.isUserCreate() && !room.isHasRobot()) {
                            GameRoom gameRoom = rooms.get(room.getGameNumber());
                            if (gameRoom == null || gameRoom.isStart()) {// 已经开局了不能加入
                                continue;
                            }
                            if (gameRoom.getDealerSession() == null || !gameRoom.getDealerSession().isOpen()) {// 用户没有掉线
                                continue;
                            }
                            log.debug("机器人进房间了");

                            room.joinRoom(null); // 机器人没有seesion
                            //
                            String userToken = queue.take();
                            UserInfos user = selectUserInfo(userToken);
                            room.setPlayerUser(user);

                            room.setHasRobot(true);

                            //
                            MessageDTO messageResult = new MessageDTO(ActionEnums.USER_JOIN_ROOM.getType(), user);
                            // 告诉房主有人进来了，并且把信息把用户信息给他
                            room.toDealer(messageResult);
                            room.robbotReady(userToken);
                        }
                    }

                } catch (Throwable e) {
                    log.error("机器人进入房间失败", e);
                }

            }
        }

    }

    public void put(String token) {
        queue.add(token);
    }


}