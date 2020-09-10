package com.ytz.rooms;


import com.ytz.bean.SysUser;
import com.ytz.core.GameRoom;
import com.ytz.core.RobotMonitor;
import com.ytz.core.TimeOutMonitor;
import com.ytz.enums.ActionEnums;
import com.ytz.enums.RoomTypeEnum;
import com.ytz.pojo.MatchRoomDTO;
import com.ytz.pojo.MessageDTO;
import com.ytz.pojo.UserInfos;
import com.ytz.service.UserInfoService;
import com.ytz.util.JsonMapper;
import com.ytz.util.MobileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RoomHandler {


    private Map<String, GameRoom> rooms = new ConcurrentHashMap<String, GameRoom>();
    private static Map<WebSocketSession, String> sessionNumber = new ConcurrentHashMap<WebSocketSession, String>();
    public static Map<String, String> tokenRooms = new ConcurrentHashMap<String, String>();

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RobotMonitor robotMonitor;
    //加载用户超时监控
    @Autowired
    private TimeOutMonitor timeOutMonitor;

    public GameRoom createRoom(WebSocketSession session, MessageDTO message) throws IOException, InterruptedException {
        UserInfos userInfo = selectUserInfo(message.getTokenId());

        if (userInfo == null) {
            session.sendMessage(new TextMessage(
                    JsonMapper.toJsonString(new MessageDTO(ActionEnums.LOGOUT.getType(), "登陆失效！", true))));
            return null;
        }

        if (Integer.parseInt(userInfo.getMoney()) < RoomTypeEnum.getByType((String) message.getData()).getMoney()) {
            MessageDTO resultMsg = new MessageDTO();
            resultMsg.setData("余额不足!");
            resultMsg.setStatus(false);
            session.sendMessage(new TextMessage(JsonMapper.toJsonString(resultMsg)));
            return null;
        }
        // 创建房间
        int number = creatRoomNumber();
        GameRoom gameRoom = new GameRoom();
        gameRoom.setRoomType(RoomTypeEnum.getByType((String) message.getData()));
        gameRoom.setDealerUser(userInfo);
        gameRoom.setUserCreate(true);
        //gameRoom.setUserFreeTime(System.currentTimeMillis());

        log.debug("创建房间号" + number);
        gameRoom.setGameNumber(number + "", session);
        rooms.put(number + "", gameRoom);
        sessionNumber.put(session, number + "");
        tokenRooms.put(message.getTokenId(), number + "");
        session.sendMessage(new TextMessage(
                JsonMapper.toJsonString(new MessageDTO(ActionEnums.CREATE_NEW_ROOM.getType(),
                        new MatchRoomDTO(number + "", gameRoom.getRoomType().getName(), gameRoom.getRoomType().getMoney())

                ))));
        return gameRoom;
    }

    private int creatRoomNumber() {
        // 创建唯一的房间号
        Random random = new Random();
        int nextInt;
        while (true) {
            nextInt = random.nextInt(900000) + 100000;
            if (rooms.get(nextInt) == null) {
                return nextInt;
            }
        }
    }

    public void receiveMessage(WebSocketSession session, String request) throws IOException {
        try {
            // 信息接收，判断是对房间操作还是对游戏对局操作
            MessageDTO message = JsonMapper.getInstance().fromJson(request, MessageDTO.class);
            if (ActionEnums.HEART_BEAT.getType().equals(message.getType())) {// 心跳处理
                //String roomId = sessionNumber.get(session);
                if (StringUtils.isBlank(message.getTokenId())) {
                    session.sendMessage(new TextMessage(
                            JsonMapper.toJsonString(new MessageDTO(ActionEnums.HEART_BEAT.getType(), ""))));
                    return;
                }
                // String roomId = tokenRooms.get(message.getTokenId());
                String roomId = sessionNumber.get(session);
                session.sendMessage(new TextMessage(
                        JsonMapper.toJsonString(new MessageDTO(ActionEnums.HEART_BEAT.getType(), roomId + ""))));
                return;
            } else if (ActionEnums.CREATE_NEW_ROOM.getType().equals(message.getType())) {// 创建房间请求
                createRoom(session, message);
            } else if (ActionEnums.JOIN_ROOM.getType().equals(message.getType())) {// 加入房间请求
                joinRoom(session, message);
            } else if (ActionEnums.MATCH_ROOM.getType().equals(message.getType())) {
                matching(session, message);
            } else if (ActionEnums.GET_USER_INFO.getType().equals(message.getType())) {
                getUserInfo(session, message);
            } else {// 对房间内游戏对局进行请求
                //GameRoom gameRoom2 = null;
               /* if (message.getRoomId() != null) {
                    gameRoom2 = rooms.get(message.getRoomId() + "");
                } else {
                	
                	if(roomId != null){
                		gameRoom2 = rooms.get(roomId);
                	}
                    
                }*/
                String roomId = sessionNumber.get(session);
                //String roomId = tokenRooms.get(message.getTokenId());
                if (roomId == null && !ActionEnums.USER_LEAVE_ROOM.getType().equals(message.getType())) {
                    session.sendMessage(new TextMessage(JsonMapper
                            .toJsonString(new MessageDTO(ActionEnums.USER_NOT_IN_ROOM.getType(), "你不在当前房间内!", false))));
                    return;
                }
                GameRoom gameRoom2 = rooms.get(roomId);
                if (gameRoom2 == null) {
                    session.sendMessage(new TextMessage(JsonMapper
                            .toJsonString(new MessageDTO(ActionEnums.MESSAGE.getType(), "房间已解散，请重新进入房间!", false))));
                    return;
                }

                //判断用户是否还在房间内,有用户会切换页面，导致退出，但是游戏界面还没有退出
                if ((gameRoom2.getPlayerUser() != null && !message.getTokenId().equals(gameRoom2.getPlayerUser().getTokenid())) &&
                        (gameRoom2.getDealerUser() != null && !message.getTokenId().equals(gameRoom2.getDealerUser().getTokenid()))
                ) {
                    session.sendMessage(new TextMessage(JsonMapper
                            .toJsonString(new MessageDTO(ActionEnums.USER_NOT_IN_ROOM.getType(), "你不在当前房间内!", true))));
                    return;
                }

                UserInfos userInfo = selectUserInfo(message.getTokenId());
                if (userInfo == null) {//用户过期了
                    session.sendMessage(new TextMessage(
                            JsonMapper.toJsonString(new MessageDTO(ActionEnums.LOGOUT.getType(), "登陆失效！", true))));
                    message.setType(ActionEnums.USER_LEAVE_ROOM.getType());
                    return;
                }
                //主要业务逻辑处理
                gameRoom2.messageHandler(session, message, userInfo);
                if (ActionEnums.USER_LEAVE_ROOM.getType().equals(message.getType())) {
                    sessionNumber.remove(session);
                    tokenRooms.remove(message.getTokenId());
                    if (gameRoom2.isHasRobot()) {//如果有机器人，真实用户又退出了，就回收房间
                        log.info("回收机器人" + gameRoom2.getDealerUser().getTokenid());
                        robotMonitor.put(gameRoom2.getDealerUser().getTokenid());//回收机器人,这里取dealer 是因为已经交换了
                        log.info("移除房间0" + gameRoom2.getGameNumber());
                        rooms.remove(gameRoom2.getGameNumber());

                    }
                }
            }
        } catch (Throwable t) {
            log.error("消息处理异常：", t);
            session.sendMessage(new TextMessage(
                    JsonMapper.toJsonString(new MessageDTO(ActionEnums.MESSAGE.getType(), "系统繁忙！", false))));
        }
    }

    private void joinRoom(WebSocketSession session, MessageDTO message) throws IOException, InterruptedException {
        // 加入房间
        String number = (String) message.getData();
        GameRoom gameRoom = rooms.get(number);
        System.out.println(gameRoom + "房间");
        MessageDTO dto = new MessageDTO();
        dto.setType(ActionEnums.JOIN_ROOM.getType());
        if (gameRoom == null || !gameRoom.isUserCreate()) {
            dto.setStatus(false);
            dto.setData("房间不存在!");
            session.sendMessage(new TextMessage(JsonMapper.toJsonString(dto)));
        } else {
            if (gameRoom.getRoomOnline() >= 2) {
                dto.setStatus(false);
                dto.setData("房间人数已满!");
                session.sendMessage(new TextMessage(JsonMapper.toJsonString(dto)));
            } else {
                gameRoom.joinRoom(session);
                sessionNumber.put(session, number);
                tokenRooms.put(message.getTokenId(), number);
                UserInfos user = selectUserInfo(message.getTokenId());
                gameRoom.setPlayerUser(user);

                dto.setData(new MatchRoomDTO(number + "", gameRoom.getRoomType().getName(), gameRoom.getRoomType().getMoney()));
                session.sendMessage(new TextMessage(JsonMapper.toJsonString(dto)));

                //
                MessageDTO messageResult = new MessageDTO(ActionEnums.USER_JOIN_ROOM.getType(), user);
                // 告诉房主有人进来了，并且把信息把用户信息给他
                gameRoom.toDealer(messageResult);

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

    private void getUserInfo(WebSocketSession session, MessageDTO dto) {

        UserInfos userInfo = selectUserInfo(dto.getTokenId());
        try {
            if (userInfo != null) {
                session.sendMessage(new TextMessage(
                        JsonMapper.toJsonString(new MessageDTO(ActionEnums.GET_USER_INFO.getType(), userInfo))));
            }

            return;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 匹配
     *
     * @throws InterruptedException
     * @throws IOException
     * @author Bob
     * @Since 2018年11月2日 下午3:23:19
     */
    private void matching(WebSocketSession session, MessageDTO message) throws IOException, InterruptedException {
        String matchType = (String) message.getData();
        MessageDTO resultMsg = new MessageDTO();
        resultMsg.setType(ActionEnums.MATCH_ROOM.getType());
        // UserInfo u = userInfoService.getUserByToken(message.getTokenId());
        UserInfos userInfo = selectUserInfo(message.getTokenId());
        if (userInfo == null) {
            session.sendMessage(new TextMessage(
                    JsonMapper.toJsonString(new MessageDTO(ActionEnums.LOGOUT.getType(), "登陆失效！", true))));
            return;
        }
        String roomRecordId = sessionNumber.get(session);
        //String roomRecordId = tokenRooms.get(message.getTokenId());
        if (StringUtils.isNoneBlank(roomRecordId)) {//如果已經在房间内告诉他不可以重复进入
            MessageDTO dto = new MessageDTO();
            GameRoom room = rooms.get(roomRecordId);
            if (room != null) {
                dto.setData(new MatchRoomDTO(roomRecordId, room.getRoomType().getName(), room.getRoomType().getMoney()));
                dto.setType(ActionEnums.MATCH_ROOM.getType());
                session.sendMessage(new TextMessage(JsonMapper.toJsonString(dto)));// 告诉他房间号 
                return;
            }
        }

        int money = Integer.parseInt(userInfo.getMoney());
        if ("auto".equals(matchType)) {// 随机匹配
            if (money > 200) {
                matchType = "top";
            } else if (money > 100) {
                matchType = "high";
            } else if (money > 50) {
                matchType = "primary";
            } else if (money < 10) {
                resultMsg.setData("余额不足!");
                resultMsg.setStatus(false);
                session.sendMessage(new TextMessage(JsonMapper.toJsonString(resultMsg)));
                return;
            }
            boolean matchFlag = false;
            //首先
            if (RoomTypeEnum.TOP.getType().equals(matchType)) {// top场次匹配
                matchFlag = matchRoomHander2(session, message, RoomTypeEnum.TOP, userInfo);
                if (!matchFlag) {
                    matchFlag = matchRoomHander2(session, message, RoomTypeEnum.HIGH, userInfo);
                    if (!matchFlag) {
                        matchFlag = matchRoomHander2(session, message, RoomTypeEnum.PRIMARY, userInfo);
                    }
                }

            } else if (RoomTypeEnum.HIGH.getType().equals(matchType)) {// 高级场匹配
                matchFlag = matchRoomHander2(session, message, RoomTypeEnum.HIGH, userInfo);
                if (!matchFlag) {
                    matchFlag = matchRoomHander2(session, message, RoomTypeEnum.PRIMARY, userInfo);
                }


            } else if (RoomTypeEnum.PRIMARY.getType().equals(matchType)) {//初级场匹配
                matchFlag = matchRoomHander2(session, message, RoomTypeEnum.PRIMARY, userInfo);
            }


            if (!matchFlag) {
                // 没有找到有座位的房间
                matchCreateRoom(session, message, RoomTypeEnum.getByType(matchType), userInfo);
            }


            return;
        }

        if (money < RoomTypeEnum.getByType(matchType).getMoney()) {
            resultMsg.setData("余额不足!");
            resultMsg.setStatus(false);
            try {
                session.sendMessage(new TextMessage(JsonMapper.toJsonString(resultMsg)));
            } catch (Throwable t) {

            }
            return;
        }

        if (RoomTypeEnum.PRIMARY.getType().equals(matchType)) {//初级场匹配

            matchRoomHander(session, message, RoomTypeEnum.PRIMARY, userInfo);
        } else if (RoomTypeEnum.HIGH.getType().equals(matchType)) {// 高级场匹配

            matchRoomHander(session, message, RoomTypeEnum.HIGH, userInfo);
        } else if (RoomTypeEnum.TOP.getType().equals(matchType)) {// top场次匹配
            matchRoomHander(session, message, RoomTypeEnum.TOP, userInfo);
        }

    }

    private void matchRoomHander(WebSocketSession session, MessageDTO message, RoomTypeEnum roomType, UserInfos user)
            throws IOException, InterruptedException {
        // UserInfos user = selectUserInfo(message.getTokenId());
        if (rooms.isEmpty()) {//
            matchCreateRoom(session, message, roomType, user);
            return;
        }
        Iterator<String> it = rooms.keySet().iterator();
        while (it.hasNext()) {
            String roomKey = it.next();
            GameRoom room = rooms.get(roomKey);

            if (room.getRoomOnline() == 1 && roomType.equals(room.getRoomType()) && !room.isUserCreate()) {
                GameRoom gameRoom = rooms.get(room.getGameNumber());
                if (gameRoom == null || gameRoom.isStart()) {// 已经开局了不能加入
                    continue;
                }
                // 自己不能跟比赛
                if (gameRoom.getDealerUser() != null && message.getTokenId().equals(gameRoom.getDealerUser().getTokenid())) {
                    continue;
                }
                if (gameRoom.getDealerSession() == null || !gameRoom.getDealerSession().isOpen()) {
                    continue;
                }

                room.joinRoom(session);
                //
                sessionNumber.put(session, room.getGameNumber());
                tokenRooms.put(message.getTokenId(), room.getGameNumber());
                room.setPlayerUser(user);
                MessageDTO dto = new MessageDTO();
                dto.setData(new MatchRoomDTO(room.getGameNumber(), room.getRoomType().getName(), room.getRoomType().getMoney()));
                dto.setType(ActionEnums.MATCH_ROOM.getType());
                try {
                    session.sendMessage(new TextMessage(JsonMapper.toJsonString(dto)));// 告诉他房间号
                } catch (Throwable t) {

                }
                //
                MessageDTO messageResult = new MessageDTO(ActionEnums.USER_JOIN_ROOM.getType(), user);
                // 告诉房主有人进来了，并且把信息把用户信息给他
                room.toDealer(messageResult);
                return;
            }
        }

        // 没有找到有座位的房间
        matchCreateRoom(session, message, roomType, user);

    }


    private boolean matchRoomHander2(WebSocketSession session, MessageDTO message, RoomTypeEnum roomType, UserInfos user)
            throws IOException, InterruptedException {
        // UserInfos user = selectUserInfo(message.getTokenId());
        if (rooms.isEmpty()) {//
            matchCreateRoom(session, message, roomType, user);
            return true;
        }
        Iterator<String> it = rooms.keySet().iterator();
        while (it.hasNext()) {
            String roomKey = it.next();
            GameRoom room = rooms.get(roomKey);
            if (room.getRoomOnline() == 0) {// 如果没有人了就移除掉
                log.info("移除房间2:" + room.getGameNumber());
                it.remove();
            } else if (room.getRoomOnline() == 1 && roomType.equals(room.getRoomType()) && !room.isUserCreate()) {
                GameRoom gameRoom = rooms.get(room.getGameNumber());
                if (gameRoom == null || gameRoom.isStart()) {// 已经开局了不能加入
                    continue;
                }
                // 自己不能跟比赛
                if (gameRoom.getDealerUser() == null || message.getTokenId().equals(gameRoom.getDealerUser().getTokenid())) {
                    continue;
                }
                if (gameRoom.getDealerSession() == null || !gameRoom.getDealerSession().isOpen()) {
                    continue;
                }

                room.joinRoom(session);
                //
                sessionNumber.put(session, room.getGameNumber());
                tokenRooms.put(message.getTokenId(), room.getGameNumber());
                room.setPlayerUser(user);
                MessageDTO dto = new MessageDTO();
                dto.setData(new MatchRoomDTO(room.getGameNumber(), room.getRoomType().getName(), room.getRoomType().getMoney()));
                dto.setType(ActionEnums.MATCH_ROOM.getType());
                try {
                    session.sendMessage(new TextMessage(JsonMapper.toJsonString(dto)));// 告诉他房间号
                } catch (Throwable T) {

                }
                //
                MessageDTO messageResult = new MessageDTO(ActionEnums.USER_JOIN_ROOM.getType(), user);
                // 告诉房主有人进来了，并且把信息把用户信息给他
                room.toDealer(messageResult);
                return true;
            }
        }


        return false;
    }

    private GameRoom matchCreateRoom(WebSocketSession session, MessageDTO message, RoomTypeEnum roomType, UserInfos user)
            throws IOException, InterruptedException {

        // 创建房间
        int number = creatRoomNumber();
        GameRoom gameRoom = new GameRoom();
        gameRoom.setDealerUser(user);

        log.debug("没有匹配到自动创建了房间" + number);
        gameRoom.setGameNumber(number + "", session);
        gameRoom.setRoomType(roomType);
        gameRoom.setUserFreeTime(System.currentTimeMillis());

        rooms.put(number + "", gameRoom);
        sessionNumber.put(session, number + "");
        tokenRooms.put(message.getTokenId(), number + "");
        session.sendMessage(new TextMessage(
                JsonMapper.toJsonString(new MessageDTO(ActionEnums.MATCH_ROOM.getType(),
                        new MatchRoomDTO(gameRoom.getGameNumber(), gameRoom.getRoomType().getName(), gameRoom.getRoomType().getMoney())
                ))));


        return gameRoom;

    }

    public void error(WebSocketSession session) throws IOException, InterruptedException {
        // TODO Auto-generated method stub
        // session异常
        String number = sessionNumber.get(session);
        if (number == null) {// 还未加入房间，不用处理异常
            return;
        }

        GameRoom gameRoom = rooms.get(number);
        String tokenid = null;
        if (gameRoom != null) {
            //找出用户的tokenid
            if (session == gameRoom.getDealerSession()) {
                if (gameRoom.getDealerUser() != null) {
                    tokenid = gameRoom.getDealerUser().getTokenid();
                }
            } else if (session == gameRoom.getPlayerSession()) {
                if (gameRoom.getPlayerUser() != null) {
                    tokenid = gameRoom.getPlayerUser().getTokenid();
                }
            }
            if (gameRoom.getRoomOnline() == 1) {
                // 如果房间没有人了，直接删除房间
                rooms.remove(number);
                log.debug("移除房间号" + number);
            } else if (gameRoom.getRoomOnline() == 2) {
                // gameRoom.forceOut(session);// 强制游戏退出
                gameRoom.userOffLine(session);
                // gameRoom.removeSession(session);// 删除
                //如果已经在房间通知
            } else {
                // 房间两个人，移除异常的session
                //  gameRoom.removeSession(session);
                gameRoom.userOffLine(session);
            }
        }


        sessionNumber.remove(session);// session的房间号移除
    }

    public Map<String, GameRoom> getRooms() {
        return rooms;
    }

    public void setRooms(Map<String, GameRoom> rooms) {
        this.rooms = rooms;
    }


}
