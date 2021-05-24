package com.github.tripolkaandrey.mintnoteapi.entity;

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

        return name.equals(that.name) &&
                icon.equals(that.icon) &&
                tags.equals(that.tags) &&
                creationDate.equals(that.creationDate) &&
                lastModifiedDate.equals(that.lastModifiedDate) &&
                parent.equals(that.parent);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + icon.hashCode();
        result = 31 * result + tags.hashCode();
        result = 31 * result + creationDate.hashCode();
        result = 31 * result + lastModifiedDate.hashCode();
        result = 31 * result + parent.hashCode();
        return result;
    }
}