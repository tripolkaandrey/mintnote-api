package com.github.tripolkaandrey.mintnoteapi.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import com.google.common.base.Objects;

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
}