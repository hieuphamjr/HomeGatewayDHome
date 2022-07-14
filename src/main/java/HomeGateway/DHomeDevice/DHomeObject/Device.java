package main.java.HomeGateway.DHomeDevice.DHomeObject;

import java.util.Date;
import java.util.HashMap;

public class Device {
    private Integer _id;
    private Integer id;
    private Integer devIdx;
    private String name;
    private String descrip;
    private Integer status;
    private Boolean available;
    private Integer type;
    private Integer idx;
    private Integer idx1;
    private Integer netAdd;
    private Integer endpoint;
    private Integer sceneId;
    private Integer icon;
    private Integer irModelId;
    private Integer irHubId;
    private Integer rank;
    private Integer groupId;
    private HashMap createdAt;
    private HashMap updatedAt;

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDevIdx() {
        return devIdx;
    }

    public void setDevIdx(Integer devIdx) {
        this.devIdx = devIdx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public Integer getIdx1() {
        return idx1;
    }

    public void setIdx1(Integer idx1) {
        this.idx1 = idx1;
    }

    public Integer getNetAdd() {
        return netAdd;
    }

    public void setNetAdd(Integer netAdd) {
        this.netAdd = netAdd;
    }

    public Integer getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Integer endpoint) {
        this.endpoint = endpoint;
    }

    public Integer getSceneId() {
        return sceneId;
    }

    public void setSceneId(Integer sceneId) {
        this.sceneId = sceneId;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public Integer getIrModelId() {
        return irModelId;
    }

    public void setIrModelId(Integer irModelId) {
        this.irModelId = irModelId;
    }

    public Integer getIrHubId() {
        return irHubId;
    }

    public void setIrHubId(Integer irHubId) {
        this.irHubId = irHubId;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public HashMap getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(HashMap createdAt) {
        this.createdAt = createdAt;
    }

    public HashMap getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(HashMap updatedAt) {
        this.updatedAt = updatedAt;
    }
}
