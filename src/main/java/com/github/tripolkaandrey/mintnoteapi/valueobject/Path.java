package com.github.tripolkaandrey.mintnoteapi.valueobject;

import com.google.common.base.Objects;

public final class Path {
    public static final String SEPARATOR = "/";

    private String parent;
    private String name;

    public Path(String parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public static Path parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }

        if (value.equals(SEPARATOR)) {
            return new Path("", "/");
        }

        int lastSeparator = value.lastIndexOf(SEPARATOR);

        if (lastSeparator < 0) {
            throw new IllegalArgumentException();
        }

        String name = value.substring(lastSeparator + 1);
        String parent = value.substring(0, lastSeparator);

        return new Path(parent, name);
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Objects.equal(parent, path.parent) && Objects.equal(name, path.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parent, name);
    }

    @Override
    public String toString() {
        return parent + SEPARATOR + name;
    }
}