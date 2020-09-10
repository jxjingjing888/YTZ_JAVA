package com.ytz.service;

import com.ytz.bean.UserInfo;
import com.ytz.pojo.GameReward;

public interface UserInfoService {
    /**
     * 通过token获取用户信息
     *
     * @param token
     * @return
     */
    UserInfo getUserByToken(String token);


    /**
     * 游戏结算中心
     *
     * @param token
     * @return
     */
    UserInfo gameSettlement(GameReward token);


}
