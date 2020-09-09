package com.ytz.pojo;

import java.io.Serializable;

public class MessageDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String type;
	private String identifier;
	private Object data;
	private Integer roomId;
	private String userId;
	private String tokenId;
	/**
	 * true 成功 false：失败
	 */
	private boolean status = true;
	
	
	public MessageDTO() {
	
	}
	
	public MessageDTO(String type, Object data) {
		super();
		this.type = type;
		this.data = data;
	}
	
	public MessageDTO(String type, Object data ,boolean status) {
		super();
		this.type = type;
		this.data = data;
		this.status = status;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}



	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
	
	

}
