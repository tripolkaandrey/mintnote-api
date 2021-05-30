package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.dto.DirectoryContents;
import com.github.tripolkaandrey.mintnoteapi.entity.Directories;
import com.github.tripolkaandrey.mintnoteapi.entity.Directory;
import com.github.tripolkaandrey.mintnoteapi.entity.Note;
import com.github.tripolkaandrey.mintnoteapi.repository.DirectoriesRepository;
import com.github.tripolkaandrey.mintnoteapi.repository.NoteRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
class DirectoriesControllerTests {
    private static final String DIRECTORIES_BASE_URL = "/directories/";
    private static final String TEST_USER_ID = "TEST_USER_ID";

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private DirectoriesRepository directoriesRepository;
    @Autowired
    private NoteRepository noteRepository;

    private Directories testDirectories;

    @BeforeEach
    public void setUpDirectories() {
        Directory directory = new Directory();
        directory.setParent(Directories.ROOT.toString());
        directory.setName("name");

        Directory directory2 = new Directory();
        directory2.setParent(Directories.ROOT.toString() + directory.getName());
        directory2.setName("name2");

        testDirectories = new Directories(TEST_USER_ID, List.of(directory, directory2));

        testDirectories = directoriesRepository.save(testDirectories).block();
    }

    @AfterEach
    public void cleanUp() {
        directoriesRepository.deleteAll().block();
        noteRepository.deleteAll().block();
    }

    private Note createNoteInDb(String parent) {
        Note note = new Note();
        note.setParent(parent);
        note.setUserId(TEST_USER_ID);
        return noteRepository.save(note).block();
    }

    @Nested
    class GetDirectoryContents {
        @Test
        @WithMockUser(username = TEST_USER_ID)
        void NoParams_RootContents_Ok() {
            Note note = createNoteInDb(Directories.ROOT.toString());

            webTestClient.get().uri(DIRECTORIES_BASE_URL)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(DirectoryContents.class)
                    .consumeWith(response -> {
                        DirectoryContents directoryContents = response.getResponseBody();
                        Assertions.assertNotNull(directoryContents);
                        Assertions.assertEquals(List.of(note), directoryContents.getNotes());
                    });
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void Ok() {
            Directory directory2 = testDirectories.getCollection().get(1);

            Note note = createNoteInDb(directory2.getParent()); //note will be in the same directory as directory2

            webTestClient.get().uri(DIRECTORIES_BASE_URL + "?path=" + directory2.getParent())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(DirectoryContents.class)
                    .consumeWith(response -> {
                        DirectoryContents directoryContents = response.getResponseBody();
                        Assertions.assertNotNull(directoryContents);
                        Assertions.assertNotNull(directoryContents.getDirectories());
                        Assertions.assertEquals(List.of(directory2), directoryContents.getDirectories());
                        Assertions.assertNotNull(directoryContents.getNotes());
                        Assertions.assertEquals(List.of(note), directoryContents.getNotes());
                    });
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void InvalidPath_BadRequest() {
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.get().uri(DIRECTORIES_BASE_URL + "?path=" + randomString)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @WithMockUser(username = TEST_USER_ID)
        void NotExistingPath_NotFound() {
            String randomString = RandomStringUtils.randomAlphanumeric(5) + "/" + RandomStringUtils.randomAlphanumeric(5);

            webTestClient.get().uri(DIRECTORIES_BASE_URL + "?path=" + randomString)
                    .exchange()
                    .expectStatus().isNotFound();
        }


        @Test
        void UnauthenticatedUser_Unauthorized() {
            String randomString = RandomStringUtils.randomAlphanumeric(10);

            webTestClient.get().uri(DIRECTORIES_BASE_URL + "?path=" + randomString)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }
}