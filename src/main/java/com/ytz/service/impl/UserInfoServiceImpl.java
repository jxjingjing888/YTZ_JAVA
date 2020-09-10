package com.ytz.service.impl;

import com.ytz.bean.SysUser;
import com.ytz.dao.SysUserDao;
import com.ytz.pojo.GameReward;
import com.ytz.service.UserInfoService;
import org.springframework.stereotype.Service;

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

    public static UserInfoServiceImpl instant;

    public UserInfoServiceImpl() {
        instant = this;
    }

    SysUserDao dao;

    /**
     * 通过token获取用户信息
     *
     * @param token
     * @return
     */
    @Override
    public SysUser getUserByToken(String token) {
        return dao.getUserByToken(token);
    }


    /**
     * 游戏结算中心
     *
     * @param token
     * @return
     */
    @Override
    public SysUser gameSettlement(GameReward token) {
        return null;
    }


}
