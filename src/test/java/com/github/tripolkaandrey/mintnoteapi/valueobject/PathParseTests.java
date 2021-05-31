package com.github.tripolkaandrey.mintnoteapi.valueobject;

import com.github.tripolkaandrey.mintnoteapi.entity.Directories;
import com.github.tripolkaandrey.mintnoteapi.exception.InvalidPathException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PathTests {
    @Nested
    class Parse {
        @Test
        void Null_InvalidPathException() {
            Assertions.assertThrows(InvalidPathException.class, () -> Path.parse(null));
        }

        @Test
        void Separator_ReturnsRoot() {
            Assertions.assertEquals(Directories.ROOT, Path.parse(Path.SEPARATOR));
        }

        @Test
        void NoSeparatorPresent_InvalidPathException() {
            String randomString = RandomStringUtils.randomAlphanumeric(10);
            Assertions.assertThrows(InvalidPathException.class, () -> Path.parse(randomString));
        }

        @Test
        void TrailingSeparator_InvalidPathException() {
            String randomString = RandomStringUtils.randomAlphanumeric(10);
            Assertions.assertThrows(InvalidPathException.class, () -> Path.parse(randomString + "/"));
        }

        @Test
        void ValidInput_DoesNotThrowInvalidPathException() {
            String randomString = RandomStringUtils.randomAlphanumeric(5) + Path.SEPARATOR + RandomStringUtils.randomAlphanumeric(5);
            Assertions.assertDoesNotThrow(() -> Path.parse(randomString));
        }
    }
}