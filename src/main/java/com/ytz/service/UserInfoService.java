package com.ytz.service;

import com.ytz.bean.SysUser;
import com.ytz.pojo.GameReward;

public interface UserInfoService {
    /**
     * 通过token获取用户信息
     *
     * @param token
     * @return
     */
    SysUser getUserByToken(String token);


    /**
     * 游戏结算中心
     *
     * @param token
     * @return
     */
    SysUser gameSettlement(GameReward token);


}
