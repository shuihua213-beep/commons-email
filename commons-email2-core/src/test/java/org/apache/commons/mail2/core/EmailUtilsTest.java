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

package org.apache.commons.mail2.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * JUnit test case for EmailUtils Class
 */
class EmailUtilsTest {

    @Test
    void testClearEndOfLineCharacters() {
        assertNull(EmailUtils.replaceEndOfLineCharactersWithSpaces(null));
        assertEquals("", EmailUtils.replaceEndOfLineCharactersWithSpaces(""));
        assertEquals("   ", EmailUtils.replaceEndOfLineCharactersWithSpaces("   "));
        assertEquals("abcdefg", EmailUtils.replaceEndOfLineCharactersWithSpaces("abcdefg"));
        assertEquals("abc defg", EmailUtils.replaceEndOfLineCharactersWithSpaces("abc\rdefg"));
        assertEquals("abc defg", EmailUtils.replaceEndOfLineCharactersWithSpaces("abc\ndefg"));
        assertEquals("abc  defg", EmailUtils.replaceEndOfLineCharactersWithSpaces("abc\r\ndefg"));
        assertEquals("abc  defg", EmailUtils.replaceEndOfLineCharactersWithSpaces("abc\n\rdefg"));
    }

    @Test
    void testIsEmptyMap() {
        assertTrue(EmailUtils.isEmpty((Map<?, ?>) null));
        final HashMap<String, String> map = new HashMap<>();
        assertTrue(EmailUtils.isEmpty(map));
        map.put("k", "v");
        assertFalse(EmailUtils.isEmpty(map));
    }

    @Test
    void testIsEmptyString() {
        assertTrue(EmailUtils.isEmpty((String) null));
        assertTrue(EmailUtils.isEmpty(""));
        assertFalse(EmailUtils.isEmpty("a"));
    }

    @Test
    void testUrlEncoding() {
        assertNull(EmailUtils.encodeUrl(null));
        assertEquals("abcdefg", EmailUtils.encodeUrl("abcdefg"));
        assertEquals("0123456789", EmailUtils.encodeUrl("0123456789"));
        assertEquals("Test%20CID", EmailUtils.encodeUrl("Test CID"));
        assertEquals("joe.doe@apache.org", EmailUtils.encodeUrl("joe.doe@apache.org"));
        assertEquals("joe+doe@apache.org", EmailUtils.encodeUrl("joe+doe@apache.org"));
        assertEquals("peter%26paul%26mary@oldmusic.org", EmailUtils.encodeUrl("peter&paul&mary@oldmusic.org"));
    }

    @Test
    void testParseEmailAddresses_Null() {
        final List<String> result = EmailUtils.parseEmailAddresses(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseEmailAddresses_EmptyString() {
        final List<String> result = EmailUtils.parseEmailAddresses("");
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseEmailAddresses_WhitespaceOnly() {
        final List<String> result = EmailUtils.parseEmailAddresses("   \t  \n  ");
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseEmailAddresses_SingleAddress() {
        final List<String> result = EmailUtils.parseEmailAddresses("user@example.com");
        assertEquals(1, result.size());
        assertEquals("user@example.com", result.get(0));
    }

    @Test
    void testParseEmailAddresses_SingleAddressWithWhitespace() {
        final List<String> result = EmailUtils.parseEmailAddresses("  user@example.com  ");
        assertEquals(1, result.size());
        assertEquals("user@example.com", result.get(0));
    }

    @Test
    void testParseEmailAddresses_SingleAddressWithDisplayName() {
        final List<String> result = EmailUtils.parseEmailAddresses("John Doe <john@example.com>");
        assertEquals(1, result.size());
        assertEquals("john@example.com", result.get(0));
    }

    @Test
    void testParseEmailAddresses_SingleAddressWithQuotedDisplayName() {
        final List<String> result = EmailUtils.parseEmailAddresses("\"Doe, John\" <john@example.com>");
        assertEquals(1, result.size());
        assertEquals("john@example.com", result.get(0));
    }

    @Test
    void testParseEmailAddresses_MultipleAddresses() {
        final List<String> result = EmailUtils.parseEmailAddresses(
                "user1@example.com, user2@example.com, user3@example.com");
        assertEquals(3, result.size());
        assertEquals("user1@example.com", result.get(0));
        assertEquals("user2@example.com", result.get(1));
        assertEquals("user3@example.com", result.get(2));
    }

    @Test
    void testParseEmailAddresses_MultipleAddressesWithDisplayNames() {
        final List<String> result = EmailUtils.parseEmailAddresses(
                "John Doe <john@example.com>, \"Smith, Jane\" <jane@example.com>, Bob <bob@example.com>");
        assertEquals(3, result.size());
        assertEquals("john@example.com", result.get(0));
        assertEquals("jane@example.com", result.get(1));
        assertEquals("bob@example.com", result.get(2));
    }

    @Test
    void testParseEmailAddresses_MixedFormat() {
        final List<String> result = EmailUtils.parseEmailAddresses(
                "user1@example.com, \"Doe, John\" <john@example.com>, user3@example.com");
        assertEquals(3, result.size());
        assertEquals("user1@example.com", result.get(0));
        assertEquals("john@example.com", result.get(1));
        assertEquals("user3@example.com", result.get(2));
    }

    @Test
    void testParseEmailAddresses_WithWhitespace() {
        final List<String> result = EmailUtils.parseEmailAddresses(
                "  user1@example.com  ,  \"Doe, John\"  <  john@example.com  >  ,  user3@example.com  ");
        assertEquals(3, result.size());
        assertEquals("user1@example.com", result.get(0));
        assertEquals("john@example.com", result.get(1));
        assertEquals("user3@example.com", result.get(2));
    }

    @Test
    void testParseEmailAddresses_InvalidAddresses() {
        final List<String> result = EmailUtils.parseEmailAddresses(
                "invalid-email, another-invalid, @example.com");
        assertEquals(3, result.size());
        assertEquals("invalid-email", result.get(0));
        assertEquals("another-invalid", result.get(1));
        assertEquals("@example.com", result.get(2));
    }
}
