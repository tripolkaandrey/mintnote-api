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
                                                                        @RequestParam(required = false, defaultValue = Path.SEPARATOR) String path) {
        return Mono.just(Path.parse(path))
                .flatMap(parsed -> directoriesRepository.existsByUserIdAndPath(principal.getName(), parsed))
                .flatMapMany(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return directoriesRepository.findAllByUserId(principal.getName());
                    } else {
                        return Mono.error(DirectoryNotFoundException::new);
                    }
                })
                .filter(x -> x.getParent().equals(path))
                .collectList()
                .zipWith(
                        noteRepository.findAllByUserIdAndParent(principal.getName(), path).collectList(),
                        DirectoryContents::new
                )
                .map(ResponseEntity::ok);
    }
}