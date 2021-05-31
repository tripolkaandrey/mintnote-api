package com.github.tripolkaandrey.mintnoteapi.repository;

import com.github.tripolkaandrey.mintnoteapi.entity.Directories;
import com.github.tripolkaandrey.mintnoteapi.entity.Directory;
import com.github.tripolkaandrey.mintnoteapi.valueobject.Path;
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DirectoriesRepository extends FirestoreReactiveRepository<Directories> {
    default Flux<Directory> findAllByUserId(String userId) {
        return this.findById(userId).flatMapIterable(Directories::getCollection);
    }

    default Flux<Directory> findAllByUserIdAndParent(String userId, String parent) {
        return this.findAllByUserId(userId).filter(x -> x.getParent().equals(parent));
    }

    default Mono<Boolean> existsByUserIdAndPath(String userId, Path path) {
        if (path.equals(Directories.ROOT)) {
            return Mono.just(true);
        }

        return this.findAllByUserId(userId)
                .any(directory ->
                        directory.getName().equals(path.getName()) &&
                        directory.getParent().equals(path.getParent())
                );
    }

    default Mono<Path> add(String userId, Directory directory) {
        return this.findById(userId)
                .flatMap(directories -> {
                    directories.getCollection().add(directory);
                    return this.save(directories);
                })
                .then(Mono.just(new Path(directory.getParent(), directory.getName())));
    }
}