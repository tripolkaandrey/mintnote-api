package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.entity.Tag;
import com.github.tripolkaandrey.mintnoteapi.exception.TagAlreadyExistsException;
import com.github.tripolkaandrey.mintnoteapi.repository.TagsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/tags/")
public class TagsController {
    private final TagsRepository tagsRepository;

    public TagsController(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    @GetMapping
    public Flux<Tag> getAll(Principal principal) {
        return tagsRepository.findAllByUserId(principal.getName());
    }

    @PostMapping
    public Mono<ResponseEntity<Tag>> create(Principal principal, @RequestBody Tag tag) {
        return tagsRepository
                .exists(principal.getName(), tag)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(TagAlreadyExistsException::new);
                    }
                    return Mono.empty();
                })
                .then(tagsRepository.add(principal.getName(), tag))
                .map(t -> ResponseEntity.created(URI.create("/tags/")).body(t));
    }
}