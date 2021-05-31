package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.dto.DirectoryContents;
import com.github.tripolkaandrey.mintnoteapi.exception.DirectoryNotFoundException;
import com.github.tripolkaandrey.mintnoteapi.repository.DirectoriesRepository;
import com.github.tripolkaandrey.mintnoteapi.repository.NoteRepository;
import com.github.tripolkaandrey.mintnoteapi.valueobject.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
                .flatMap(p -> pathExistsFilter(p, principal.getName()))
                .flatMapMany(p -> directoriesRepository.findAllByUserIdAndParent(principal.getName(), p.toString()))
                .collectList()
                .zipWith(
                        noteRepository.findAllByUserIdAndParent(principal.getName(), path).collectList(),
                        DirectoryContents::new
                )
                .map(ResponseEntity::ok);
    }

    private Mono<Path> pathExistsFilter(Path path, String userId) {
        return directoriesRepository.existsByUserIdAndPath(userId, path)
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists)) {
                        return Mono.error(DirectoryNotFoundException::new);
                    } else {
                        return Mono.just(path);
                    }
                });
    }
}