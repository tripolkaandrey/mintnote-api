package com.github.tripolkaandrey.mintnoteapi.repository;

import com.github.tripolkaandrey.mintnoteapi.entity.Tag;
import com.github.tripolkaandrey.mintnoteapi.entity.Tags;
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import reactor.core.publisher.Flux;

public interface TagsRepository extends FirestoreReactiveRepository<Tags> {
    default Flux<Tag> findAllByUserId(String userId) {
        return this.findById(userId).flatMapIterable(Tags::getCollection);
    }

}