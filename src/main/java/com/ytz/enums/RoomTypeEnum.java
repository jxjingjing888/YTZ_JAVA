package com.ytz.enums;

public enum RoomTypeEnum {

    PRIMARY("primary", 10, "1", 2, "低分"),
    /**
     *
     */
    HIGH("high", 50, "2", 10, "中分"),

    /**
     *
     */
    TOP("top", 100, "3", 20, "高分");

    // 普通方法  
    public static RoomTypeEnum getByType(String type) {
        for (RoomTypeEnum c : RoomTypeEnum.values()) {
            if (c.getType().equals(type)) {
                return c;
            }
        }
        return null;
    }


    private RoomTypeEnum(String type, Integer money, String val, int cost, String name) {
        this.type = type;
        this.money = money;
        this.val = val;
        this.cost = cost;
        this.name = name;
    }

    private String type;
    private Integer money;
    private String val;
    private Integer cost;
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


}
