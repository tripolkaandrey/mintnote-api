package com.github.tripolkaandrey.mintnoteapi.repository;

import com.github.tripolkaandrey.mintnoteapi.entity.Directories;
import com.github.tripolkaandrey.mintnoteapi.entity.Directory;
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface DirectoriesRepository extends FirestoreReactiveRepository<Directories> {
    default Flux<Directory> findAllByUserId(String userId) {
        return this.findById(userId).flatMapIterable(Directories::getCollection);
    }
}