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
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class AbstractIDNEmailAddressConverterTest {

    private static final String AUSTRIAN_IDN_EMAIL_ADDRESS = "noreply@d\u00F6m\u00E4in.example";
    private static final String CZECH_IDN_EMAIL_ADDRESS = "noreply@\u010Desk\u00E1republika.icom.museum";
    private static final String RUSSIAN_IDN_EMAIL_ADDRESS = "noreply@\u0440\u043E\u0441\u0441\u0438\u044F.\u0438\u043A\u043E\u043C.museum";
    private static final String CHINESE_IDN_EMAIL_ADDRESS = "noreply@\u4f8b\u5b50.\u6d4b\u8bd5";

    private static final String[] IDN_EMAIL_ADDRESSES = {
        AUSTRIAN_IDN_EMAIL_ADDRESS,
        CZECH_IDN_EMAIL_ADDRESS,
        RUSSIAN_IDN_EMAIL_ADDRESS,
        CHINESE_IDN_EMAIL_ADDRESS
    };

    private final AbstractIDNEmailAddressConverter converter = new AbstractIDNEmailAddressConverter() {
    };

    @Test
    void testConvertInvalidEmailAddressToAscii() {
        assertNull(converter.toASCII(null));
        assertEquals("", converter.toASCII(""));
        assertEquals("@", converter.toASCII("@"));
        assertEquals("@@", converter.toASCII("@@"));
        assertEquals("foo", converter.toASCII("foo"));
        assertEquals("foo@", converter.toASCII("foo@"));
        assertEquals("@badhost.com", converter.toASCII("@badhost.com"));
    }

    @Test
    void testAustrianIDNEmailAddressToAsciiConversion() {
        assertEquals("noreply@xn--dmin-moa0i.example", converter.toASCII(AUSTRIAN_IDN_EMAIL_ADDRESS));
    }

    @Test
    void testCzechIDNEmailAddressToAsciiConversion() {
        assertEquals("noreply@xn--h1alffa9f.xn--h1aegh.museum", converter.toASCII(CZECH_IDN_EMAIL_ADDRESS));
    }

    @Test
    void testRussianIDNEmailAddressToAsciiConversion() {
        assertEquals("noreply@xn--h1alffa9f.xn--h1aegh.museum", converter.toASCII(RUSSIAN_IDN_EMAIL_ADDRESS));
    }

    @Test
    void testChineseIDNEmailAddressToAsciiConversion() {
        assertEquals("noreply@xn--fsq.xn--0zwm56d", converter.toASCII(CHINESE_IDN_EMAIL_ADDRESS));
    }

    @Test
    void testMultipleIDNEmailAddressToAsciiConversion() {
        assertEquals("noreply@xn--dmin-moa0i.example", converter.toASCII(converter.toASCII(AUSTRIAN_IDN_EMAIL_ADDRESS)));
    }

    @Test
    void testNonIDNEmailAddressToAsciiConversion() {
        assertEquals("me@home.com", converter.toASCII("me@home.com"));
    }

    @Test
    void testRoundTripConversionOfIDNEmailAddress() {
        for (final String email : IDN_EMAIL_ADDRESSES) {
            assertEquals(email, converter.toUnicode(converter.toASCII(email)));
        }
    }

    @Test
    void testToUnicodeWithPunycodeEmail() {
        assertEquals("noreply@d\u00F6m\u00E4in.example", converter.toUnicode("noreply@xn--dmin-moa0i.example"));
    }

    @Test
    void testToUnicodeWithNonPunycodeEmail() {
        assertEquals("me@home.com", converter.toUnicode("me@home.com"));
    }
}
