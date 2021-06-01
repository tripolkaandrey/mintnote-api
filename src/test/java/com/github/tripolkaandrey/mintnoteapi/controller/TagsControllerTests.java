package com.github.tripolkaandrey.mintnoteapi.controller;

import com.github.tripolkaandrey.mintnoteapi.entity.Tag;
import com.github.tripolkaandrey.mintnoteapi.entity.Tags;
import com.github.tripolkaandrey.mintnoteapi.repository.TagsRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
class TagsControllerTests {
    private static final String TAGS_BASE_URL = "/tags/";
    private static final String TEST_USER_ID = "TEST_USER_ID";

    @Autowired
    private TagsRepository tagsRepository;
    @Autowired
    private WebTestClient webTestClient;
    private Tags testTags;

    @BeforeEach
    public void setUpDirectories() {
        Tag tag = new Tag.Builder()
                .withName("name")
                .withColor("#000000")
                .build();

        Tag tag2 = new Tag.Builder()
                .withName("name1")
                .withColor("#000000")
                .build();


        testTags = new Tags(TEST_USER_ID, List.of(tag, tag2));

        testTags = tagsRepository.save(testTags).block();
    }

    @AfterEach
    public void cleanUp() {
        tagsRepository.deleteAll().block();
    }

    @Nested
    class Get {
        @Test
        @WithMockUser(username = TEST_USER_ID)
        void AllTags_Ok() {
            webTestClient.get().uri(TAGS_BASE_URL)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Tag.class)
                    .hasSize(testTags.getCollection().size())
                    .consumeWith(response -> {
                        List<Tag> responseTags = response.getResponseBody();
                        Assertions.assertNotNull(responseTags);
                        Assertions.assertTrue(responseTags.containsAll(testTags.getCollection()));
                    });
        }

        @Test
        void AllTags_Unauthorized() {
            webTestClient.get().uri(TAGS_BASE_URL)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }
}