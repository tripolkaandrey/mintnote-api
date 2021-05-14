package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.entity.Note;
import com.github.tripolkaandrey.mintnoteapi.entity.Tag;
import com.github.tripolkaandrey.mintnoteapi.repository.NoteRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
class NotesControllerTests {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private NoteRepository noteRepository;

    @AfterEach
    public void cleanUp() {
        noteRepository.deleteAll().block();
    }

    @Test
    void getNotes_ReturnsAllNotes() {
        final int amountOfNotes = 5;
        for (int i = 0; i < amountOfNotes; i++) {
            noteRepository.save(new Note()).block();
        }

        webTestClient.get().uri("/notes/")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Note.class)
                .hasSize(amountOfNotes);
    }

    @Test
    void getNote_ExistingId_Ok() {
        Note testNote = noteRepository.save(new Note()).block();

        webTestClient.get().uri("/notes/" + testNote.getId() + "/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Note.class)
                .consumeWith(response -> {
                    Note responseNote = response.getResponseBody();
                    Assertions.assertEquals(testNote.getId(), responseNote.getId());
                });
    }

    @Test
    void getNote_NotExistingId_NotFound() {
        String randomString = RandomStringUtils.randomAlphanumeric(10);

        webTestClient.get().uri("/notes/" + randomString)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateName_ExistingId_Ok() {
        Note testNote = noteRepository.save(new Note()).block();
        String randomString = RandomStringUtils.randomAlphanumeric(10);

        webTestClient.put().uri("/notes/" + testNote.getId() + "/name/")
                .body(Mono.just(randomString), String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Note.class)
                .consumeWith(response -> {
                    Note responseNote = response.getResponseBody();
                    Assertions.assertEquals(randomString, responseNote.getName());
                });
    }

    @Test
    void updateIcon_ExistingId_Ok() {
        Note testNote = noteRepository.save(new Note()).block();
        String randomString = RandomStringUtils.randomAlphanumeric(10);

        webTestClient.put().uri("/notes/" + testNote.getId() + "/icon/")
                .body(Mono.just(randomString), String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Note.class)
                .consumeWith(response -> {
                    Note responseNote = response.getResponseBody();
                    Assertions.assertEquals(randomString, responseNote.getIcon());
                });
    }

    @Test
    void updateParent_ExistingId_Ok() {
        Note testNote = noteRepository.save(new Note()).block();
        String randomString = RandomStringUtils.randomAlphanumeric(10);

        webTestClient.put().uri("/notes/" + testNote.getId() + "/parent/")
                .body(Mono.just(randomString), String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Note.class)
                .consumeWith(response -> {
                    Note responseNote = response.getResponseBody();
                    Assertions.assertEquals(randomString, responseNote.getParent());
                });
    }

    @Test
    void updateContent_ExistingId_Ok() {
        Note testNote = noteRepository.save(new Note()).block();
        String randomString = RandomStringUtils.randomAlphanumeric(10);

        webTestClient.put().uri("/notes/" + testNote.getId() + "/content/")
                .body(Mono.just(randomString), String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Note.class)
                .consumeWith(response -> {
                    Note responseNote = response.getResponseBody();
                    Assertions.assertEquals(randomString, responseNote.getContent());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"name", "parent", "icon", "content"})
    void updateProperty_NotExistingId_NotFound(String parameterName) {
        String randomString = RandomStringUtils.randomAlphanumeric(10);

        webTestClient.put().uri("/notes/" + randomString + "/" + parameterName)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateTags_Existing_Ok() {
        Note testNote = noteRepository.save(new Note()).block();
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("test", "test"));

        webTestClient.put().uri("/notes/" + testNote.getId() + "/tags/")
                .body(Mono.just(tags), List.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Note.class).consumeWith(response -> {
            Note responseNote = response.getResponseBody();
            Assertions.assertEquals(tags, responseNote.getTags());
        });
    }

    @Test
    void updateTags_NotExisting_NotFound() {
        String randomString = RandomStringUtils.randomAlphanumeric(10);
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("test", "test"));

        webTestClient.put().uri("/notes/" + randomString + "/tags")
                .body(Mono.just(tags), List.class)
                .exchange()
                .expectStatus().isNotFound();
    }
}