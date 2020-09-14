package com.ytz.pojo;

import lombok.Data;

import java.io.Serializable;


@Data
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




}
