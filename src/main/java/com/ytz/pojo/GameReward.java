package com.ytz.pojo;

import lombok.Data;

/**
 * 游戏用户击中Entity
 *
 * @author Bob
 */
@Data
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


}
