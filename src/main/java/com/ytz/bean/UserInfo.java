/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.ytz.bean;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * 用户信息Entity
 *
 * @author zsl
 * @version 2017-09-08
 */
@Setter
@Getter
public class UserInfo {

    private static final long serialVersionUID = 1L;
    private String id;
    private String realName;        // 真实姓名
    private String cardNo;        // 身份证号
    private String userName;        // 登录名
    private String password;        // 密码
    private String mobilePhone;        // 手机号码
    private String sex;        // 性别
    private String address;        // 地址
    private String postNo;        // 邮编
    private String qq;        // qq
    private String qqCheck;        // qq_check
    private String webchat;        // 微信
    private String weibo;        // 微博
    private String faceImg;        // 头像
    private String userType;        // 用户类型
    private String port;        // 端口
    private String ipAddress;        // ip地址
    private String status;        // 状态
    private String remark;        // 备注
    private UserInfo invite;        // 邀请人
    private Double balance;        // 余额/充值金额
    private Double commissionCount;        // 佣金/赠送金额
    private Double commissionBalance;        // 佣金余额 /奖励佣金
    private Double commissionMention;        // 佣金提现 /兑换奖金
    private Double prizeCount;        // 奖励金额 /中奖金额
    private Double points;        // 积分
    private String tokenid;     //系统流转tokenid
    private Integer bullNonum;   //套牛连续不中次数
    private Integer fishNonum;   //捕鱼连续不中次数
    private Date registDate;   //注册时间
    private Date loginDate;   //最后登录时间
    private String chessPwd;   //棋牌验证码
    private String gameId;    //棋牌ID
    private Double diamondBalance; //钻石余额


    public UserInfo() {
        super();
    }


    @Length(min = 0, max = 200, message = "真实姓名长度必须介于 0 和 200 之间")
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Length(min = 0, max = 20, message = "身份证号长度必须介于 0 和20 之间")
    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    @Length(min = 0, max = 30, message = "登录名长度必须介于 0 和30 之间")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    //@Length(min=0, max=50, message="密码长度必须介于 0 和 50 之间")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Length(min = 0, max = 20, message = "手机号码长度必须介于 0 和 20 之间")
    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @Length(min = 0, max = 1, message = "性别长度必须介于 0 和 1 之间")
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Length(min = 0, max = 200, message = "地址长度必须介于 0 和 200 之间")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Length(min = 0, max = 10, message = "邮编长度必须介于 0 和 10 之间")
    public String getPostNo() {
        return postNo;
    }

    public void setPostNo(String postNo) {
        this.postNo = postNo;
    }

    @Length(min = 0, max = 30, message = "qq长度必须介于 0 和 30 之间")
    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    @Length(min = 0, max = 1, message = "qq_check长度必须介于 0 和 1 之间")
    public String getQqCheck() {
        return qqCheck;
    }

    public void setQqCheck(String qqCheck) {
        this.qqCheck = qqCheck;
    }

    @Length(min = 0, max = 30, message = "微信长度必须介于 0 和 30 之间")
    public String getWebchat() {
        return webchat;
    }

    public void setWebchat(String webchat) {
        this.webchat = webchat;
    }

    @Length(min = 0, max = 30, message = "微博长度必须介于 0 和 30 之间")
    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    @Length(min = 0, max = 200, message = "头像长度必须介于 0 和 200 之间")
    public String getFaceImg() {
        return faceImg;
    }

    public void setFaceImg(String faceImg) {
        this.faceImg = faceImg;
    }

    @Length(min = 0, max = 20, message = "用户类型长度必须介于 0 和 20 之间")
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Length(min = 0, max = 32, message = "端口长度必须介于 0 和 32 之间")
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Length(min = 0, max = 32, message = "ip地址长度必须介于 0 和 32 之间")
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Length(min = 0, max = 32, message = "状态长度必须介于 0 和 32 之间")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Length(min = 0, max = 2000, message = "备注长度必须介于 0 和 2000 之间")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @JsonBackReference
    public UserInfo getInvite() {
        return invite;
    }

    public void setInvite(UserInfo invite) {
        this.invite = invite;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getCommissionCount() {
        return commissionCount;
    }

    public void setCommissionCount(Double commissionCount) {
        this.commissionCount = commissionCount;
    }

    public Double getCommissionBalance() {
        return commissionBalance;
    }

    public void setCommissionBalance(Double commissionBalance) {
        this.commissionBalance = commissionBalance;
    }

    public Double getCommissionMention() {
        return commissionMention;
    }

    public void setCommissionMention(Double commissionMention) {
        this.commissionMention = commissionMention;
    }

    public Double getPrizeCount() {
        return prizeCount;
    }

    public void setPrizeCount(Double prizeCount) {
        this.prizeCount = prizeCount;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public String getTokenid() {
        return tokenid;
    }

    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }

    public Integer getBullNonum() {
        return bullNonum;
    }

    public void setBullNonum(Integer bullNonum) {
        this.bullNonum = bullNonum;
    }

    public Integer getFishNonum() {
        return fishNonum;
    }

    public void setFishNonum(Integer fishNonum) {
        this.fishNonum = fishNonum;
    }

    public Date getRegistDate() {
        return registDate;
    }

    public void setRegistDate(Date registDate) {
        this.registDate = registDate;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }


    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getChessPwd() {
        return chessPwd;
    }

    public void setChessPwd(String chessPwd) {
        this.chessPwd = chessPwd;
    }

    public Double getDiamondBalance() {
        return diamondBalance;
    }

    public void setDiamondBalance(Double diamondBalance) {
        this.diamondBalance = diamondBalance;
    }


}