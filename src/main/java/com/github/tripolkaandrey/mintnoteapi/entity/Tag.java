package com.github.tripolkaandrey.mintnoteapi.entity;

import com.google.common.base.Objects;

public final class Tag {
    private String name;
    private String color;

    public Tag() {
        //Required by FirestoreReactiveRepository for deserialization
    }

    public Tag(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equal(name, tag.name) &&
                Objects.equal(color, tag.color);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, color);
    }

    public static class Builder {
        private Tag tag;

        public Builder() {
            tag = new Tag();
        }

        public Builder reset() {
            tag = new Tag();
            return this;
        }

        public Builder withName(String name) {
            tag.setName(name);
            return this;
        }

        public Builder withColor(String color) {
            tag.setColor(color);
            return this;
        }

        public Tag build() {
            return tag;
        }
    }
}
