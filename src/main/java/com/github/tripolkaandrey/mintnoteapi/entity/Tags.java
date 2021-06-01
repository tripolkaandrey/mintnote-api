package com.github.tripolkaandrey.mintnoteapi.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import java.util.List;

//Representation of embedded in document collection of tag entity
@Document(collectionName = "tags")
public final class Tags {
    @DocumentId
    private String id;
    private List<Tag> collection;

    public Tags() {
        //Required by FirestoreReactiveRepository for deserialization
    }

    public Tags(String id, List<Tag> collection) {
        this.id = id;
        this.collection = collection;
    }

    public List<Tag> getCollection() {
        return collection;
    }

    public void setCollection(List<Tag> collection) {
        this.collection = collection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void add(Tag tag) {
        this.collection.add(tag);
    }
}