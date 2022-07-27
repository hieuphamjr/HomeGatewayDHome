package main.java.HomeGateway.DHomeDevice.DHomeObject;

import java.util.HashMap;
import java.util.List;

public class Group {
    private Integer _id;
    private Integer id;
    private String name;
    private Integer icon;
    private HashMap createdAt;
    private HashMap updatedAt;
    private Integer hierarchyLevel;
    private List<?> children;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
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

    public Integer getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(Integer hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public List<?> getChildren() {
        return children;
    }

    public void setChildren(List<?> children) {
        this.children = children;
    }
}
