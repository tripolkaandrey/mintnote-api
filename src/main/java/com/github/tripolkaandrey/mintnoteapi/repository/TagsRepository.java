package com.github.tripolkaandrey.mintnoteapi.repository;

import com.github.tripolkaandrey.mintnoteapi.entity.Tags;
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

public interface TagsRepository extends FirestoreReactiveRepository<Tags> {

}