package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.entity.Note;
import com.github.tripolkaandrey.mintnoteapi.repository.NoteRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

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
}