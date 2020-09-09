package com.ytz.enums;

public enum ActionEnums {
    
    HEART_BEAT("heartbeat"),
	
	MESSAGE("message"),
	/**
	 * 房间匹配 
	 */
	MATCH_ROOM("matchRoom"),
	/**
	 * 创建房间
	 */
	CREATE_NEW_ROOM("createNewRoom"),
	/**
	 * 加入房间
	 */
	JOIN_ROOM("joinRoom"),
	/**
	 * 有个用户加入房间
	 */
	USER_JOIN_ROOM("userJoinRoom"),
	
	/**
     * 用户离开房间
     */
    USER_LEAVE_ROOM("userLeaveRoom"),
    
    
    /**
     * 用户叫点数
     */
    CALL_POINTS("callPoints"),
    
    /**
     * 可以叫点数了
     */
    CALL_POINTS_READY("callPointsReady"),
    
    
    /**
     * 用户准备
     */
    USER_READY("userReady"),
	/**
	 * 聊天
	 */
	TALK("talk"),
	/**
	 * 在线人数
	 */
	ONLINE_NUM("onlineNum"),
	
	/**
	 * 用戶信息
	 */
	GET_USER_INFO("getUserInfo"),
	
	   /**
     * 用戶信息
     */
	GET_OHTER_PLAYER_INFO("getOhterPlayerInfo"),
	
	/**
     * 游戏开始
     */
    GAME_START("gameStart"),
    
    /**
     * 开
     */
    SHOW_DOWN("showDown"),
    
    /**
     * 强开，强制退出造成的
     */
    FORCE_SHOW_DOWN("forceShowDown"),
    
    /**
     * 用户信息变更(主要是扣钱，赢钱了)
     */
    USER_INFO_CHANGE("userInfoChange"),
    
    //用户超时
    USER_TIME_OUT("userTimeOut"),
	
	
	LOGOUT("logout"),
	
	/**
	 * 余额不足
	 */
	NOT_ENOUGH_BALANCE("notEnoughBalance"),
	/**
	 * 用户已经不在房间内
	 */
	USER_NOT_IN_ROOM("userNotInRoom"),
	;
    
    
	
	
	
	private ActionEnums(String type) {
		this.type = type;

	}
	
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
	

}
