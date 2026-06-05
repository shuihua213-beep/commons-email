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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    void testParseAddressListNull() {
        final Set<String> result = EmailUtils.parseAddressList(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseAddressListEmpty() {
        final Set<String> result = EmailUtils.parseAddressList("");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseAddressListSingleAddress() {
        final Set<String> result = EmailUtils.parseAddressList("user@domain.com");
        assertEquals(1, result.size());
        assertTrue(result.contains("user@domain.com"));
    }

    @Test
    void testParseAddressListWithDisplayName() {
        final Set<String> result = EmailUtils.parseAddressList("John Doe <john@example.com>");
        assertEquals(1, result.size());
        assertTrue(result.contains("john@example.com"));
    }

    @Test
    void testParseAddressListWithQuotedName() {
        final Set<String> result = EmailUtils.parseAddressList("\"Doe, John\" <john@example.com>");
        assertEquals(1, result.size());
        assertTrue(result.contains("john@example.com"));
    }

    @Test
    void testParseAddressListMultipleRecipients() {
        final Set<String> result = EmailUtils.parseAddressList("user1@domain.com, user2@domain.com");
        assertEquals(2, result.size());
        assertTrue(result.contains("user1@domain.com"));
        assertTrue(result.contains("user2@domain.com"));
    }

    @Test
    void testParseAddressListMixedFormats() {
        final Set<String> result = EmailUtils.parseAddressList(
                "\"Smith, Alice\" <alice@example.com>, bob@example.com, Charlie <charlie@example.com>");
        assertEquals(3, result.size());
        assertTrue(result.contains("alice@example.com"));
        assertTrue(result.contains("bob@example.com"));
        assertTrue(result.contains("charlie@example.com"));
    }

    @Test
    void testParseAddressListWithWhitespace() {
        final Set<String> result = EmailUtils.parseAddressList("  user@domain.com  ,  another@domain.com  ");
        assertEquals(2, result.size());
        assertTrue(result.contains("user@domain.com"));
        assertTrue(result.contains("another@domain.com"));
    }

    @Test
    void testParseAddressListDuplicateAddresses() {
        final Set<String> result = EmailUtils.parseAddressList("user@domain.com, User <user@domain.com>");
        assertEquals(1, result.size());
        assertTrue(result.contains("user@domain.com"));
    }

    @Test
    void testParseAddressListInvalidMissingAt() {
        final Set<String> result = EmailUtils.parseAddressList("invalid-email");
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseAddressListInvalidMultipleAt() {
        final Set<String> result = EmailUtils.parseAddressList("user@@domain.com");
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseAddressListInvalidWhitespaceInAddress() {
        final Set<String> result = EmailUtils.parseAddressList("user @domain.com");
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseAddressListMixedValidAndInvalid() {
        final Set<String> result = EmailUtils.parseAddressList("valid@domain.com, invalid, also-valid@test.org");
        assertEquals(2, result.size());
        assertTrue(result.contains("valid@domain.com"));
        assertTrue(result.contains("also-valid@test.org"));
    }

    @Test
    void testParseAddressListCaseNormalization() {
        final Set<String> result = EmailUtils.parseAddressList("User@Domain.COM");
        assertEquals(1, result.size());
        assertTrue(result.contains("user@domain.com"));
    }

    @Test
    void testParseAddressListEmptyAngleBrackets() {
        final Set<String> result = EmailUtils.parseAddressList("Name <>");
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseAddressListCommaInQuotedName() {
        final Set<String> result = EmailUtils.parseAddressList(
                "\"Last, First\" <first.last@example.com>, other@example.com");
        assertEquals(2, result.size());
        assertTrue(result.contains("first.last@example.com"));
        assertTrue(result.contains("other@example.com"));
    }
}
