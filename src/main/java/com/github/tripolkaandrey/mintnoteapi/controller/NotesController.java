package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.entity.Note;
import com.github.tripolkaandrey.mintnoteapi.entity.Tag;
import com.github.tripolkaandrey.mintnoteapi.exception.NoteNotFoundException;
import com.github.tripolkaandrey.mintnoteapi.repository.NoteRepository;
import com.github.tripolkaandrey.mintnoteapi.service.TranslationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@RestController
@RequestMapping("/notes/")
public final class NotesController {
    private final NoteRepository noteRepository;
    private final TranslationService translationService;

    public NotesController(NoteRepository noteRepository, TranslationService translationService) {
        this.noteRepository = noteRepository;
        this.translationService = translationService;
    }

    @GetMapping
    public Flux<Note> getNotes() {
        return noteRepository.findAll();
    }

    @GetMapping("{id}/")
    public Mono<ResponseEntity<Note>> getNote(@PathVariable String id) {
        return noteRepository.findById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(NoteNotFoundException::new));
    }

    @PutMapping("{id}/name/")
    public Mono<ResponseEntity<Note>> updateName(@PathVariable String id, @RequestBody String name) {
        return updateNote(id, note -> note.setName(name));
    }

    @PutMapping("{id}/tags/")
    public Mono<ResponseEntity<Note>> updateTags(@PathVariable String id, @RequestBody List<Tag> tags) {
        return updateNote(id, note -> note.setTags(tags));
    }

    @PutMapping("{id}/icon/")
    public Mono<ResponseEntity<Note>> updateIcon(@PathVariable String id, @RequestBody String icon) {
        return updateNote(id, note -> note.setIcon(icon));
    }

    @PutMapping("{id}/parent/")
    public Mono<ResponseEntity<Note>> updateParent(@PathVariable String id, @RequestBody String parent) {
        return updateNote(id, note -> note.setParent(parent));
    }

    @PutMapping("{id}/content/")
    public Mono<ResponseEntity<Note>> updateContent(@PathVariable String id, @RequestBody String content) {
        return updateNote(id, note -> note.setContent(content));
    }

    private Mono<ResponseEntity<Note>> updateNote(String id, Consumer<Note> updater) {
        return noteRepository.findById(id)
                .flatMap(
                        n -> {
                            updater.accept(n);
                            n.setLastModifiedDate(new Date());
                            return noteRepository.save(n);
                        }
                ).map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(NoteNotFoundException::new));
    }

    @PostMapping
    public Mono<ResponseEntity<Note>> createNote(@RequestBody Note note) {
        return noteRepository.save(note)
                .map(body -> ResponseEntity.created(URI.create("/notes/" + body.getId())).body(body))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("{id}/")
    public Mono<ResponseEntity<Void>> deleteNote(@PathVariable String id) {
        return noteRepository.findById(id)
                .flatMap(n -> noteRepository.deleteById(id).then(Mono.just(ResponseEntity.ok().<Void>build())))
                .switchIfEmpty(Mono.error(NoteNotFoundException::new));

    }

    @GetMapping("{id}/translation/{targetLanguage}/")
    public Mono<ResponseEntity<String>> translate(@PathVariable String id, @PathVariable String targetLanguage) {
        return noteRepository.findById(id)
                .flatMap(note -> translationService.translate(note.getContent(), targetLanguage))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(NoteNotFoundException::new));
    }
}