package com.github.tripolkaandrey.mintnoteapi.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import java.util.List;

//Representation of embedded in document collection of directory entity
@Document(collectionName = "directories")
public final class Directories {
    @DocumentId
    private String id;
    private List<Directory> collection;

    public List<Directory> getCollection() {
        return collection;
    }

    public void setCollection(List<Directory> collection) {
        this.collection = collection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}