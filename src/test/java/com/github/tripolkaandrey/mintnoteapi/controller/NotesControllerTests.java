package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.entity.Note;
import com.github.tripolkaandrey.mintnoteapi.entity.Tag;
import com.github.tripolkaandrey.mintnoteapi.exception.NoteNotFoundException;
import com.github.tripolkaandrey.mintnoteapi.repository.NoteRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
class NotesControllerTests {
    private static final String NOTES_BASE_URL = "/notes/";
    private static final String TEST_USER_ID = "TEST_USER_ID";

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private NoteRepository noteRepository;

    @AfterEach
    public void cleanUp() {
        noteRepository.deleteAll().block();
    }

    @Test
    @WithMockUser(username = TEST_USER_ID)
    void Create_Created() {
        Note testNote = new Note();

        webTestClient.post().uri(NOTES_BASE_URL)
                .body(Mono.just(testNote), Note.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Note.class)
                .consumeWith(response -> {
                    Note responseNote = response.getResponseBody();
                    Assertions.assertNotNull(responseNote);
                });
    }

    @Nested
    class GetTests {
        @Test
        @WithMockUser(username = TEST_USER_ID)
        void AllNotes_Ok() {
            final int amountOfNotes = 5;
            for (int i = 0; i < amountOfNotes; i++) {
                createNoteInDb(TEST_USER_ID);
            }

            webTestClient.get().uri(NOTES_BASE_URL)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Note.class)
                    .hasSize(amountOfNotes);
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void ExistingId_Ok() {
            Note testNote = createNoteInDb(TEST_USER_ID);

            webTestClient.get().uri(NOTES_BASE_URL + testNote.getId() + "/")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Note.class)
                    .consumeWith(response -> {
                        Note responseNote = response.getResponseBody();
                        Assertions.assertEquals(testNote.getId(), responseNote.getId());
                    });
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void NotExistingId_NotFound() {
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.get().uri(NOTES_BASE_URL + randomString)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(NoteNotFoundException.class);
        }
    }

    @Nested
    class UpdateTests {
        @Test
        @WithMockUser(username = TEST_USER_ID)
        void UpdateName_ExistingId_Ok() {
            Note testNote = createNoteInDb(TEST_USER_ID);
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/name/")
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
        @WithMockUser(username = TEST_USER_ID)
        void UpdateIcon_ExistingId_Ok() {
            Note testNote = createNoteInDb(TEST_USER_ID);
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/icon/")
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
        @WithMockUser(username = TEST_USER_ID)
        void UpdateParent_ExistingId_Ok() {
            Note testNote = createNoteInDb(TEST_USER_ID);
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/parent/")
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
        @WithMockUser(username = TEST_USER_ID)
        void UpdateContent_ExistingId_Ok() {
            Note testNote = createNoteInDb(TEST_USER_ID);
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/content/")
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
        @WithMockUser(username = TEST_USER_ID)
        @ValueSource(strings = {"name", "parent", "icon", "content"})
        void UpdateProperty_NotExistingId_NotFound(String parameterName) {
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + randomString + "/" + parameterName)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(NoteNotFoundException.class);
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void UpdateTags_Existing_Ok() {
            Note testNote = createNoteInDb(TEST_USER_ID);
            List<Tag> tags = new ArrayList<>();
            tags.add(new Tag("test", "test"));

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/tags/")
                    .body(Mono.just(tags), List.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Note.class).consumeWith(response -> {
                Note responseNote = response.getResponseBody();
                Assertions.assertEquals(tags, responseNote.getTags());
            });
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void UpdateTags_NotExisting_NotFound() {
            String randomString = RandomStringUtils.randomAlphanumeric(10);
            List<Tag> tags = new ArrayList<>();
            tags.add(new Tag("test", "test"));

            webTestClient.put().uri(NOTES_BASE_URL + randomString + "/tags")
                    .body(Mono.just(tags), List.class)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(NoteNotFoundException.class);
        }
    }

    @Nested
    class DeleteTests {
        @Test
        @WithMockUser(username = TEST_USER_ID)
        void ExistingId_Ok() {
            Note note = createNoteInDb(TEST_USER_ID);

            webTestClient.delete().uri(NOTES_BASE_URL + note.getId() + "/")
                    .exchange()
                    .expectStatus().isOk();

            Assertions.assertFalse(noteRepository.existsById(note.getId()).block());
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void NotExistingId_NotFound() {
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.delete().uri(NOTES_BASE_URL + randomString)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(NoteNotFoundException.class);
        }
    }

    private Note createNoteInDb(String userId) {
        Note note = new Note();
        note.setUserId(userId);
        return noteRepository.save(note).block();
    }
}