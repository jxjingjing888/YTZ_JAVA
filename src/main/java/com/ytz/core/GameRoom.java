package com.ytz.core;


import com.ytz.bean.UserInfo;
import com.ytz.enums.ActionEnums;
import com.ytz.enums.RoomTypeEnum;
import com.ytz.pojo.*;
import com.ytz.util.JsonMapper;
import com.ytz.util.MobileUtil;
import com.ytz.util.RuleUtils;
import com.ytz.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

@Slf4j
public class GameRoom implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Game game = new Game();
    private String gameNumber;// 房间号
    private int roomOnline = 0;
    private WebSocketSession playerSession;
    private WebSocketSession dealerSession;
    private UserInfos playerUser;
    private UserInfos dealerUser;
    private Long callPointTimeOut = null;//游戏开始时间
    private Long userFreeTime = null;//用户空闲开始时间
    private boolean dealerRound = true;//谁叫点的回合
    private boolean isStart = false;//房间是否已经开始游戏了?
    private boolean isUserCreate = false;//是否是用户创建的房间
    private boolean dealerFirst = true;//庄家开始？ true：是 flase：闲家开始
    private boolean playerUserOffLine = false;//玩家是否掉线了？
    private boolean dealerUserOffLine = false;//玩家是否掉线了？
    private RoomTypeEnum roomType;
    /**
     * 房间是否有机器人？ 机器人不会是庄家
     */
    private boolean hasRobot = false;
    private int round = 0;

    private boolean playerReady;
    private boolean dealerReady;

    public void setGameNumber(String number, WebSocketSession session) {
        this.gameNumber = number;
        this.roomOnline = 1;
        dealerSession = session;
        toDealer(new MessageDTO("", gameNumber));
    }

    public boolean joinRoom(WebSocketSession session){
        // 第二个人加入房间，可以开始游戏
        roomOnline++;
        playerSession = session;
        return true;
    }

    public int getRoomOnline() {// 返回房间人数
        return roomOnline;
    }

    private void beginGame() throws IOException, InterruptedException {
        game.init();// 游戏初始化
        isStart = true;
        userFreeTime = null;
        callPointTimeOut = System.currentTimeMillis();
        // 告诉玩家游戏点数
        toDealer(new MessageDTO(ActionEnums.GAME_START.getType(), game.getDealer().getDiceArray()));
        // 告诉玩家游戏点数
        toPlayer(new MessageDTO(ActionEnums.GAME_START.getType(), game.getPlayer().getDiceArray()));
        if(dealerFirst){
        	 // 庄家先叫点数
        	dealerRound = true;
            toDealer(new MessageDTO(ActionEnums.CALL_POINTS.getType(), ""));
        }else{
        	 // 闲家先叫点数
        	dealerRound = false;
        	if(hasRobot){//如果有机器人
        		robbotCallPoint(true);
        	}
        	toPlayer(new MessageDTO(ActionEnums.CALL_POINTS.getType(), ""));
        }
       

    }

    public boolean toDealer(MessageDTO message)  {
        if (dealerSession != null) {
            // 发送消息给庄家
            try {
            	Thread.sleep(100);
                dealerSession.sendMessage(new TextMessage(JsonMapper.toJsonString(message)));
            } catch (Exception e) {
            	log.debug("------庄家失联");
               // removeSession(dealerSession);
            	userOffLine(this.dealerSession);
                return false;
            }
        }
        return true;
    }

    public boolean toPlayer(MessageDTO message){

        if (playerSession != null) {
            // 发送信息给玩家
            try {
            	Thread.sleep(100);
                playerSession.sendMessage(new TextMessage(JsonMapper.toJsonString(message)));
            } catch (Exception e) {
            	log.debug("------玩家失联");
                //removeSession(playerSession);
            	userOffLine(this.playerSession);
                return false;
            }
        }
        return true;
    }

    public void messageHandler(WebSocketSession session, MessageDTO message,UserInfos  userInfo) throws IOException, InterruptedException {
        log.debug("--->消息处理");
        //用户重连修正操作 ！
        
        /*boolean result = userScoketSessionUpdate(message.getTokenId(), session);
        if(!result){
        	return;
        }*/
        
        if (ActionEnums.TALK.getType().equals(message.getType())) {
            talk(session, message);
        } else if (ActionEnums.USER_READY.getType().equals(message.getType())) {// 进行准备
            userReady(session, message);
        } else if (ActionEnums.SHOW_DOWN.getType().equals(message.getType())) {
            showdown(session, message);
        } else if (ActionEnums.USER_LEAVE_ROOM.getType().equals(message.getType())) {
            // 判断是否是强退
            forceOut(session);
            removeSession(session);
        } else if (ActionEnums.CALL_POINTS.getType().equals(message.getType())) {
            callPoints(session, message);
        } else if (ActionEnums.GET_OHTER_PLAYER_INFO.getType().equals(message.getType())) {
            getOhterPlayerInfo(session, message);
        } else if (ActionEnums.CALL_POINTS_READY.getType().equals(message.getType())) {
            // callPointReady(session, message);
        } else if (ActionEnums.USER_TIME_OUT.getType().equals(message.getType())) {
            userTimeOut(session, message);
        }

    }
    
    /**
     * 用户重连session 修正
     * @param tokenId
     * @param session         
     * @author  Bob
     * @Since 2019年1月4日 下午3:30:19
     *
     */
    private boolean userScoketSessionUpdate(String tokenId, WebSocketSession session) {
        if (playerUser!=null && tokenId.equals(playerUser.getTokenid())) {// 闲家修复
            if (session != this.playerSession) {
                playerSession = session;
                this.playerUserOffLine = false;//用户重连成功了
            }
        } else if (tokenId.equals(dealerUser.getTokenid())) {// 庄家修复
            if (session != this.dealerSession) {
                dealerSession = session;
                this.dealerUserOffLine = false;//用户重连成功了
            }
        } else {
        	 log.error("未找到用户信息");
        	try {
				session.sendMessage(new TextMessage(JsonMapper
				        .toJsonString(new MessageDTO(ActionEnums.USER_NOT_IN_ROOM.getType(), "你不在当前房间内!", false))));
			} catch (IOException e) {
			}
            return false;
           
        }
        return true;
    }

    private UserInfos selectUserInfo(String token) {
        UserInfo u = GameUtils.getUserByToken(token);
        UserInfos userInfo = null;
        if (u != null) {
            userInfo = new UserInfos();
            userInfo.setUserId(u.getId());
            userInfo.setTokenid(u.getTokenid());
            userInfo.setRealName(u.getMobilePhone());
            userInfo.setPhone(MobileUtil.mobileEncrypt(u.getMobilePhone()));
            userInfo.setMoney(u.getDiamondBalance() == null ? "" : u.getDiamondBalance().intValue() + "");
            userInfo.setImg(u.getFaceImg());
        }
        return userInfo;
    }

    private void userReady(WebSocketSession session, MessageDTO message) throws IOException, InterruptedException {
        UserInfo u = GameUtils.getUserByToken(message.getTokenId());
        // 首先判断金额足不足
        if (u.getDiamondBalance().intValue() < this.roomType.getMoney() || ("0").equals(u.getStatus())) {
            message.setData("余额不足!");
            message.setStatus(true);
            message.setType(ActionEnums.NOT_ENOUGH_BALANCE.getType());
            session.sendMessage(new TextMessage(JsonMapper.toJsonString(message)));
            return;
        }

        if(session == null){//机器人等于null
        	game.getPlayer().setTokenId(message.getTokenId());
            this.playerReady = true;
            boolean result =  toDealer(new MessageDTO(ActionEnums.USER_READY.getType(), ""));
            if(!result){
          	   return;
             }
        } else if (session == dealerSession) {
            game.getDealer().setTokenId(message.getTokenId());
            this.dealerReady = true;
            boolean result =  toPlayer(new MessageDTO(ActionEnums.USER_READY.getType(), ""));// 告诉对方已经准备好
            if(!result){
         	   return;
            }
        } else {
            game.getPlayer().setTokenId(message.getTokenId());
            this.playerReady = true;
            boolean result =  toDealer(new MessageDTO(ActionEnums.USER_READY.getType(), ""));
            if(!result){
         	   return;
            }
        }
        if (this.playerReady && this.dealerReady) {

            // T玩家 扣砖石
            GameReward reward = new GameReward();
            reward.setCostRate(roomType.getMoney() + "");// 钻石消耗，初级场 20钻石，中级场
                                                         // 50钻石，高级场 100钻石
            reward.setIsSuccess("0");// 0表示扣钻石，1表示赢钻石
            reward.setLevelRate(null);// 赢取钻石，初级场扣4，中级场扣10，高级场扣20
            reward.setName("dice");// 骰子
            reward.setIsDeduct("0");// 默认为0
            reward.setPercentage("1"); // 倍数，默认为1
            reward.setTokenid(game.getPlayer().getTokenId());
            reward.setTypeName(roomType.getVal());// 1为初级场，2为中级场，3为高级场
            reward.setUserId(message.getUserId());
            GameExpense expense = GameUtils.countDiamondByToken(reward);

            if (expense == null) {// 为禁用用户或者余额不足
                // result.put("code", "500");//禁用
                log.error("玩家为禁用用户或者余额不足" + message.getUserId());
            	
            }

            // 庄家扣砖石
            GameReward dealerReward = new GameReward();
            dealerReward.setCostRate(roomType.getMoney() + "");// 钻石消耗，初级场
                                                               // 20钻石，中级场
                                                               // 50钻石，高级场 100钻石
            dealerReward.setIsSuccess("0");// 0表示扣钻石，1表示赢钻石
            dealerReward.setLevelRate(null);// 赢取钻石，初级场扣4，中级场扣10，高级场扣20
            dealerReward.setName("dice");// 骰子
            dealerReward.setIsDeduct("0");// 默认为0
            dealerReward.setPercentage("1"); // 倍数，默认为1
            dealerReward.setTokenid(game.getDealer().getTokenId());
            dealerReward.setTypeName(roomType.getVal());// 1为初级场，2为中级场，3为高级场
            dealerReward.setUserId(message.getUserId());
            expense = GameUtils.countDiamondByToken(dealerReward);
            if (expense == null) {// 为禁用用户或者余额不足
                // result.put("code", "500");//禁用
                log.error("玩家为禁用用户或者余额不足" + message.getUserId());
            }
            beginGame();
            // 告诉他们现在的金额
            toDealer(new MessageDTO(ActionEnums.USER_INFO_CHANGE.getType(),
                    selectUserInfo(game.getDealer().getTokenId())));
            toPlayer(new MessageDTO(ActionEnums.USER_INFO_CHANGE.getType(),
                    selectUserInfo(game.getPlayer().getTokenId())));

        }
    }

    private void talk(WebSocketSession session, MessageDTO dto) throws IOException, InterruptedException {
        if (session == dealerSession) {
            toPlayer(dto);
        } else {
            toDealer(dto);
        }

    }

    private void showdown(WebSocketSession session, MessageDTO dto)  {
        try {
            if (this.round == 0) {
                return;
            }
            if (!isStart) {// 如果游戏已关闭了 不能结算
                session.sendMessage(new TextMessage(
                        JsonMapper.toJsonString(new MessageDTO(ActionEnums.MESSAGE.getType(), "游戏已结束!", false))));

                return;
            }
            // 游戏结束业务逻辑
            this.isStart = false;
            this.callPointTimeOut = null;
            this.playerReady = false;
            this.dealerReady = false;
            this.round = 0;
            MatchResult result = null;
            if (session == dealerSession) {
                if (this.dealerRound == false) {
                    log.debug("庄家错误开点回合");
                    return;
                }
                result = RuleUtils.match(game.getDealer().getDiceArray(), game.getPlayer().getDiceArray(),
                        game.getPlayer().getNum(), game.getPlayer().getDiceNum(), game.getPlayer().isZai(),
                        game.getPlayer().isFei());

                // 优先结算，以防万一
                if (result.isStatus()) {// 赢了
                    addMoney(game.getDealer().getTokenId(), game.getDealer().getUserId());
                    toDealer(new MessageDTO(ActionEnums.USER_INFO_CHANGE.getType(),
                            selectUserInfo(game.getDealer().getTokenId())));
                    dealerFirst = false;
                } else {
                    dealerFirst = true;
                    addMoney(game.getPlayer().getTokenId(), game.getPlayer().getUserId());
                    // 对手赢了
                    toPlayer(new MessageDTO(ActionEnums.USER_INFO_CHANGE.getType(),
                            selectUserInfo(game.getPlayer().getTokenId())));

                }

                result.setPlayerDices(game.getPlayer().getDiceArray());// 把对方的骰子给自己看
                // 发送结果给自己
                toDealer(new MessageDTO(ActionEnums.SHOW_DOWN.getType(), result));

                // 发送结果给对方
                result.setPlayerDices(game.getDealer().getDiceArray());// 告诉对方我的骰子
                result.setStatus(!result.isStatus());// 把相反的结果告诉对家
                toPlayer(new MessageDTO(ActionEnums.SHOW_DOWN.getType(), result));

            } else {
                if (this.dealerRound == true) {
                    log.debug("闲家错误开点回合");
                    return;
                }
                result = RuleUtils.match(game.getDealer().getDiceArray(), game.getPlayer().getDiceArray(),
                        game.getDealer().getNum(), game.getDealer().getDiceNum(), game.getDealer().isZai(),
                        game.getDealer().isFei());
                // 优先结算，以防万一
                if (result.isStatus()) {// 赢了
                    dealerFirst = true;
                    addMoney(game.getPlayer().getTokenId(), game.getPlayer().getUserId());
                    toPlayer(new MessageDTO(ActionEnums.USER_INFO_CHANGE.getType(),
                            selectUserInfo(game.getPlayer().getTokenId())));
                } else {
                    dealerFirst = false;
                    addMoney(game.getDealer().getTokenId(), game.getDealer().getUserId());
                    toDealer(new MessageDTO(ActionEnums.USER_INFO_CHANGE.getType(),
                            selectUserInfo(game.getDealer().getTokenId())));

                }

                result.setPlayerDices(game.getDealer().getDiceArray());// 把对方的骰子给我看
                toPlayer(new MessageDTO(ActionEnums.SHOW_DOWN.getType(), result));

                result.setPlayerDices(game.getPlayer().getDiceArray());// 告诉对方我的骰子
                result.setStatus(!result.isStatus());// 把相反的结果告诉对家
                toDealer(new MessageDTO(ActionEnums.SHOW_DOWN.getType(), result));

            }

            this.game.gameOver();
            // 机器人又开始准备了
            if (hasRobot) {
                robbotReady(this.playerUser.getTokenid());
            }
            // 看下有没有用户掉线
            if (this.dealerUserOffLine) {
                log.debug("庄家掉线了" + this.getGameNumber());
                removeSession(dealerSession);
            }
            if (this.playerUserOffLine) {
                log.debug("闲家掉线了" + this.getGameNumber());
                removeSession(this.playerSession);
            }

        } catch (Throwable t) {

        }
    }

    private void addMoney(String tokenId, String userId) {

        // 增加砖石
        GameReward reward = new GameReward();
        reward.setCostRate(roomType.getMoney() + "");// 钻石消耗，初级场 20钻石，中级场
                                                     // 50钻石，高级场 100钻石
        reward.setIsSuccess("1");// 0表示扣钻石，1表示赢钻石
        reward.setLevelRate(((roomType.getMoney() * 2) - roomType.getCost()) + "");// 赢取钻石，初级场扣4，中级场扣10，高级场扣20
        reward.setName("dice");// 骰子
        reward.setIsDeduct("0");// 默认为0
        reward.setPercentage("1"); // 倍数，默认为1
        reward.setTokenid(tokenId);
        reward.setTypeName(roomType.getVal());// 1为初级场，2为中级场，3为高级场
        reward.setUserId(userId);
        GameExpense expense = GameUtils.countDiamondByToken(reward);
        if (expense == null) {// 为禁用用户或者余额不足
            // result.put("code", "500");//禁用
        }
    }

    /**
     * 用户叫点了
     * 
     * @param session
     * @param dto
     * @throws IOException
     * @throws InterruptedException
     */
    private void callPoints(WebSocketSession session, MessageDTO dto) throws IOException, InterruptedException {
        if (!isStart) {
            return;
        }
        if(!this.playerReady || !this.dealerReady){
            return;
        }
        CallPointDTO message = JsonMapper.getInstance().fromJson((String) dto.getData(), CallPointDTO.class);
        round ++ ;
        /*****************  庄家处理  *********************/
        if (this.dealerSession == session) {
            if(dealerRound == false){//防止恶意错误叫号
                log.debug("庄家错误回合");
                return;
            }
            Player dealer = game.getDealer();
            dealer.setDiceNum(message.getDiceNum());
            dealer.setNum(message.getNum());
            dealer.setZai(message.isZai());
            dealer.setFei(message.isFei());
            callPointTimeOut = System.currentTimeMillis();
            dealerRound = false;//切换到闲家回合
            toPlayer(new MessageDTO(ActionEnums.CALL_POINTS_READY.getType(), message));
           
            if(this.playerUserOffLine){
                showdown(this.playerSession, null);	
           }
           
            robbotCallPoint(false);//机器人逻辑处理
            return;
        }
        /*****************  闲家处理  *********************/
        if(dealerRound == true){//防止恶意错误叫号
            log.debug("闲家错误回合");
            return;
        }
        Player player = game.getPlayer();
        player.setDiceNum(message.getDiceNum());
        player.setNum(message.getNum());
        player.setZai(message.isZai());
        player.setFei(message.isFei());
        callPointTimeOut = System.currentTimeMillis();//更新用户叫点的时间
        dealerRound = true;  //切换到庄家回合
        toDealer(new MessageDTO(ActionEnums.CALL_POINTS_READY.getType(), message));
        if(this.dealerUserOffLine){
         showdown(this.dealerSession, null);	
        }
       
        
        
    }

    public WebSocketSession getLivingSession(WebSocketSession session) {
        // TODO Auto-generated method stub
        // 获得还在房间的session
        if (playerSession != session) {
            return playerSession;
        }
        return dealerSession;

    }

    private void getOhterPlayerInfo(WebSocketSession session, MessageDTO message)
            throws IOException, InterruptedException {
        if (playerSession == session) {
            // 获取庄家信息
            if (dealerUser != null) {
                toPlayer(new MessageDTO(ActionEnums.GET_OHTER_PLAYER_INFO.getType(), dealerUser));
            }
        } else {
            if (playerUser != null) {
                toDealer(new MessageDTO(ActionEnums.GET_OHTER_PLAYER_INFO.getType(), playerUser));
            }

        }

    }

    /**
     * 强制退出
     * 
     * @throws InterruptedException
     * @throws IOException
     */
    public void forceOut(WebSocketSession session) {
        if (!isStart) {
            return;
        }
        this.setStart(false);// 强制结束游戏
        this.callPointTimeOut = null;
        this.playerReady = false;
        this.dealerReady = false;
        this.round = 0;
        MatchResult result = RuleUtils.match(game.getDealer().getDiceArray(), game.getPlayer().getDiceArray(),
                game.getPlayer().getNum(), game.getPlayer().getDiceNum(), game.getPlayer().isZai(),
                game.getPlayer().isFei());
        if (session == dealerSession) {
            addMoney(game.getPlayer().getTokenId(), game.getPlayer().getUserId());
            toPlayer(new MessageDTO(ActionEnums.USER_INFO_CHANGE.getType(),
                    selectUserInfo(game.getPlayer().getTokenId())));
            toPlayer(new MessageDTO(ActionEnums.FORCE_SHOW_DOWN.getType(), result));

        } else {
            addMoney(game.getDealer().getTokenId(), game.getDealer().getUserId());
            toDealer(new MessageDTO(ActionEnums.USER_INFO_CHANGE.getType(),
                    selectUserInfo(game.getDealer().getTokenId())));
            toDealer(new MessageDTO(ActionEnums.FORCE_SHOW_DOWN.getType(), result));

        }

    }
    /**
     * 用户掉线了
     * @throws InterruptedException 
     * @throws IOException 
     */
    public void userOffLine(WebSocketSession session){
        try {
            if (!isStart) {
                log.debug("--------------removeSession");
                removeSession(session);
                // 游戏没有开始发消息告诉用户就可以了
                if (session == dealerSession) {
                    toPlayer(new MessageDTO(ActionEnums.USER_LEAVE_ROOM.getType(), ""));// 中途异常退出模块

                } else {
                    toDealer(new MessageDTO(ActionEnums.USER_LEAVE_ROOM.getType(), ""));// 中途异常退出模块
                }

                return;
            }
            // 游戏已经开始了的业务逻辑
            // 自己掉线用户的回合就直接开
            if (session == dealerSession) {
                this.dealerUserOffLine = true;
                if (dealerRound) {
                    showdown(session, null);
                }
                // toPlayer(new
                // MessageDTO(ActionEnums.USER_LEAVE_ROOM.getType(), ""));//
                // 中途异常退出模块

            } else {
                this.playerUserOffLine = true;
                if (!dealerRound) {
                    showdown(session, null);
                }
                // toDealer(new
                // MessageDTO(ActionEnums.USER_LEAVE_ROOM.getType(), ""));//
                // 中途异常退出模块
            }

        } catch (Throwable t) {
            log.debug("发消息异常0");
        }

    }

    /**
     * 用户超时
     * 
     * @param session
     * @throws IOException
     * @throws InterruptedException
     */
    public void userTimeOut(WebSocketSession session, MessageDTO dto) throws IOException, InterruptedException {
        if (!isStart) {
            return;
        }
        Player player = game.getPlayer();
        Player dealer = game.getDealer();
        CallPointDTO message = new CallPointDTO();
        String data = (String) dto.getData();
        this.callPointTimeOut = System.currentTimeMillis();
        round ++ ;
        /*****************  庄家回合处理 *************************/
        if (this.dealerSession == session) {
        	if(dealerRound == false){//防止恶意错误叫号
                log.debug("庄家超时错误回合");
                return;
            }
            //庄家处理
            if (player.getNum() == 14) {// 设置最大上线
                showdown(session, dto);
                this.dealerRound  = false;
                return;
            }
            this.dealerRound  = false;
            if ("isFirst".equals(data)) {
                dealer.setDiceNum(3);
                dealer.setNum(5);
                
            } else {
                dealer.setDiceNum(player.getDiceNum());
                dealer.setNum(player.getNum() + 1);
            	//showdown(session, dto);
               
            	// return;
            }

            dealer.setZai(player.isZai());
            dealer.setFei(player.isFei());

            message.setDiceNum(dealer.getDiceNum());
            message.setNum(dealer.getNum());
            message.setZai(dealer.isZai());
            message.setFei(dealer.isFei());

           
            toPlayer(new MessageDTO(ActionEnums.CALL_POINTS_READY.getType(), message));
            toDealer(new MessageDTO(ActionEnums.USER_TIME_OUT.getType(), message));//告诉自己超时了
            robbotCallPoint(false);//机器人逻辑处理
        	
            return;
        }
        /*****************  闲家回合处理 *************************/
        if(dealerRound == true){//防止恶意错误叫号
            log.debug("闲家超时错误回合");
            return;
        }
       
        if (dealer.getNum() == 14) {// 设置最大上线
            showdown(session, dto);
            this.dealerRound  = true;
            return;
        }
        this.dealerRound  = true;
        if ("isFirst".equals(data)) {
            player.setDiceNum(3);
            player.setNum(5);
        } else {
            player.setDiceNum(dealer.getDiceNum());
            player.setNum(dealer.getNum() + 1);
        	//showdown(session, dto);//超时直接开
        	 //return;
        }

        player.setZai(dealer.isZai());
        player.setFei(dealer.isFei());

        message.setDiceNum(player.getDiceNum());
        message.setNum(player.getNum());
        message.setZai(player.isZai());
        message.setFei(player.isFei());

        toDealer(new MessageDTO(ActionEnums.CALL_POINTS_READY.getType(), message));
        toPlayer(new MessageDTO(ActionEnums.USER_TIME_OUT.getType(), message));//告诉自己超时了
        

    }

    /**
     * 踢掉房间用户
     * @param session
     * @throws IOException
     * @throws InterruptedException
     */
    public void removeSession(WebSocketSession session) throws IOException, InterruptedException {
        // TODO Auto-generated method stub
        // 移除异常的session
        
        if (session == dealerSession) {
        	if(this.dealerUser !=null ){
        		RoomHandler.tokenRooms.remove(this.dealerUser.getTokenid());	
        	}
        	
            dealerSession = playerSession;
            dealerUser = playerUser;
            // 准备状态也要交换
            this.dealerReady = this.playerReady;
            dealerUserOffLine = false;//用户T掉了 恢复状态
        }else if(session == playerSession){
        	if(this.playerUser !=null){
        		RoomHandler.tokenRooms.remove(this.playerUser.getTokenid());
        	}
        	
        	playerUserOffLine = false;//用户T掉了 恢复状态
        }else{
        	log.debug("未知session处理");
        	return;
        }
        roomOnline--;
        playerSession = null;
        playerUser = null;
        this.playerReady = false;
        this.userFreeTime = System.currentTimeMillis();
        toDealer(new MessageDTO(ActionEnums.USER_LEAVE_ROOM.getType(), ""));// 中途异常退出模块

    }
    
    
    /**
     * 机器人叫点
     * @param isFirst
     * @throws InterruptedException 
     * @throws IOException 
     */
    public void robbotCallPoint(boolean isFirst) throws IOException, InterruptedException{
    	if(!hasRobot){//如果没有机器人
    		return;
    	}
        if (!isStart) {
            return;
        }
        Player player = game.getPlayer();
        Player dealer = game.getDealer();
        final CallPointDTO message = new CallPointDTO();
       // String data = (String) dto.getData();
       final WebSocketSession dealerSession = this.dealerSession;
            if (dealer.getNum() == 14) {// 设置最大上线
            	ThreadUtils.execute(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(3000);
							showdown(dealerSession , null);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				});
                
                return;
            }

            /*if (isFirst) {
                player.setDiceNum(3);
                player.setNum(5);
            } else {
                player.setDiceNum(dealer.getDiceNum());
                player.setNum(dealer.getNum() + 1);
            }

            player.setZai(dealer.isZai());
            player.setFei(dealer.isFei());

            message.setDiceNum(player.getDiceNum());
            message.setNum(player.getNum());
            message.setZai(player.isZai());
            message.setFei(player.isFei());*/
            
            if (isFirst) {
                player.setDiceNum(random.nextInt(5)+2);//2-6 不叫1
                player.setNum(RuleUtils.randomNum(3,4));
            } else {//用户已经叫过点了
           	 int diceTotal = RuleUtils.getDealerDice(dealer.getDiceArray(),  dealer.getDiceNum(), dealer.isZai());
           	int robotDiceTotal = RuleUtils.getDealerDice(player.getDiceArray(),  dealer.getDiceNum(), dealer.isZai());
           	 if((diceTotal < dealer.getNum() && dealer.getNum()>5) || dealer.getNum() > 8){//用戶瞎叫的且5個及以上，直接開
           		 Thread.sleep(RuleUtils.randomNum(1, 3)*1000);//
           		 showdown(this.playerSession, null); 
           		 return;
           	 }else if(this.round < 3 || diceTotal>=dealer.getNum() || robotDiceTotal>= dealer.getNum()){//如果用户真的有那么多,只能继续叫    ,或者机器人自己就有那么多只能继续叫       		 
           		 //计算机器人可以叫的点数
           		   //int[]  dealerDices = dealer.getDiceArray();
           		   int dealerDiceNum = dealer.getDiceNum();
           		   //int dealerNum = dealer.getNum();
           		   int randomAddNum = RuleUtils.randomNum(0,2);
           		   int mostDice = RuleUtils.getMostDice(dealer.getDiceArray(), dealer.isZai());
           		   
           		   if(dealerDiceNum == 6 || dealerDiceNum == 1){
   					   player.setNum(dealer.getNum() + 1);  
   					   player.setDiceNum(mostDice);// 
   				   }else{
   					   player.setNum(dealer.getNum() + randomAddNum);  
   					   player.setDiceNum(mostDice);// 
   					   if(randomAddNum == 0){
   						 player.setDiceNum(RuleUtils.randomNum(dealer.getDiceNum()+1, 6));// 
   						 //检查用户是不是已经有这么多了
   						//int newDiceTotal = RuleUtils.getDealerDice(dealer.getDiceArray(),  player.getDiceNum(), dealer.isZai());
   						/*if(newDiceTotal > player.getNum()){
   						  player.setNum(player.getNum()+1);
   						  player.setDiceNum(RuleUtils.randomNum(2, 5));
   						}*/
   						
   					   }else{
   						 player.setDiceNum(RuleUtils.randomNum(2, 5));// 
   						 	//检查用户是不是已经有这么多了
   						/*int newDiceTotal = RuleUtils.getDealerDice(dealer.getDiceArray(),  player.getDiceNum(), dealer.isZai());
    						if(newDiceTotal > player.getNum()){
    						  player.setNum(player.getNum()+1);
    						}*/
   					   }
   					  
   				   }
           		   
           		 int[]  playNewDices = RuleUtils.makeRobotDice(player.getNum(),player.getDiceNum(),dealer);
           		 player.setDiceArray(playNewDices);//更新最新的骰子
           		 
           		 
           		 
           		 
           	 }else{//3回合准备开了
           		 Thread.sleep(RuleUtils.randomNum(1, 3)*1000);//
           		 showdown(this.playerSession, null);
           		 return;
           	 }

            }

            player.setZai(dealer.isZai());
            player.setFei(dealer.isFei());
            message.setDiceNum(player.getDiceNum());
            message.setNum(player.getNum());
            message.setZai(player.isZai());
            message.setFei(player.isFei());


       	
            this.round ++ ;
            this.dealerRound = true;//切换到庄家回合(切换到 真人回合)
            
            ThreadUtils.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(3000);
						toDealer(new MessageDTO(ActionEnums.CALL_POINTS_READY.getType(), message));
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			});
            
            return;
        
    }
    
    //机器人准备
    public void robbotReady(String tokenId) {
        if (hasRobot) {
            try {
                Thread.sleep(1000);
                MessageDTO message = new MessageDTO();
                message.setTokenId(tokenId);
                message.setType(ActionEnums.USER_READY.getType());
                userReady(null, message);

            } catch (Exception e) {
                log.error("机器人准备失败", e);
            }
        }
    }
    
   private static Random random = new Random();  
   /**
    * 
    * @param isFirst
    * @param win  这句是否要赢？
 * @throws InterruptedException 
 * @throws IOException 
    */
   /* private void callHandler(boolean isFirst,boolean win) throws IOException, InterruptedException{
    	
    	 Player dealer = game.getDealer();
    	 Player player = game.getPlayer();
         if (isFirst) {
             player.setDiceNum(3);
             player.setNum(random.nextInt(5)+2);//2-6 不叫1
         } else {//用户已经叫过点了
        	 int diceTotal = RuleUtils.getDealerDice(dealer.getDiceArray(),  dealer.getDiceNum(), dealer.isZai());
        	 if((diceTotal < dealer.getNum() && dealer.getNum()>5) || dealer.getNum() > 8){//用戶瞎叫的且5個及以上，直接開
        		 showdown(this.dealerSession, null); 
        		 return;
        	 }else if(this.round < 2 || diceTotal>=dealer.getNum()){//如果用户真的有那么多,只能继续叫
                // player.setDiceNum(dealer.getDiceNum());
                 //player.setNum(dealer.getNum() + 1); 
        		 int[]  playNewDices = RuleUtils.makeRobotDice(player.getNum(),player.getDiceNum(),dealer);
        		 player.setDiceArray(playNewDices);//更新最新的骰子
        		 
        		 
        		 //计算机器人可以叫的点数
        		   int[]  dealerDices = dealer.getDiceArray();
        		   int dealerDiceNum = dealer.getDiceNum();
        		   int dealerNum = dealer.getNum();
        		   int randomAddNum = RuleUtils.randomNum(0,2);
        		   
        		   if(dealerDiceNum == 6 || dealerDiceNum == 1){
					   player.setNum(dealer.getNum() + 1);  
					   player.setDiceNum(RuleUtils.randomNum(2, 5));// 
				   }else{
					   player.setNum(dealer.getNum() + randomAddNum);  
					   if(randomAddNum == 0){//
						   player.setDiceNum(RuleUtils.randomNum(dealer.getDiceNum(), 6));//   
					   }else{
						   player.setDiceNum(RuleUtils.randomNum(2, 5));// 
					   }
					  
				   }
        		 
        		 
        		 
        		 
        	 }else{//3回合准备开了
        		 showdown(this.dealerSession, null);
        		 return;
        	 }

         }

         player.setZai(dealer.isZai());
         player.setFei(dealer.isFei());
         CallPointDTO message = new CallPointDTO();
         message.setDiceNum(player.getDiceNum());
         message.setNum(player.getNum());
         message.setZai(player.isZai());
         message.setFei(player.isFei());

         toDealer(new MessageDTO(ActionEnums.CALL_POINTS_READY.getType(), message));
    	
    	
    }*/
    
    public static void main(String[] args) {
    	for(int i=0;i<50;i++){
    		System.out.println(random.nextInt(5 - 2 + 1) + 2);
    	}
		
	}

    public UserInfos getPlayerUser() {
        return playerUser;
    }

    public void setPlayerUser(UserInfos playerUser) {
        this.playerUser = playerUser;
    }

    public UserInfos getDealerUser() {
        return dealerUser;
    }

    public void setDealerUser(UserInfos dealerUser) {
        this.dealerUser = dealerUser;
    }

    public String getGameNumber() {
        return gameNumber;
    }

    public RoomTypeEnum getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomTypeEnum roomType) {
        this.roomType = roomType;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    public boolean isUserCreate() {
        return isUserCreate;
    }

    public void setUserCreate(boolean isUserCreate) {
        this.isUserCreate = isUserCreate;
    }

    public WebSocketSession getPlayerSession() {
        return playerSession;
    }

    public void setPlayerSession(WebSocketSession playerSession) {
        this.playerSession = playerSession;
    }

    public WebSocketSession getDealerSession() {
        return dealerSession;
    }

    public void setDealerSession(WebSocketSession dealerSession) {
        this.dealerSession = dealerSession;
    }

    public boolean isHasRobot() {
        return hasRobot;
    }

    public void setHasRobot(boolean hasRobot) {
        this.hasRobot = hasRobot;
    }

	public Long getCallPointTimeOut() {
		return callPointTimeOut;
	}

	public void setCallPointTimeOut(Long callPointTimeOut) {
		this.callPointTimeOut = callPointTimeOut;
	}

	public boolean isDealerRound() {
		return dealerRound;
	}

	public void setDealerRound(boolean dealerRound) {
		this.dealerRound = dealerRound;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

    public Long getUserFreeTime() {
        return userFreeTime;
    }

    public void setUserFreeTime(long userFreeTime) {
        this.userFreeTime = userFreeTime;
    }

}
