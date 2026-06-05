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
    void testParseRfc822AddressesNull() {
        assertTrue(EmailUtils.parseRfc822Addresses(null).isEmpty());
    }

    @Test
    void testParseRfc822AddressesEmpty() {
        assertTrue(EmailUtils.parseRfc822Addresses("").isEmpty());
        assertTrue(EmailUtils.parseRfc822Addresses("   ").isEmpty());
    }

    @Test
    void testParseRfc822AddressesSimple() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("user@example.com");
        assertEquals(1, result.size());
        assertTrue(result.contains("user@example.com"));
    }

    @Test
    void testParseRfc822AddressesWithDisplayName() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("John Doe <john@example.com>");
        assertEquals(1, result.size());
        assertTrue(result.contains("john@example.com"));
    }

    @Test
    void testParseRfc822AddressesQuotedDisplayName() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("\"John, Doe\" <john@example.com>");
        assertEquals(1, result.size());
        assertTrue(result.contains("john@example.com"));
    }

    @Test
    void testParseRfc822AddressesQuotedDisplayNameWithSpecialChars() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("\"O'Brien, John\" <john.obrien@example.com>");
        assertEquals(1, result.size());
        assertTrue(result.contains("john.obrien@example.com"));
    }

    @Test
    void testParseRfc822AddressesMultipleRecipients() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("alice@example.com, bob@example.com");
        assertEquals(2, result.size());
        assertTrue(result.contains("alice@example.com"));
        assertTrue(result.contains("bob@example.com"));
    }

    @Test
    void testParseRfc822AddressesMultipleMixedFormats() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("Alice <alice@example.com>, bob@example.com, \"Charlie\" <charlie@example.com>");
        assertEquals(3, result.size());
        assertTrue(result.contains("alice@example.com"));
        assertTrue(result.contains("bob@example.com"));
        assertTrue(result.contains("charlie@example.com"));
    }

    @Test
    void testParseRfc822AddressesWhitespace() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("  alice@example.com  ,   bob@example.com  ");
        assertEquals(2, result.size());
        assertTrue(result.contains("alice@example.com"));
        assertTrue(result.contains("bob@example.com"));
    }

    @Test
    void testParseRfc822AddressesWhitespaceAroundAngleBrackets() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("John Doe <  john@example.com  >");
        assertEquals(1, result.size());
        assertTrue(result.contains("john@example.com"));
    }

    @Test
    void testParseRfc822AddressesIllegalNoAtSign() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("notanemail");
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseRfc822AddressesIllegalDisplayNameNoAtSign() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("John Doe <notanemail>");
        assertTrue(result.contains("notanemail"));
    }

    @Test
    void testParseRfc822AddressesIllegalMixed() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("notanemail, user@example.com");
        assertEquals(1, result.size());
        assertTrue(result.contains("user@example.com"));
    }

    @Test
    void testParseRfc822AddressesDuplicateAddresses() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("alice@example.com, Alice <alice@example.com>");
        assertEquals(1, result.size());
        assertTrue(result.contains("alice@example.com"));
    }

    @Test
    void testParseRfc822AddressesCommaInQuotedName() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("\"Last, First\" <first.last@example.com>, other@example.com");
        assertEquals(2, result.size());
        assertTrue(result.contains("first.last@example.com"));
        assertTrue(result.contains("other@example.com"));
    }

    @Test
    void testParseRfc822AddressesUnclosedAngleBracket() {
        final Set<String> result = EmailUtils.parseRfc822Addresses("John Doe <john@example.com");
        assertTrue(result.isEmpty());
    }
}
