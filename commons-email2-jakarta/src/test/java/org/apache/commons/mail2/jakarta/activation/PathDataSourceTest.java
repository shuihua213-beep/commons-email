/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.mail2.jakarta.activation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PathDataSourceTest {

    @TempDir
    Path tempDir;

    private Path testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("test-attachment.txt");
        Files.writeString(testFile, "Test attachment content for resource leak testing");
    }

    @Test
    void testCloseReleasesAllStreams() throws IOException {
        final PathDataSource dataSource = new PathDataSource(testFile);

        final InputStream stream1 = dataSource.getInputStream();
        final InputStream stream2 = dataSource.getInputStream();
        final InputStream stream3 = dataSource.getInputStream();

        assertFalse(stream1.read() == -1, "Stream 1 should have content");
        stream1.skipNBytes(Long.MAX_VALUE > Files.size(testFile) ? Files.size(testFile) : Long.MAX_VALUE);

        dataSource.close();

        assertTrue(stream1.available() == 0 || !isStreamOpen(stream1), "Stream 1 should be closed after close()");
        assertTrue(stream2.available() == 0 || !isStreamOpen(stream2), "Stream 2 should be closed after close()");
        assertTrue(stream3.available() == 0 || !isStreamOpen(stream3), "Stream 3 should be closed after close()");
    }

    @Test
    void testFileCanBeDeletedAfterClose() throws IOException {
        final PathDataSource dataSource = new PathDataSource(testFile);

        for (int i = 0; i < 10; i++) {
            try (InputStream is = dataSource.getInputStream()) {
                is.readAllBytes();
            }
        }

        dataSource.close();

        assertTrue(Files.deleteIfExists(testFile), "File should be deletable after PathDataSource.close()");
        assertFalse(Files.exists(testFile), "File should not exist after deletion");
    }

    @Test
    void testRepeatedReadsDoNotLeakHandles() throws IOException {
        final Path testAttachment = tempDir.resolve("repeated-attachment.bin");
        final byte[] testData = new byte[1024];
        for (int i = 0; i < testData.length; i++) {
            testData[i] = (byte) (i % 256);
        }
        Files.write(testAttachment, testData);

        final PathDataSource dataSource = new PathDataSource(testAttachment);

        for (int i = 0; i < 100; i++) {
            final InputStream is = dataSource.getInputStream();
            final byte[] buffer = is.readAllBytes();
            assertEquals(testData.length, buffer.length, "Iteration " + i + ": should read full content");
        }

        dataSource.close();

        final File file = testAttachment.toFile();
        assertTrue(file.delete(), "File should be deletable after 100 reads and close()");
    }

    @Test
    void testCloseIsIdempotent() throws IOException {
        final PathDataSource dataSource = new PathDataSource(testFile);

        dataSource.getInputStream();
        dataSource.getInputStream();

        dataSource.close();
        dataSource.close();
        dataSource.close();
    }

    @Test
    void testGetInputStreamReturnsNewStreamEachTime() throws IOException {
        final PathDataSource dataSource = new PathDataSource(testFile);

        final InputStream stream1 = dataSource.getInputStream();
        final InputStream stream2 = dataSource.getInputStream();

        assertFalse(stream1 == stream2, "Each call to getInputStream() should return a new stream");

        stream1.close();
        stream2.close();
        dataSource.close();
    }

    @Test
    void testGetContentType() {
        final Path textFile = tempDir.resolve("document.txt");
        try {
            Files.writeString(textFile, "text content");
            final PathDataSource dataSource = new PathDataSource(textFile);
            final String contentType = dataSource.getContentType();
            assertTrue(contentType != null && !contentType.isEmpty(), "Content type should not be null or empty");
        } finally {
            Files.deleteIfExists(textFile);
        }
    }

    @Test
    void testGetName() {
        final PathDataSource dataSource = new PathDataSource(testFile);
        assertEquals("test-attachment.txt", dataSource.getName(), "getName() should return the file name");
    }

    @Test
    void testGetPath() {
        final PathDataSource dataSource = new PathDataSource(testFile);
        assertEquals(testFile, dataSource.getPath(), "getPath() should return the original path");
    }

    private boolean isStreamOpen(final InputStream stream) {
        try {
            stream.read();
            return true;
        } catch (final IOException e) {
            return false;
        }
    }
}
