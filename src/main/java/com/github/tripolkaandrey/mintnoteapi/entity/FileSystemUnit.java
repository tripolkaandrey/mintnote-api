package com.github.tripolkaandrey.mintnoteapi.entity;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class FileSystemUnit {
    private String name;
    private String icon;
    private List<Tag> tags;
    private Date creationDate;
    private Date lastModifiedDate;
    private String parent;

    protected FileSystemUnit() {
        tags = new ArrayList<>();
        creationDate = new Date();
        lastModifiedDate = new Date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileSystemUnit that = (FileSystemUnit) o;
        return Objects.equal(name, that.name) &&
                Objects.equal(icon, that.icon) &&
                Objects.equal(tags, that.tags) &&
                Objects.equal(creationDate, that.creationDate) &&
                Objects.equal(lastModifiedDate, that.lastModifiedDate) &&
                Objects.equal(parent, that.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, icon, tags, creationDate, lastModifiedDate, parent);
    }
}