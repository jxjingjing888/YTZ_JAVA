package com.ytz.pojo;

import java.io.Serializable;

public class MatchRoomDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String roomNum;
    private String roomTypeName;
    private String roomType;
    private int money;


    public MatchRoomDTO(String roomNum, String roomTypeName, int money) {
        super();
        this.roomNum = roomNum;
        this.roomTypeName = roomTypeName;
        this.money = money;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }


}
