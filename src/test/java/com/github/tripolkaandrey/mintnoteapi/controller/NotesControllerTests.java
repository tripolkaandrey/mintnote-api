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

    @Nested
    class CreateTests {
        @Test
        @WithMockUser(username = TEST_USER_ID)
        void AuthenticatedUser_Created() {
            Note testNote = new Note();

            webTestClient.post().uri(NOTES_BASE_URL)
                    .body(Mono.just(testNote), Note.class)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(Note.class)
                    .consumeWith(response -> {
                        Note responseNote = response.getResponseBody();
                        Assertions.assertNotNull(responseNote);
                        Assertions.assertEquals(TEST_USER_ID, responseNote.getUserId());
                    });
        }

        @Test
        void UnauthenticatedUser_Unauthorized() {
            Note testNote = new Note();

            webTestClient.post().uri(NOTES_BASE_URL)
                    .body(Mono.just(testNote), Note.class)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
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
                    .hasSize(amountOfNotes)
                    .consumeWith(response -> {
                        List<Note> notes = response.getResponseBody();
                        Assertions.assertNotNull(notes);
                        notes.forEach(n -> Assertions.assertEquals(TEST_USER_ID, n.getUserId()));
                    });
        }

        @Test
        void AllNotes_Unauthenticated_Unauthorized() {
            webTestClient.get().uri(NOTES_BASE_URL)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void ExistingId_Owner_Ok() {
            Note testNote = createNoteInDb(TEST_USER_ID);

            webTestClient.get().uri(NOTES_BASE_URL + testNote.getId() + "/")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Note.class)
                    .consumeWith(response -> {
                        Note responseNote = response.getResponseBody();
                        Assertions.assertNotNull(responseNote);
                        Assertions.assertEquals(testNote.getId(), responseNote.getId());
                        Assertions.assertEquals(TEST_USER_ID, testNote.getUserId());
                    });
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void ExistingId_NotOwner_Forbidden() {
            Note testNote = createNoteInDb();

            webTestClient.get().uri(NOTES_BASE_URL + testNote.getId() + "/")
                    .exchange()
                    .expectStatus().isForbidden();
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

        @Test
        void Unauthenticated_Unauthorized() {
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.get().uri(NOTES_BASE_URL + randomString)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }

    @Nested
    class UpdateTests {
        @Test
        @WithMockUser(username = TEST_USER_ID)
        void Name_Ok() {
            Note testNote = createNoteInDb(TEST_USER_ID);
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/name/")
                    .body(Mono.just(randomString), String.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Note.class)
                    .consumeWith(response -> {
                        Note responseNote = response.getResponseBody();
                        Assertions.assertNotNull(responseNote);
                        Assertions.assertEquals(randomString, responseNote.getName());
                    });
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void Icon_Ok() {
            Note testNote = createNoteInDb(TEST_USER_ID);
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/icon/")
                    .body(Mono.just(randomString), String.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Note.class)
                    .consumeWith(response -> {
                        Note responseNote = response.getResponseBody();
                        Assertions.assertNotNull(responseNote);
                        Assertions.assertEquals(randomString, responseNote.getIcon());
                    });
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void Parent_Ok() {
            Note testNote = createNoteInDb(TEST_USER_ID);
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/parent/")
                    .body(Mono.just(randomString), String.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Note.class)
                    .consumeWith(response -> {
                        Note responseNote = response.getResponseBody();
                        Assertions.assertNotNull(responseNote);
                        Assertions.assertEquals(randomString, responseNote.getParent());
                    });
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void Content_Ok() {
            Note testNote = createNoteInDb(TEST_USER_ID);
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/content/")
                    .body(Mono.just(randomString), String.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Note.class)
                    .consumeWith(response -> {
                        Note responseNote = response.getResponseBody();
                        Assertions.assertNotNull(responseNote);
                        Assertions.assertEquals(randomString, responseNote.getContent());
                    });
        }

        @ParameterizedTest
        @WithMockUser(username = TEST_USER_ID)
        @ValueSource(strings = {"name", "parent", "icon", "content"})
        void Property_NotExistingId_NotFound(String parameterName) {
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + randomString + "/" + parameterName)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(NoteNotFoundException.class);
        }

        @ParameterizedTest
        @WithMockUser(username = TEST_USER_ID)
        @ValueSource(strings = {"name", "parent", "icon", "content"})
        void Property_NotOwner_Forbidden() {
            Note testNote = createNoteInDb();
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/name/")
                    .body(Mono.just(randomString), String.class)
                    .exchange()
                    .expectStatus().isForbidden();
        }

        @ParameterizedTest
        @ValueSource(strings = {"name", "parent", "icon", "content"})
        void Property_UnauthenticatedUser_Unauthorized() {
            Note testNote = createNoteInDb();
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/name/")
                    .body(Mono.just(randomString), String.class)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void Tags_Ok() {
            Note testNote = createNoteInDb(TEST_USER_ID);
            List<Tag> tags = new ArrayList<>();
            tags.add(new Tag("test", "test"));

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/tags/")
                    .body(Mono.just(tags), List.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Note.class).consumeWith(response -> {
                Note responseNote = response.getResponseBody();
                Assertions.assertNotNull(responseNote);
                Assertions.assertEquals(tags, responseNote.getTags());
            });
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void Tags_NotOwner_Forbidden() {
            Note testNote = createNoteInDb();
            List<Tag> tags = new ArrayList<>();
            tags.add(new Tag("test", "test"));

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/tags/")
                    .body(Mono.just(tags), List.class)
                    .exchange()
                    .expectStatus().isForbidden();
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void Tags_NotExistingId_NotFound() {
            String randomString = RandomStringUtils.randomAlphanumeric(10);
            List<Tag> tags = new ArrayList<>();
            tags.add(new Tag("test", "test"));

            webTestClient.put().uri(NOTES_BASE_URL + randomString + "/tags")
                    .body(Mono.just(tags), List.class)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(NoteNotFoundException.class);
        }

        @Test
        void Tags_Unauthenticated_Unauthorized() {
            String randomString = RandomStringUtils.randomAlphanumeric(10);
            List<Tag> tags = new ArrayList<>();
            tags.add(new Tag("test", "test"));

            webTestClient.put().uri(NOTES_BASE_URL + randomString + "/tags")
                    .body(Mono.just(tags), List.class)
                    .exchange()
                    .expectStatus().isUnauthorized();
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

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void NotOwner_Forbidden() {
            Note testNote = createNoteInDb();

            webTestClient.delete().uri(NOTES_BASE_URL + testNote.getId() + "/")
                    .exchange()
                    .expectStatus().isForbidden();

        }

        @Test
        void UnauthenticatedUser_Unauthorized() {
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.delete().uri(NOTES_BASE_URL + randomString)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }

    private Note createNoteInDb(String userId) {
        Note note = new Note();
        note.setUserId(userId);
        return noteRepository.save(note).block();
    }

    private Note createNoteInDb() {
        return noteRepository.save(new Note()).block();
    }
}