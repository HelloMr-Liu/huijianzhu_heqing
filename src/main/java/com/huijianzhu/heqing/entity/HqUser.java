package com.huijianzhu.heqing.entity;

import java.util.Date;

public class HqUser {
    private Integer userId;

    private String userName;

    private String userAccount;

    private String passWord;

    private String phoneNumber;

    private String email;

    private Integer userType;

    private String permissionsId;

    private Date updateTime;

    private String updateUserName;

    private Date createTime;

    private String delFlag;

    private String e1;

    private String e2;

    private String e3;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount == null ? null : userAccount.trim();
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord == null ? null : passWord.trim();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getPermissionsId() {
        return permissionsId;
    }

    public void setPermissionsId(String permissionsId) {
        this.permissionsId = permissionsId == null ? null : permissionsId.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName == null ? null : updateUserName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag == null ? null : delFlag.trim();
    }

    public String getE1() {
        return e1;
    }

    public void setE1(String e1) {
        this.e1 = e1 == null ? null : e1.trim();
    }

    public String getE2() {
        return e2;
    }

    public void setE2(String e2) {
        this.e2 = e2 == null ? null : e2.trim();
    }

    public String getE3() {
        return e3;
    }

    public void setE3(String e3) {
        this.e3 = e3 == null ? null : e3.trim();
    }
}