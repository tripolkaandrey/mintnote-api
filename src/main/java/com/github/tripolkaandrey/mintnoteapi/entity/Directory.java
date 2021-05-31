package com.github.tripolkaandrey.mintnoteapi.entity;

import java.util.List;

public final class Directory extends FileSystemUnit {
    public static class Builder {
        private Directory directory;

        public Builder() {
            directory = new Directory();
        }

        public Builder reset() {
            directory = new Directory();
            return this;
        }

        public Builder withName(String name) {
            directory.setName(name);
            return this;
        }

        public Builder withParent(String parent) {
            directory.setParent(parent);
            return this;
        }

        public Builder withIcon(String icon) {
            directory.setIcon(icon);
            return this;
        }

        public Builder withTags(List<Tag> tags) {
            directory.setTags(tags);
            return this;
        }

        public Directory build() {
            return directory;
        }
    }
}