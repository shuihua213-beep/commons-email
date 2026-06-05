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
package org.apache.commons.mail2.jakarta;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.mail2.jakarta.activation.PathDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link PathDataSource}.
 */
class PathDataSourceTest {

    @TempDir
    Path tempDir;

    @Test
    void testGetContentType() throws IOException {
        final Path file = Files.createTempFile(tempDir, "test", ".txt");
        Files.write(file, "test".getBytes());
        final PathDataSource ds = new PathDataSource(file);
        assertNotNull(ds.getContentType());
    }

    @Test
    void testGetInputStream() throws IOException {
        final byte[] content = "Hello PathDataSource".getBytes();
        final Path file = Files.createTempFile(tempDir, "test", ".dat");
        Files.write(file, content);
        final PathDataSource ds = new PathDataSource(file);
        try (InputStream inputStream = ds.getInputStream()) {
            final byte[] readData = new byte[content.length];
            final int bytesRead = inputStream.read(readData);
            assertEquals(content.length, bytesRead);
            assertArrayEquals(content, readData);
        }
    }

    @Test
    void testGetName() throws IOException {
        final Path file = Files.createTempFile(tempDir, "testGetName", ".tmp");
        final PathDataSource ds = new PathDataSource(file);
        assertEquals("testGetName.tmp", ds.getName());
    }

    @Test
    void testGetOutputStream() throws IOException {
        final Path file = Files.createTempFile(tempDir, "test", ".dat");
        final PathDataSource ds = new PathDataSource(file);
        assertNotNull(ds.getOutputStream());
    }

    @Test
    void testGetPath() throws IOException {
        final Path file = Files.createTempFile(tempDir, "test", ".dat");
        final PathDataSource ds = new PathDataSource(file);
        assertEquals(file, ds.getPath());
    }

    @Test
    void testCloseReleasesAllStreams() throws IOException {
        final Path file = Files.createTempFile(tempDir, "testClose", ".dat");
        Files.write(file, "data".getBytes());
        final PathDataSource ds = new PathDataSource(file);

        final List<InputStream> streams = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            streams.add(ds.getInputStream());
        }

        ds.close();

        assertDoesNotThrow(() -> Files.delete(file));
    }

    @Test
    void testCloseAfterIndividualStreamClose() throws IOException {
        final Path file = Files.createTempFile(tempDir, "testMixed", ".dat");
        Files.write(file, "data".getBytes());
        final PathDataSource ds = new PathDataSource(file);

        final InputStream is1 = ds.getInputStream();
        final InputStream is2 = ds.getInputStream();
        final InputStream is3 = ds.getInputStream();

        is1.close();
        is3.close();

        ds.close();

        assertDoesNotThrow(() -> Files.delete(file));
    }

    @Test
    void testRepeatedReadDoesNotLeaveFileHandles() throws IOException {
        final Path file = Files.createTempFile(tempDir, "testRepeated", ".dat");
        Files.write(file, "bulk attachment test data".getBytes());

        for (int iteration = 0; iteration < 50; iteration++) {
            final PathDataSource ds = new PathDataSource(file);
            try {
                for (int i = 0; i < 20; i++) {
                    final InputStream is = ds.getInputStream();
                    final byte[] buf = new byte[256];
                    while (is.read(buf) != -1) {
                    }
                    is.close();
                }
            } finally {
                ds.close();
            }
        }

        assertDoesNotThrow(() -> Files.delete(file));
    }

    @Test
    void testRepeatedReadWithoutExplicitCloseOnEachStream() throws IOException {
        final Path file = Files.createTempFile(tempDir, "testBulk", ".dat");
        Files.write(file, "bulk data".getBytes());

        final PathDataSource ds = new PathDataSource(file);
        final List<InputStream> leaked = new ArrayList<>();
        try {
            for (int i = 0; i < 30; i++) {
                leaked.add(ds.getInputStream());
            }
        } finally {
            ds.close();
        }

        assertDoesNotThrow(() -> Files.delete(file));
    }

    @Test
    void testFileDeletableAfterClose() throws IOException {
        final Path file = Files.createTempFile(tempDir, "testDeletable", ".dat");
        Files.write(file, "test".getBytes());

        final PathDataSource ds = new PathDataSource(file);
        ds.getInputStream();
        ds.getInputStream();
        ds.close();

        assertTrue(Files.deleteIfExists(file));
    }

    @Test
    void testCloseIsIdempotent() throws IOException {
        final Path file = Files.createTempFile(tempDir, "testIdempotent", ".dat");
        Files.write(file, "data".getBytes());
        final PathDataSource ds = new PathDataSource(file);

        ds.getInputStream();
        ds.getInputStream();

        ds.close();
        ds.close();

        assertDoesNotThrow(() -> Files.delete(file));
    }

    @Test
    void testConstructorWithOptions() throws IOException {
        final Path file = Files.createTempFile(tempDir, "testOptions", ".dat");
        Files.write(file, "data".getBytes());
        final PathDataSource ds = new PathDataSource(file, null, StandardOpenOption.READ);
        try (InputStream is = ds.getInputStream()) {
            assertNotNull(is);
        }
    }
}