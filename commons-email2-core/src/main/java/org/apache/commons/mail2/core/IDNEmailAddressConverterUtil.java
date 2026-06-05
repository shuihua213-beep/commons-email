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

import java.net.IDN;
import java.util.function.Function;

public final class IDNEmailAddressConverterUtil {

    private IDNEmailAddressConverterUtil() {
    }

    public static String toASCII(final String email) {
        return toString(email, IDN::toASCII);
    }

    public static String toUnicode(final String email) {
        return toString(email, IDN::toUnicode);
    }

    private static String getDomainPart(final String email, final int idx) {
        return email.substring(idx + 1);
    }

    private static String getLocalPart(final String email, final int idx) {
        return email.substring(0, idx);
    }

    private static String toString(final String email, final Function<String, String> converter) {
        final int idx = email == null ? -1 : email.indexOf('@');
        if (idx < 0) {
            return email;
        }
        return getLocalPart(email, idx) + '@' + converter.apply(getDomainPart(email, idx));
    }
}
