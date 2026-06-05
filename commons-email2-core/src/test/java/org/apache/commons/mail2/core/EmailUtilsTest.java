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
    void testToAsciiEmail() {
        assertNull(EmailUtils.toAsciiEmail(null));
        assertEquals("", EmailUtils.toAsciiEmail(""));
        assertEquals("@", EmailUtils.toAsciiEmail("@"));
        assertEquals("@@", EmailUtils.toAsciiEmail("@@"));
        assertEquals("foo", EmailUtils.toAsciiEmail("foo"));
        assertEquals("foo@", EmailUtils.toAsciiEmail("foo@"));
        assertEquals("@badhost.com", EmailUtils.toAsciiEmail("@badhost.com"));
        assertEquals("me@home.com", EmailUtils.toAsciiEmail("me@home.com"));
        
        final String austrianIdn = "noreply@d\u00F6m\u00E4in.example";
        final String russianIdn = "noreply@\u0440\u043E\u0441\u0441\u0438\u044F.\u0438\u043A\u043E\u043C.museum";
        
        assertEquals("noreply@xn--dmin-moa0i.example", EmailUtils.toAsciiEmail(austrianIdn));
        assertEquals("noreply@xn--h1alffa9f.xn--h1aegh.museum", EmailUtils.toAsciiEmail(russianIdn));
    }

    @Test
    void testToUnicodeEmail() {
        assertNull(EmailUtils.toUnicodeEmail(null));
        assertEquals("", EmailUtils.toUnicodeEmail(""));
        assertEquals("@", EmailUtils.toUnicodeEmail("@"));
        assertEquals("@@", EmailUtils.toUnicodeEmail("@@"));
        assertEquals("foo", EmailUtils.toUnicodeEmail("foo"));
        assertEquals("foo@", EmailUtils.toUnicodeEmail("foo@"));
        assertEquals("@badhost.com", EmailUtils.toUnicodeEmail("@badhost.com"));
        assertEquals("me@home.com", EmailUtils.toUnicodeEmail("me@home.com"));
        
        final String austrianIdn = "noreply@d\u00F6m\u00E4in.example";
        final String czechIdn = "noreply@\u010Desk\u00E1republika.icom.museum";
        final String russianIdn = "noreply@\u0440\u043E\u0441\u0441\u0438\u044F.\u0438\u043A\u043E\u043C.museum";
        
        assertEquals(austrianIdn, EmailUtils.toUnicodeEmail(EmailUtils.toAsciiEmail(austrianIdn)));
        assertEquals(czechIdn, EmailUtils.toUnicodeEmail(EmailUtils.toAsciiEmail(czechIdn)));
        assertEquals(russianIdn, EmailUtils.toUnicodeEmail(EmailUtils.toAsciiEmail(russianIdn)));
    }
}
