package com.ytz.dao;

import com.ytz.bean.UserInfo;

public interface UserMapper {

    /**
     * 通过token获取用户信息
     *
     * @param token
     * @return
     */
    UserInfo getUserByToken(String token);

}
