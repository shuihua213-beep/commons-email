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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * Utility methods used by commons-email.
 * <p>
 * These methods are copied from other commons components (commons-lang) to avoid creating a dependency for such a small component.
 * </p>
 * <p>
 * This is a package scoped class, and should not be used directly by users.
 * </p>
 *
 * @since 2.0.0
 */
public final class EmailUtils {

    /**
     * Random object used by random method. This has to be not local to the random method so as to not return the same value in the same millisecond.
     */
    private static final Random RANDOM = new Random();

    /**
     * Radix used in encoding.
     */
    private static final int RADIX = 16;

    /**
     * The escape character used for the URL encoding scheme.
     */
    private static final char ESCAPE_CHAR = '%';

    /**
     * BitSet of RFC 2392 safe URL characters.
     */
    private static final BitSet SAFE_URL = new BitSet(256);

    // Static initializer for safe_uri
    static {
        // alpha characters
        for (int i = 'a'; i <= 'z'; i++) {
            SAFE_URL.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            SAFE_URL.set(i);
        }
        // numeric characters
        for (int i = '0'; i <= '9'; i++) {
            SAFE_URL.set(i);
        }

        // safe chars
        SAFE_URL.set('-');
        SAFE_URL.set('_');
        SAFE_URL.set('.');
        SAFE_URL.set('*');
        SAFE_URL.set('+');
        SAFE_URL.set('$');
        SAFE_URL.set('!');
        SAFE_URL.set('\'');
        SAFE_URL.set('(');
        SAFE_URL.set(')');
        SAFE_URL.set(',');
        SAFE_URL.set('@');
    }

    /**
     * Checks that the given file exists and is not a directory.
     *
     * @param file the file to check.
     * @return the given file if it exists and is not a directory.
     * @throws IOException if the given file does not exist or is a directory.
     * @since 2.0.0-M2
     */
    public static File check(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        if (!file.exists()) {
            throw new IOException("\"" + file + "\" does not exist");
        }
        if (file.isDirectory()) {
            throw new IOException("\"" + file + "\" is a directory");
        }
        return file;
    }

    /**
     * Checks that the given file exists and is not a directory.
     *
     * @param file the file to check.
     * @return the given file if it exists and is not a directory.
     * @throws IOException if the given file does not exist or is a directory.
     * @since 2.0.0-M2
     */
    public static Path check(final Path file) throws IOException {
        Objects.requireNonNull(file, "file");
        if (!Files.exists(file)) {
            throw new IOException("\"" + file + "\" does not exist");
        }
        if (Files.isDirectory(file)) {
            throw new IOException("\"" + file + "\" is a directory");
        }
        return file;
    }

