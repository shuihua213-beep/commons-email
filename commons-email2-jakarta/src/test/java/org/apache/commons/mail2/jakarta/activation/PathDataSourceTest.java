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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import jakarta.activation.FileTypeMap;
import org.junit.jupiter.api.Test;

class PathDataSourceTest {

    @Test
    void testGetInputStreamClosesAfterEndOfFile() throws IOException {
        final Path path = Files.createTempFile("path-data-source", ".txt");
        try {
            final byte[] expected = "Test data for PathDataSource".getBytes(StandardCharsets.UTF_8);
            Files.write(path, expected);
            final PathDataSource dataSource = new PathDataSource(path);
            for (int index = 0; index < 3; index++) {
                final InputStream inputStream = dataSource.getInputStream();
                assertArrayEquals(expected, inputStream.readAllBytes());
                assertThrows(IOException.class, inputStream::read);
            }
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void testGetInputStreamReleasesDeleteOnClosePathAfterRead() throws IOException {
        final Path path = Files.createTempFile("path-data-source", ".txt");
        try {
            final byte[] expected = "Test data for PathDataSource".getBytes(StandardCharsets.UTF_8);
            Files.write(path, expected);
            final PathDataSource dataSource = new PathDataSource(path, FileTypeMap.getDefaultFileTypeMap(), StandardOpenOption.DELETE_ON_CLOSE);
            assertArrayEquals(expected, dataSource.getInputStream().readAllBytes());
            assertFalse(Files.exists(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }
}
