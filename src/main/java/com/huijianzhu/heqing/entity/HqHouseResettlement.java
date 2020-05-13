package com.huijianzhu.heqing.entity;

import java.util.Date;

public class HqHouseResettlement {
    private Integer resettlementId;

    private String noLiveNumber;

    private String liveNumber;

    private String surplusNumber;

    private Integer plotId;

    private String plotName;

    private Date updateTime;

    private String updateUserName;

    public Integer getResettlementId() {
        return resettlementId;
    }

    public void setResettlementId(Integer resettlementId) {
        this.resettlementId = resettlementId;
    }

    public String getNoLiveNumber() {
        return noLiveNumber;
    }

    public void setNoLiveNumber(String noLiveNumber) {
        this.noLiveNumber = noLiveNumber == null ? null : noLiveNumber.trim();
    }

    public String getLiveNumber() {
        return liveNumber;
    }

    public void setLiveNumber(String liveNumber) {
        this.liveNumber = liveNumber == null ? null : liveNumber.trim();
    }

    public String getSurplusNumber() {
        return surplusNumber;
    }

    public void setSurplusNumber(String surplusNumber) {
        this.surplusNumber = surplusNumber == null ? null : surplusNumber.trim();
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