package com.huijianzhu.heqing.entity;

import java.util.Date;

public class HqNoLiveAccount {
    private Integer noLiveId;

    private Integer plotId;

    private String plotName;

    private String totalDealMoney;

    private String okMoney;

    private String payScale;

    private Date updateTime;

    private String updateUserName;

    public Integer getNoLiveId() {
        return noLiveId;
    }

    public void setNoLiveId(Integer noLiveId) {
        this.noLiveId = noLiveId;
    }

    public Integer getPlotId() {
        return plotId;
    }

    public void setPlotId(Integer plotId) {
        this.plotId = plotId;
    }

    public String getPlotName() {
        return plotName;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName == null ? null : plotName.trim();
    }

    public String getTotalDealMoney() {
        return totalDealMoney;
    }

    public void setTotalDealMoney(String totalDealMoney) {
        this.totalDealMoney = totalDealMoney == null ? null : totalDealMoney.trim();
    }

    public String getOkMoney() {
        return okMoney;
    }

    public void setOkMoney(String okMoney) {
        this.okMoney = okMoney == null ? null : okMoney.trim();
    }

    public String getPayScale() {
        return payScale;
    }

    public void setPayScale(String payScale) {
        this.payScale = payScale == null ? null : payScale.trim();
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
}