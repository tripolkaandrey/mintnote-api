package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.dto.TinyType;
import com.github.tripolkaandrey.mintnoteapi.entity.Note;
import com.github.tripolkaandrey.mintnoteapi.entity.Tag;
import com.github.tripolkaandrey.mintnoteapi.exception.AccessDeniedException;
import com.github.tripolkaandrey.mintnoteapi.exception.DirectoryNotFoundException;
import com.github.tripolkaandrey.mintnoteapi.exception.NoteNotFoundException;
import com.github.tripolkaandrey.mintnoteapi.repository.DirectoriesRepository;
import com.github.tripolkaandrey.mintnoteapi.repository.NoteRepository;
import com.github.tripolkaandrey.mintnoteapi.service.TranslationService;
import com.github.tripolkaandrey.mintnoteapi.valueobject.Path;
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
    private final DirectoriesRepository directoriesRepository;
    private final TranslationService translationService;

    public NotesController(NoteRepository noteRepository, DirectoriesRepository directoriesRepository, TranslationService translationService) {
        this.noteRepository = noteRepository;
        this.directoriesRepository = directoriesRepository;
        this.translationService = translationService;
    }

    @GetMapping
    public Flux<Note> getAll(Principal principal) {
        return noteRepository.findAllByUserId(principal.getName());
    }

    @GetMapping("{id}/")
    public Mono<ResponseEntity<Note>> get(Principal principal, @PathVariable String id) {
        return noteRepository.findById(id)
                .flatMap(n -> accessFilter(principal.getName(), n))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(NoteNotFoundException::new));
    }

    private Mono<Note> accessFilter(String userId, Note note) {
        if (userId.equals(note.getUserId())) {
            return Mono.just(note);
        } else {
            return Mono.error(new AccessDeniedException());
        }
    }

    @PutMapping("{id}/name/")
    public Mono<ResponseEntity<Note>> updateName(Principal principal, @PathVariable String id, @RequestBody String name) {
        return updateProperty(principal.getName(), id, note -> note.setName(name));
    }

    @PutMapping("{id}/tags/")
    public Mono<ResponseEntity<Note>> updateTags(Principal principal, @PathVariable String id, @RequestBody List<Tag> tags) {
        return updateProperty(principal.getName(), id, note -> note.setTags(tags));
    }

    @PutMapping("{id}/icon/")
    public Mono<ResponseEntity<Note>> updateIcon(Principal principal, @PathVariable String id, @RequestBody String icon) {
        return updateProperty(principal.getName(), id, note -> note.setIcon(icon));
    }

    @PutMapping("{id}/parent/")
    public Mono<ResponseEntity<Note>> updateParent(Principal principal, @PathVariable String id, @RequestBody String parent) {
        //TODO: check for parent directory existence
        return updateProperty(principal.getName(), id, note -> note.setParent(parent));
    }

    @PutMapping("{id}/content/")
    public Mono<ResponseEntity<Note>> updateContent(Principal principal, @PathVariable String id, @RequestBody String content) {
        return updateProperty(principal.getName(), id, note -> note.setContent(content));
    }

    private Mono<ResponseEntity<Note>> updateProperty(String userId, String id, Consumer<Note> updater) {
        return noteRepository.findById(id)
                .flatMap(n -> accessFilter(userId, n))
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
    public Mono<ResponseEntity<Note>> create(Principal principal, @RequestBody Note note) {
        note.setId(null);
        note.setUserId(principal.getName());
        return directoriesRepository
                .existsByUserIdAndPath(principal.getName(), Path.parse(note.getParent()))
                .flatMap(exists -> {
                        if(Boolean.TRUE.equals(exists)) {
                            return noteRepository.save(note)
                                    .map(n -> ResponseEntity.created(URI.create("/notes/" + n.getId())).body(n));
                        } else {
                            return Mono.error(DirectoryNotFoundException::new);
                        }
                });
    }

    @DeleteMapping("{id}/")
    public Mono<ResponseEntity<Void>> delete(Principal principal, @PathVariable String id) {
        return noteRepository.findById(id)
                .switchIfEmpty(Mono.error(NoteNotFoundException::new))
                .flatMap(n -> accessFilter(principal.getName(), n))
                .then(noteRepository.deleteById(id))
                .map(ResponseEntity::ok);
    }

    @GetMapping("{id}/translation/{targetLanguage}/")
    public Mono<ResponseEntity<TinyType<String>>> translate(Principal principal, @PathVariable String id, @PathVariable String targetLanguage) {
        return noteRepository.findById(id)
                .flatMap(n -> accessFilter(principal.getName(), n))
                .flatMap(n -> translationService.translate(n.getContent(), targetLanguage))
                .map(TinyType::of)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(NoteNotFoundException::new));
    }
}