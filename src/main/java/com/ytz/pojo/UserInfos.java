package com.ytz.pojo;

import lombok.Data;

/**
 * 用户信息
 *
 * @author Bob
 */
@Data
public class UserInfos {
    private String realName;    //用户姓名
    private String img;  //用户图片
    private Integer userId;  //用户id
    private String tokenid;  //用户toeknid
    private String money;  //用户余额
    private String phone;


}
