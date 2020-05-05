package com.huijianzhu.heqing.entity;

public class HqPermissions {
    private Integer permissionsId;

    private String modelId;

    private String modelName;

    private String modelPath;

    private String isParent;

    private String parentId;

    private String icoClass;

    private String e1;

    private String e2;

    private String e3;

    public Integer getPermissionsId() {
        return permissionsId;
    }

    public void setPermissionsId(Integer permissionsId) {
        this.permissionsId = permissionsId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId == null ? null : modelId.trim();
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName == null ? null : modelName.trim();
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath == null ? null : modelPath.trim();
    }

    public String getIsParent() {
        return isParent;
    }

    public void setIsParent(String isParent) {
        this.isParent = isParent == null ? null : isParent.trim();
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
    }

    public String getIcoClass() {
        return icoClass;
    }

    public void setIcoClass(String icoClass) {
        this.icoClass = icoClass == null ? null : icoClass.trim();
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