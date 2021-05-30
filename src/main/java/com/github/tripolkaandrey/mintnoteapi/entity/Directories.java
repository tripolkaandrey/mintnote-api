package com.github.tripolkaandrey.mintnoteapi.entity;

import com.github.tripolkaandrey.mintnoteapi.valueobject.Path;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import java.util.List;

//Representation of embedded in document collection of directory entity
@Document(collectionName = "directories")
public final class Directories {
    public static final Path ROOT = new Path("", Path.SEPARATOR);

    @DocumentId
    private String id;
    private List<Directory> collection;

    public Directories(String id, List<Directory> collection) {
        this.id = id;
        this.collection = collection;
    }

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