package com.github.tripolkaandrey.mintnoteapi.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import com.google.common.base.Objects;

import java.util.List;

@Document(collectionName = "notes")
public final class Note extends FileSystemUnit {
    @DocumentId
    private String id;
    private String content;
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Note note = (Note) o;
        return Objects.equal(id, note.id) &&
                Objects.equal(content, note.content) &&
                Objects.equal(userId, note.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), id, content, userId);
    }

    public static class Builder {
        private Note note;

        public Builder() {
            note = new Note();
        }

        public Builder reset() {
            note = new Note();
            return this;
        }

        public Builder withName(String name) {
            note.setName(name);
            return this;
        }

        public Builder withParent(String parent) {
            note.setParent(parent);
            return this;
        }

        public Builder withIcon(String icon) {
            note.setIcon(icon);
            return this;
        }

        public Builder withTags(List<Tag> tags) {
            note.setTags(tags);
            return this;
        }

        public Builder withContent(String content) {
            note.setContent(content);
            return this;
        }

        public Builder withUserId(String userId) {
            note.setUserId(userId);
            return this;
        }

        public Note build() {
            return note;
        }
    }
}