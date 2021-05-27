package com.github.tripolkaandrey.mintnoteapi.repository;

import com.github.tripolkaandrey.mintnoteapi.entity.Note;
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface NoteRepository extends FirestoreReactiveRepository<Note> {
    Flux<Note> findAllByUserId(String userId);
}