    /**
     * Encodes an input string according to RFC 2392. Unsafe characters are escaped.
     *
     * @param input the input string to be URL encoded
     * @return a URL encoded string
     * @see <a href="https://tools.ietf.org/html/rfc2392">RFC 2392</a>
     */
    public static String encodeUrl(final String input) {
        if (input == null) {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        for (final byte c : input.getBytes(StandardCharsets.US_ASCII)) {
            final int b = Byte.toUnsignedInt(c);
            if (SAFE_URL.get(b)) {
                builder.append((char) b);
            } else {
                builder.append(ESCAPE_CHAR);
                final char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, RADIX));
                final char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, RADIX));
                builder.append(hex1);
                builder.append(hex2);
            }
        }
        return builder.toString();
    }

    /**
     * Tests whether the given collection is null or empty.
     *
     * @param collection the collection to test.
     * @return whether the given collection is null or empty.
     */
    public static boolean isEmpty(final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Tests whether the given map is null or empty.
     *
     * @param map the map to test.
     * @return whether the given map is null or empty.
     */
    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Tests whether the given array is null or empty.
     *
     * @param array the collection to test.
     * @return whether the given array is null or empty.
     */
    public static boolean isEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if a String is empty ("") or null.
     * <p>
     * Copied from Commons Lang 2.1, svn 240418
     * </p>
     *
     * @param str the String to check, may be null
     * @return {@code true} if the String is empty or null
     */
    public static boolean isEmpty(final String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Checks if a String is not empty ("") and not null.
     * <p>
     * Copied from Commons Lang 2.1, svn 240418
     * </p>
     *
     * @param str the String to check, may be null
     * @return {@code true} if the String is not empty and not null
     */
    public static boolean isNotEmpty(final String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * Creates a random string based on a variety of options, using supplied source of randomness.
     * <p>
     * If start and end are both {@code 0}, start and end are set to {@code ' '} and {@code 'z'}, the ASCII printable characters, will be used, unless letters
     * and numbers are both {@code false}, in which case, start and end are set to {@code 0} and {@code Integer.MAX_VALUE}.
     * </p>
     * <p>
     * If set is not {@code null}, characters between start and end are chosen.
     * </p>
     * <p>
     * This method accepts a user-supplied {@link Random} instance to use as a source of randomness. By seeding a single {@link Random} instance with a fixed
     * seed and using it for each call, the same random sequence of strings can be generated repeatedly and predictably.
     * </p>
     * <p>
     * Copied from Commons Lang 2.1, svn 201930
     * </p>
     *
     * @param count   the length of random string to create
     * @param start   the position in set of chars to start at
     * @param end     the position in set of chars to end before
     * @param letters only allow letters?
     * @param numbers only allow numbers?
     * @param chars   the set of chars to choose randoms from. If {@code null}, then it will use the set of all chars.
     * @param random  a source of randomness.
     * @return the random string
     * @throws IllegalArgumentException if {@code count} &lt; 0.
     */
    private static String random(int count, int start, int end, final boolean letters, final boolean numbers, final char[] chars, final Random random) {
        if (count == 0) {
            return "";
        }
        if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }

        if (start == 0 && end == 0) {
            end = 'z' + 1;
            start = ' ';

            if (!letters && !numbers) {
                start = 0;
                end = Integer.MAX_VALUE;
            }
        }

        final StringBuilder buffer = new StringBuilder();
        final int gap = end - start;

        while (count-- != 0) {
            char ch;

            if (chars == null) {
                ch = (char) (random.nextInt(gap) + start);
            } else {
                ch = chars[random.nextInt(gap) + start];
            }

            if (letters && numbers && Character.isLetterOrDigit(ch) || letters && Character.isLetter(ch) || numbers && Character.isDigit(ch)
                    || !letters && !numbers) {
                buffer.append(ch);
            } else {
                count++;
            }
        }

        return buffer.toString();
    }

    /**
     * Creates a random string whose length is the number of characters specified.
     * <p>
     * Characters will be chosen from the set of alphabetic characters.
     * </p>
     * <p>
     * Copied from Commons Lang 2.1, svn 201930
     * </p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomAlphabetic(final int count) {
        return random(count, 0, 0, true, false, null, RANDOM);
    }

    /**
     * Replaces end-of-line characters with spaces.
     *
     * @param input the input string to be scanned.
     * @return a clean string
     */
    public static String replaceEndOfLineCharactersWithSpaces(final String input) {
        return input == null ? null : input.replace('\n', ' ').replace('\r', ' ');
    }

    /**
     * Converts the given string to lower case.
     *
     * @param value the input string.
     * @return a lower case string.
     */
    public static String toLower(final String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    /**
     * Parses an RFC822 address list and returns the set of distinct email addresses.
     * <p>
     * Handles common RFC822 address formats including:
     * </p>
     * <ul>
     *   <li>Plain address: {@code user@domain.com}</li>
     *   <li>Address with display name: {@code John Doe <john@example.com>}</li>
     *   <li>Quoted display name: {@code "Doe, John" <john@example.com>}</li>
     *   <li>Multiple addresses separated by commas</li>
     * </ul>
     * <p>
     * Invalid or malformed addresses are silently ignored. All returned addresses are normalized to lower case.
     * </p>
     *
     * @param addressList the RFC822 address list to parse, may be null or empty
     * @return a set of distinct, normalized email addresses, never null
     * @since 2.0.0
     */
    public static Set<String> parseAddressList(final String addressList) {
        if (isEmpty(addressList)) {
            return Collections.emptySet();
        }
        final Set<String> result = new LinkedHashSet<>();
        final List<String> parts = splitAddressList(addressList);
        for (final String part : parts) {
            final String address = extractAddress(part.trim());
            if (isValidEmail(address)) {
                result.add(address.toLowerCase(Locale.ROOT));
            }
        }
        return result;
    }

    /**
     * Splits an RFC822 address list by commas, respecting quoted strings so that commas
     * inside quotes are not treated as delimiters.
     *
     * @param addressList the raw address list string.
     * @return a list of individual address parts.
     */
    private static List<String> splitAddressList(final String addressList) {
        final List<String> parts = new ArrayList<>();
        final int length = addressList.length();
        boolean inQuotes = false;
        int start = 0;
        for (int i = 0; i < length; i++) {
            final char c = addressList.charAt(i);
            if (c == '"') {
                if (!inQuotes || i + 1 >= length || addressList.charAt(i + 1) != '"') {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                final String part = addressList.substring(start, i).trim();
                if (!part.isEmpty()) {
                    parts.add(part);
                }
                start = i + 1;
            }
        }
        final String lastPart = addressList.substring(start).trim();
        if (!lastPart.isEmpty()) {
            parts.add(lastPart);
        }
        return parts;
    }

    /**
     * Extracts the email address from an address part. If the part contains angle brackets,
     * the content between the leftmost {@code <} and rightmost {@code >} is returned.
     * Otherwise, the trimmed part itself is returned.
     *
     * @param part a single address part from the address list.
     * @return the extracted email address string.
     */
    private static String extractAddress(final String part) {
        final int ltIndex = part.indexOf('<');
        final int gtIndex = part.lastIndexOf('>');
        if (ltIndex >= 0 && gtIndex > ltIndex) {
            return part.substring(ltIndex + 1, gtIndex).trim();
        }
        return part;
    }

    /**
     * Performs a basic validation that the string looks like an email address.
     * <p>
     * A valid email address must contain exactly one {@code @}, have non-empty local-part and domain,
     * and contain no whitespace.
     * </p>
     *
     * @param address the address string to validate.
     * @return {@code true} if the address passes basic validation.
     */
    private static boolean isValidEmail(final String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        final int atIndex = address.indexOf('@');
        if (atIndex <= 0 || atIndex != address.lastIndexOf('@')) {
            return false;
        }
        if (address.indexOf(' ') >= 0 || address.indexOf('\t') >= 0) {
            return false;
        }
        final String localPart = address.substring(0, atIndex);
        final String domain = address.substring(atIndex + 1);
        return !localPart.isEmpty() && !domain.isEmpty();
    }

    /**
     * Constructs a new {@code EmailException} with no detail message.
     */
    private EmailUtils() {
    }
}
