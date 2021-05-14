package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.entity.Note;
import com.github.tripolkaandrey.mintnoteapi.entity.Tag;
import com.github.tripolkaandrey.mintnoteapi.repository.NoteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/notes/")
public final class NotesController {
    private final NoteRepository noteRepository;

    public NotesController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @GetMapping
    public Flux<Note> getNotes() {
        return noteRepository.findAll();
    }

    @GetMapping("{id}/")
    public Mono<ResponseEntity<Note>> getNote(@PathVariable String id) {
        return noteRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("{id}/name/")
    public Mono<ResponseEntity<Note>> updateName(@PathVariable String id, @RequestBody String name) {
        return noteRepository.findById(id)
                .flatMap(
                        n -> {
                            n.setName(name);
                            n.setLastModifiedDate(new Date());
                            return noteRepository.save(n);
                        }
                ).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("{id}/tags/")
    public Mono<ResponseEntity<Note>> updateTags(@PathVariable String id, @RequestBody List<Tag> tags) {
        return noteRepository.findById(id)
                .flatMap(
                        n -> {
                            n.setTags(tags);
                            n.setLastModifiedDate(new Date());
                            return noteRepository.save(n);
                        }
                ).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("{id}/icon/")
    public Mono<ResponseEntity<Note>> updateIcon(@PathVariable String id, @RequestBody String icon) {
        return noteRepository.findById(id)
                .flatMap(
                        n -> {
                            n.setIcon(icon);
                            n.setLastModifiedDate(new Date());
                            return noteRepository.save(n);
                        }
                ).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("{id}/parent/")
    public Mono<ResponseEntity<Note>> updateParent(@PathVariable String id, @RequestBody String parent) {
        return noteRepository.findById(id)
                .flatMap(
                        n -> {
                            n.setParent(parent);
                            n.setLastModifiedDate(new Date());
                            return noteRepository.save(n);
                        }
                ).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("{id}/content/")
    public Mono<ResponseEntity<Note>> updateContent(@PathVariable String id, @RequestBody String content) {
        return noteRepository.findById(id)
                .flatMap(
                        n -> {
                            n.setContent(content);
                            n.setLastModifiedDate(new Date());
                            return noteRepository.save(n);
                        }
                ).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Note>> createNote(@RequestBody Note note) {
        return noteRepository.save(note)
                .map(body -> ResponseEntity.created(URI.create("/notes/" + body.getId())).body(body))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}