package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.entity.Tag;
import com.github.tripolkaandrey.mintnoteapi.repository.TagsRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
}