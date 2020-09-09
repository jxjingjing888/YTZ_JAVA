package com.ytz.service;

import com.ytz.bean.UserInfo;

public interface UserInfoService {
    /**
     * 通过token获取用户信息
     * @param token
     * @return
     */
     UserInfo getUserByToken(String token);


}
