package com.huijianzhu.heqing.entity;

public class HqPropertyValueWithBLOBs extends HqPropertyValue {
    private String propertyValue;

    private String propertyValueDesc;

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue == null ? null : propertyValue.trim();
    }

    public String getPropertyValueDesc() {
        return propertyValueDesc;
    }

    public void setPropertyValueDesc(String propertyValueDesc) {
        this.propertyValueDesc = propertyValueDesc == null ? null : propertyValueDesc.trim();
    }
}