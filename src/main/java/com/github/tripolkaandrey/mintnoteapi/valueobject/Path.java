package com.github.tripolkaandrey.mintnoteapi.valueobject;

import com.github.tripolkaandrey.mintnoteapi.entity.Directories;
import com.github.tripolkaandrey.mintnoteapi.exception.InvalidPathException;
import com.google.common.base.Objects;

public final class Path {
    public static final String SEPARATOR = "/";

    private String parent;
    private String name;

    public Path(String parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public static Path parse(String value) throws InvalidPathException{
        if (value == null) {
            throw new InvalidPathException();
        }

        if (value.equals(SEPARATOR)) {
            return Directories.ROOT;
        }

        int lastSeparator = value.lastIndexOf(SEPARATOR);

        if (lastSeparator < 0 || lastSeparator == value.length() - 1) {
            throw new InvalidPathException();
        }

        String name = value.substring(lastSeparator + 1);
        String parent = value.substring(0, lastSeparator + 1);

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
        if (this == Directories.ROOT) {
            return SEPARATOR;
        } else {
            return parent + SEPARATOR + name;
        }
    }
}