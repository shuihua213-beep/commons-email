package org.apache.commons.mail2.jakarta.activation;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathDataSourceTest {

    @Test
    public void testInputStreamRelease() throws Exception {
        Path tempFile = Files.createTempFile("test-attachment", ".txt");
        Files.write(tempFile, "test data".getBytes());

        PathDataSource dataSource = new PathDataSource(tempFile);
        
        for (int i = 0; i < 10; i++) {
            InputStream is = dataSource.getInputStream();
            assertNotNull(is);
            // Read fully without explicitly closing
            byte[] data = IOUtils.toByteArray(is);
            assertEquals("test data", new String(data));
        }

        boolean deleted = Files.deleteIfExists(tempFile);
        assertTrue(deleted, "File should be deleted successfully");
    }
}