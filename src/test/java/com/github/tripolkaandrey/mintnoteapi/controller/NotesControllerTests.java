package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.entity.Directories;
import com.github.tripolkaandrey.mintnoteapi.entity.Note;
import com.github.tripolkaandrey.mintnoteapi.entity.Tag;
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

    private Note createNoteInDb(Note note) {
        return noteRepository.save(note).block();
    }

    @Nested
    class CreateTests {
        @Test
        @WithMockUser(username = TEST_USER_ID)
        void AuthenticatedUser_Created() {
            Note testNote = new Note.Builder()
                    .withParent(Directories.ROOT.toString())
                    .build();

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
            webTestClient.post().uri(NOTES_BASE_URL)
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
            List<Note> testNotes = new ArrayList<>();
            for (int i = 0; i < amountOfNotes; i++) {
                testNotes.add(createNoteInDb((new Note.Builder().withUserId(TEST_USER_ID).build())));
            }

            webTestClient.get().uri(NOTES_BASE_URL)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Note.class)
                    .hasSize(amountOfNotes)
                    .consumeWith(response -> {
                        List<Note> responseNotes = response.getResponseBody();
                        Assertions.assertNotNull(responseNotes);
                        Assertions.assertTrue(responseNotes.containsAll(testNotes));
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
            Note testNote = createNoteInDb(new Note.Builder().withUserId(TEST_USER_ID).build());

            webTestClient.get().uri(NOTES_BASE_URL + testNote.getId() + "/")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Note.class)
                    .consumeWith(response -> {
                        Note responseNote = response.getResponseBody();
                        Assertions.assertNotNull(responseNote);
                        Assertions.assertEquals(testNote, responseNote);
                    });
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void ExistingId_NotOwner_Forbidden() {
            String randomUserId = RandomStringUtils.randomAlphanumeric(10);
            Note testNote = createNoteInDb(new Note.Builder().withUserId(randomUserId).build());

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
                    .expectStatus().isNotFound();
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
            Note testNote = createNoteInDb(new Note.Builder().withUserId(TEST_USER_ID).build());
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
            Note testNote = createNoteInDb(new Note.Builder().withUserId(TEST_USER_ID).build());
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
            Note testNote = createNoteInDb(new Note.Builder().withUserId(TEST_USER_ID).build());
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
            Note testNote = createNoteInDb(new Note.Builder().withUserId(TEST_USER_ID).build());
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

            webTestClient.put().uri(NOTES_BASE_URL + randomString + "/" + parameterName + "/")
                    .body(Mono.just(randomString), String.class)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @ParameterizedTest
        @WithMockUser(username = TEST_USER_ID)
        @ValueSource(strings = {"name", "parent", "icon", "content"})
        void Property_NotOwner_Forbidden(String parameterName) {
            String randomUserId = RandomStringUtils.randomAlphanumeric(10);
            Note testNote = createNoteInDb(new Note.Builder().withUserId(randomUserId).build());
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + testNote.getId() + "/" + parameterName + "/")
                    .body(Mono.just(randomString), String.class)
                    .exchange()
                    .expectStatus().isForbidden();
        }

        @ParameterizedTest
        @ValueSource(strings = {"name", "parent", "icon", "content"})
        void Property_UnauthenticatedUser_Unauthorized(String parameterName) {
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + randomString + "/" + parameterName)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void Tags_Ok() {
            Note testNote = createNoteInDb(new Note.Builder().withUserId(TEST_USER_ID).build());
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
            String randomUserId = RandomStringUtils.randomAlphanumeric(10);
            Note testNote = createNoteInDb(new Note.Builder().withUserId(randomUserId).build());
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
                    .expectStatus().isNotFound();
        }

        @Test
        void Tags_Unauthenticated_Unauthorized() {
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.put().uri(NOTES_BASE_URL + randomString + "/tags")
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }

    @Nested
    class DeleteTests {
        @Test
        @WithMockUser(username = TEST_USER_ID)
        void ExistingId_Ok() {
            Note testNote = createNoteInDb(new Note.Builder().withUserId(TEST_USER_ID).build());

            webTestClient.delete().uri(NOTES_BASE_URL + testNote.getId() + "/")
                    .exchange()
                    .expectStatus().isOk();

            Assertions.assertFalse(noteRepository.existsById(testNote.getId()).block());
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void NotExistingId_NotFound() {
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.delete().uri(NOTES_BASE_URL + randomString)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void NotOwner_Forbidden() {
            String randomUserId = RandomStringUtils.randomAlphanumeric(10);
            Note testNote = createNoteInDb(new Note.Builder().withUserId(randomUserId).build());

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
}