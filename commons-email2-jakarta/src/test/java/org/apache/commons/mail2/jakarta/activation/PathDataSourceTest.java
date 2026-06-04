/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.mail2.jakarta.activation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link PathDataSource}.
 */
class PathDataSourceTest {

    @Test
    void testGetContentType() throws IOException {
        final File tmpFile = File.createTempFile("test", ".txt");
        try {
            final PathDataSource dataSource = new PathDataSource(tmpFile.toPath());
            assertNotNull(dataSource.getContentType());
        } finally {
            tmpFile.delete();
        }
    }

    @Test
    void testGetInputStream() throws IOException {
        final byte[] testData = "Test data for PathDataSource".getBytes();
        final File tmpFile = File.createTempFile("test", ".txt");
        try {
            Files.write(tmpFile.toPath(), testData);
            final PathDataSource dataSource = new PathDataSource(tmpFile.toPath());
            try (InputStream inputStream = dataSource.getInputStream()) {
                final byte[] readData = new byte[testData.length];
                final int bytesRead = inputStream.read(readData);
                assertEquals(testData.length, bytesRead);
            }
        } finally {
            tmpFile.delete();
        }
    }

    @Test
    void testGetName() throws IOException {
        final File tmpFile = File.createTempFile("test", ".txt");
        try {
            final PathDataSource dataSource = new PathDataSource(tmpFile.toPath());
            assertEquals(tmpFile.getName(), dataSource.getName());
        } finally {
            tmpFile.delete();
        }
    }

    @Test
    void testGetOutputStream() throws IOException {
        final byte[] testData = "Test data writing".getBytes();
        final File tmpFile = File.createTempFile("test", ".txt");
        try {
            final PathDataSource dataSource = new PathDataSource(tmpFile.toPath());
            try (OutputStream outputStream = dataSource.getOutputStream()) {
                outputStream.write(testData);
            }
            final byte[] readData = Files.readAllBytes(tmpFile.toPath());
            assertEquals(new String(testData), new String(readData));
        } finally {
            tmpFile.delete();
        }
    }

    @Test
    void testGetPath() throws IOException {
        final File tmpFile = File.createTempFile("test", ".txt");
        try {
            final PathDataSource dataSource = new PathDataSource(tmpFile.toPath());
            assertEquals(tmpFile.toPath(), dataSource.getPath());
        } finally {
            tmpFile.delete();
        }
    }

    @Test
    void testConstructorWithOptions() throws IOException {
        final byte[] testData = "Test data with options".getBytes();
        final File tmpFile = File.createTempFile("test", ".txt");
        try {
            final PathDataSource dataSource = new PathDataSource(tmpFile.toPath(), null, StandardOpenOption.WRITE);
            assertNotNull(dataSource);
            try (OutputStream outputStream = dataSource.getOutputStream()) {
                outputStream.write(testData);
            }
        } finally {
            tmpFile.delete();
        }
    }

    @Test
    void testFileLocking() throws IOException {
        final File tmpFile = File.createTempFile("attachment", ".eml");
        try {
            final PathDataSource dataSource = new PathDataSource(tmpFile.toPath());
            // Just creating the PathDataSource shouldn't lock the file
            assertTrue(tmpFile.delete(), "File should be deletable after creating PathDataSource");
        } finally {
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
        }
    }

    @Test
    void testFileLockingAfterGetInputStream() throws IOException {
        final File tmpFile = File.createTempFile("attachment", ".eml");
        try {
            final PathDataSource dataSource = new PathDataSource(tmpFile.toPath());
            // Get the input stream but don't close it - should still allow file to be deleted?
            // Note: This is a test to verify the behavior.
            final InputStream is = dataSource.getInputStream();
            try {
                // In some implementations, opening a stream might lock the file.
                // We want to ensure that the file can still be deleted or that streams are properly managed.
            } finally {
                is.close();
            }
            // After closing the stream, file should definitely be deletable
            assertTrue(tmpFile.delete(), "File should be deletable after closing input stream");
        } finally {
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
        }
    }

    @Test
    void testNullPath() {
        assertThrows(NullPointerException.class, () -> new PathDataSource(null));
    }
}
