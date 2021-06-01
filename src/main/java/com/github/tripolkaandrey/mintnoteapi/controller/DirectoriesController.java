package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.dto.DirectoryContents;
import com.github.tripolkaandrey.mintnoteapi.entity.Directory;
import com.github.tripolkaandrey.mintnoteapi.exception.DirectoryAlreadyExistsException;
import com.github.tripolkaandrey.mintnoteapi.exception.DirectoryNotFoundException;
import com.github.tripolkaandrey.mintnoteapi.repository.DirectoriesRepository;
import com.github.tripolkaandrey.mintnoteapi.repository.NoteRepository;
import com.github.tripolkaandrey.mintnoteapi.valueobject.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/directories/")
public final class DirectoriesController {
    private final DirectoriesRepository directoriesRepository;
    private final NoteRepository noteRepository;

    public DirectoriesController(DirectoriesRepository directoriesRepository, NoteRepository noteRepository) {
        this.directoriesRepository = directoriesRepository;
        this.noteRepository = noteRepository;
    }

    @GetMapping
    public Mono<ResponseEntity<DirectoryContents>> getDirectoryContents(Principal principal,
                                                                        @RequestParam(defaultValue = Path.SEPARATOR) String path) {
        return Mono.just(Path.parse(path))
                .flatMap(p -> pathExistsFilter(principal.getName(), p))
                .flatMapMany(p -> directoriesRepository.findAllByUserIdAndParent(principal.getName(), p.toString()))
                .collectList()
                .zipWith(
                        noteRepository.findAllByUserIdAndParent(principal.getName(), path).collectList(),
                        DirectoryContents::new
                )
                .map(ResponseEntity::ok);
    }

    private Mono<Path> pathExistsFilter(String userId, Path path) {
        return directoriesRepository.existsByUserIdAndPath(userId, path)
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists)) {
                        return Mono.error(DirectoryNotFoundException::new);
                    } else {
                        return Mono.just(path);
                    }
                });
    }

    @PostMapping
    public Mono<ResponseEntity<Directory>> create(Principal principal, @RequestBody Directory directory) {
        return Mono.just(new Path(directory.getParent(), directory.getName()))
                .map(p -> Path.parse(p.toString()))
                .flatMap(p -> pathNotExistFilter(principal.getName(), p))
                .flatMap(p -> pathExistsFilter(principal.getName(), Path.parse(p.getParent())))
                .flatMap(p -> directoriesRepository.add(principal.getName(), directory))
                .map(p -> ResponseEntity.created(URI.create("/directories/?path=" + p)).body(directory));
    }

    private Mono<Path> pathNotExistFilter(String userId, Path path) {
        return directoriesRepository.existsByUserIdAndPath(userId, path)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(DirectoryAlreadyExistsException::new);
                    } else {
                        return Mono.just(path);
                    }
                });
    }
}