package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.entity.Note;
import com.github.tripolkaandrey.mintnoteapi.entity.Tag;
import com.github.tripolkaandrey.mintnoteapi.exception.AccessDeniedException;
import com.github.tripolkaandrey.mintnoteapi.exception.NoteNotFoundException;
import com.github.tripolkaandrey.mintnoteapi.repository.NoteRepository;
import com.github.tripolkaandrey.mintnoteapi.service.TranslationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.security.Principal;
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
    public Flux<Note> getNotes(Principal principal) {
        return noteRepository.findAllByUserId(principal.getName());
    }

    @GetMapping("{id}/")
    public Mono<ResponseEntity<Note>> getNote(Principal principal, @PathVariable String id) {
        return noteRepository.findById(id)
                .flatMap(n -> isUserAllowed(principal, n))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(NoteNotFoundException::new));
    }

    private Mono<Note> isUserAllowed(Principal principal, Note note) {
        if (principal.getName().equals(note.getUserId())) {
            return Mono.just(note);
        } else {
            return Mono.error(new AccessDeniedException());
        }
    }

    @PutMapping("{id}/name/")
    public Mono<ResponseEntity<Note>> updateName(Principal principal, @PathVariable String id, @RequestBody String name) {
        return updateNote(principal, id, note -> note.setName(name));
    }

    @PutMapping("{id}/tags/")
    public Mono<ResponseEntity<Note>> updateTags(Principal principal, @PathVariable String id, @RequestBody List<Tag> tags) {
        return updateNote(principal, id, note -> note.setTags(tags));
    }

    @PutMapping("{id}/icon/")
    public Mono<ResponseEntity<Note>> updateIcon(Principal principal, @PathVariable String id, @RequestBody String icon) {
        return updateNote(principal, id, note -> note.setIcon(icon));
    }

    @PutMapping("{id}/parent/")
    public Mono<ResponseEntity<Note>> updateParent(Principal principal, @PathVariable String id, @RequestBody String parent) {
        return updateNote(principal, id, note -> note.setParent(parent));
    }

    @PutMapping("{id}/content/")
    public Mono<ResponseEntity<Note>> updateContent(Principal principal, @PathVariable String id, @RequestBody String content) {
        return updateNote(principal, id, note -> note.setContent(content));
    }

    private Mono<ResponseEntity<Note>> updateNote(Principal principal, String id, Consumer<Note> updater) {
        return noteRepository.findById(id)
                .flatMap(n -> isUserAllowed(principal, n))
                .flatMap(
                        n -> {
                            updater.accept(n);
                            n.setLastModifiedDate(new Date());
                            return noteRepository.save(n);
                        }
                )
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(NoteNotFoundException::new));
    }

    @PostMapping
    public Mono<ResponseEntity<Note>> createNote(Principal principal, @RequestBody Note note) {
        note.setId(null);
        note.setUserId(principal.getName());
        return noteRepository.save(note)
                .map(n -> ResponseEntity.created(URI.create("/notes/" + n.getId())).body(n))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("{id}/")
    public Mono<ResponseEntity<Void>> deleteNote(Principal principal, @PathVariable String id) {
        return noteRepository.findById(id)
                .flatMap(n -> isUserAllowed(principal, n))
                .switchIfEmpty(Mono.error(NoteNotFoundException::new))
                .then(noteRepository.deleteById(id))
                .map(ResponseEntity::ok);
    }

    @GetMapping("{id}/translation/{targetLanguage}/")
    public Mono<ResponseEntity<String>> translate(Principal principal, @PathVariable String id, @PathVariable String targetLanguage) {
        return noteRepository.findById(id)
                .flatMap(n -> isUserAllowed(principal, n))
                .flatMap(n -> translationService.translate(n.getContent(), targetLanguage))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(NoteNotFoundException::new));
    }
}