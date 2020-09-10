package com.ytz.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * sys_user
 * @author 
 */
@Data
public class SysUser implements Serializable {
    private Integer id;

    /**
     * token
     */
    private String token;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 登录名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String mobilePhone;

    /**
     * 性别 0：女，1：男
     */
    private Integer sex;

    /**
     * 地址
     */
    private String address;

    /**
     * 邮编
     */
    private Integer postno;

    /**
     * qq
     */
    private String qq;

    /**
     * 微信号
     */
    private String wechat;

    /**
     * 微博
     */
    private String weibo;

    /**
     * 头像
     */
    private String faceImg;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 登录ip
     */
    private String ipAddress;

    /**
     * 用户状态0：正常，1：冻结
     */
    private Long status;

    /**
     * 邀请id
     */
    private Integer inviteId;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 最后一次登录时间
     */
    private Date loginDate;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}