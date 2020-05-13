package com.huijianzhu.heqing.entity;

import java.util.Date;

public class HqPipeAccount {
    private Integer pipeAccountId;

    private String telecomBudgetAmount;

    private String electricityBudgetAmount;

    private String gasBudgetAmount;

    private String waterBudgetAmount;

    private String telecomAuditAmount;

    private String electricityAuditAmount;

    private String gasAuditAmount;

    private String waterAuditAmount;

    private Integer plotId;

    private String plotName;

    private Date updateTime;

    private String updateUserName;

    public Integer getPipeAccountId() {
        return pipeAccountId;
    }

    public void setPipeAccountId(Integer pipeAccountId) {
        this.pipeAccountId = pipeAccountId;
    }

    public String getTelecomBudgetAmount() {
        return telecomBudgetAmount;
    }

    public void setTelecomBudgetAmount(String telecomBudgetAmount) {
        this.telecomBudgetAmount = telecomBudgetAmount == null ? null : telecomBudgetAmount.trim();
    }

    public String getElectricityBudgetAmount() {
        return electricityBudgetAmount;
    }

    public void setElectricityBudgetAmount(String electricityBudgetAmount) {
        this.electricityBudgetAmount = electricityBudgetAmount == null ? null : electricityBudgetAmount.trim();
    }

    public String getGasBudgetAmount() {
        return gasBudgetAmount;
    }

    public void setGasBudgetAmount(String gasBudgetAmount) {
        this.gasBudgetAmount = gasBudgetAmount == null ? null : gasBudgetAmount.trim();
    }

    public String getWaterBudgetAmount() {
        return waterBudgetAmount;
    }

    public void setWaterBudgetAmount(String waterBudgetAmount) {
        this.waterBudgetAmount = waterBudgetAmount == null ? null : waterBudgetAmount.trim();
    }

    public String getTelecomAuditAmount() {
        return telecomAuditAmount;
    }

    public void setTelecomAuditAmount(String telecomAuditAmount) {
        this.telecomAuditAmount = telecomAuditAmount == null ? null : telecomAuditAmount.trim();
    }

    public String getElectricityAuditAmount() {
        return electricityAuditAmount;
    }

    public void setElectricityAuditAmount(String electricityAuditAmount) {
        this.electricityAuditAmount = electricityAuditAmount == null ? null : electricityAuditAmount.trim();
    }

    public String getGasAuditAmount() {
        return gasAuditAmount;
    }

    public void setGasAuditAmount(String gasAuditAmount) {
        this.gasAuditAmount = gasAuditAmount == null ? null : gasAuditAmount.trim();
    }

    public String getWaterAuditAmount() {
        return waterAuditAmount;
    }

    public void setWaterAuditAmount(String waterAuditAmount) {
        this.waterAuditAmount = waterAuditAmount == null ? null : waterAuditAmount.trim();
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