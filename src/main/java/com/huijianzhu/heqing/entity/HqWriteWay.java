package com.huijianzhu.heqing.entity;

public class HqWriteWay {
    private String writeId;

    private String writeName;

    private String isUnit;

    public String getWriteId() {
        return writeId;
    }

    public void setWriteId(String writeId) {
        this.writeId = writeId == null ? null : writeId.trim();
    }

    public String getWriteName() {
        return writeName;
    }

    public void setWriteName(String writeName) {
        this.writeName = writeName == null ? null : writeName.trim();
    }

    public String getIsUnit() {
        return isUnit;
    }

    public void setIsUnit(String isUnit) {
        this.isUnit = isUnit == null ? null : isUnit.trim();
    }
}