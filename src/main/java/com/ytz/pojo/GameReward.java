package com.ytz.pojo;

/**
 * 游戏用户击中Entity
 *
 * @author mzh14
 */
public class GameReward {
    private String name;        // 游戏名称
    private String typeName;        // 种类名(比如小黄鱼、章鱼、级别1
    private String costRate;        // 炮弹金币消耗
    private String percentage;        // 发射倍率
    private String tokenid;     //系统流转tokenid
    private String mobilePhone;     //手机号码
    private String userId;     //登陆用户
    private String levelRate;        // 游戏种类级别兑换比
    private String isSuccess;        // 是否成功  0失败，1成功
    private String isDeduct;        //是否再次扣除
    private String isCumulative;        //是否累加

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCostRate() {
        return costRate;
    }

    public void setCostRate(String costRate) {
        this.costRate = costRate;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getTokenid() {
        return tokenid;
    }

    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getLevelRate() {
        return levelRate;
    }

    public void setLevelRate(String levelRate) {
        this.levelRate = levelRate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getIsDeduct() {
        return isDeduct;
    }

    public void setIsDeduct(String isDeduct) {
        this.isDeduct = isDeduct;
    }

    public String getIsCumulative() {
        return isCumulative;
    }

    public void setIsCumulative(String isCumulative) {
        this.isCumulative = isCumulative;
    }
}
