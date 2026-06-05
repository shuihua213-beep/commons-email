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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
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
    void testParseRfc822AddressList() {
        // null or empty
        assertEquals(Collections.emptyList(), EmailUtils.parseRfc822AddressList(null));
        assertEquals(Collections.emptyList(), EmailUtils.parseRfc822AddressList(""));
        assertEquals(Collections.emptyList(), EmailUtils.parseRfc822AddressList("   "));

        // single recipient
        assertEquals(Collections.singletonList("a@b.com"), EmailUtils.parseRfc822AddressList("a@b.com"));
        assertEquals(Collections.singletonList("a@b.com"), EmailUtils.parseRfc822AddressList("<a@b.com>"));
        assertEquals(Collections.singletonList("a@b.com"), EmailUtils.parseRfc822AddressList("John Doe <a@b.com>"));

        // names with quotes
        assertEquals(Collections.singletonList("a@b.com"), EmailUtils.parseRfc822AddressList("\"Doe, John\" <a@b.com>"));
        assertEquals(Arrays.asList("a@b.com", "c@d.com"), EmailUtils.parseRfc822AddressList("\"Doe, John\" <a@b.com>, \"Smith, Jane\" <c@d.com>"));

        // multiple recipients
        assertEquals(Arrays.asList("a@b.com", "c@d.com", "e@f.com"), 
                EmailUtils.parseRfc822AddressList("a@b.com, c@d.com, e@f.com"));
        assertEquals(Arrays.asList("a@b.com", "c@d.com"), 
                EmailUtils.parseRfc822AddressList("John Doe <a@b.com>, Jane Doe <c@d.com>"));

        // whitespace characters
        assertEquals(Arrays.asList("a@b.com", "c@d.com"), 
                EmailUtils.parseRfc822AddressList("  a@b.com  ,   c@d.com  "));
        assertEquals(Arrays.asList("a@b.com", "c@d.com"), 
                EmailUtils.parseRfc822AddressList("  \" Doe , John \" <a@b.com>  ,   c@d.com  "));

        // trailing comma (ignored or empty parts skipped)
        assertEquals(Collections.singletonList("a@b.com"), EmailUtils.parseRfc822AddressList("a@b.com, "));
    }

    @Test
    void testParseRfc822AddressListIllegalScenarios() {
        assertThrows(IllegalArgumentException.class, () -> EmailUtils.parseRfc822AddressList("invalid format"));
        assertThrows(IllegalArgumentException.class, () -> EmailUtils.parseRfc822AddressList("a@b.com, <invalid format>"));
        assertThrows(IllegalArgumentException.class, () -> EmailUtils.parseRfc822AddressList("John Doe <a@b.com> extra"));
        assertThrows(IllegalArgumentException.class, () -> EmailUtils.parseRfc822AddressList("John Doe a@b.com"));
        assertThrows(IllegalArgumentException.class, () -> EmailUtils.parseRfc822AddressList("<a@b.com"));
        assertThrows(IllegalArgumentException.class, () -> EmailUtils.parseRfc822AddressList("a@b.com>"));
        assertThrows(IllegalArgumentException.class, () -> EmailUtils.parseRfc822AddressList("<a@b.com> <c@d.com>"));
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
}
