package com.ytz.dao;

import com.ytz.bean.UserInfo;

import java.util.List;

public interface UserMapper  {

    /**
     * 通过token获取用户信息
     * @param token
     * @return
     */
     UserInfo getUserByToken(String token);

}
