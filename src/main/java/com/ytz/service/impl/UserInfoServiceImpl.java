package com.ytz.service.impl;

import com.ytz.bean.UserInfo;
import com.ytz.dao.UserMapper;
import com.ytz.pojo.GameReward;
import com.ytz.service.UserInfoService;
import org.springframework.stereotype.Service;

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

    public static UserInfoServiceImpl instant;

    public UserInfoServiceImpl() {
        instant = this;
    }

    UserMapper dao;

    /**
     * 通过token获取用户信息
     *
     * @param token
     * @return
     */
    @Override
    public UserInfo getUserByToken(String token) {
        return dao.getUserByToken(token);
    }


    /**
     * 游戏结算中心
     *
     * @param token
     * @return
     */
    @Override
    public UserInfo gameSettlement(GameReward token) {
        return null;
    }


}
