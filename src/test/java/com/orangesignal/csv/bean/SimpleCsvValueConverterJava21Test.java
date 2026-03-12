/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.orangesignal.csv.bean;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.InetAddress;
import java.net.URI;
import java.time.Instant;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * {@link SimpleCsvValueConverter} クラスの Java 21 (およびモダン Java) に関する単体テストです。
 */
class SimpleCsvValueConverterJava21Test {

	@Test
	void testConvertModernTypes() throws Exception {
		final SimpleCsvValueConverter c = new SimpleCsvValueConverter();

		// UUID
		final UUID uuid = UUID.randomUUID();
		assertThat(c.convert(uuid.toString(), UUID.class), is(uuid));
		assertThat(c.convert(uuid), is(uuid.toString()));

		// Currency
		assertThat(c.convert("JPY", Currency.class), is(Currency.getInstance("JPY")));
		assertThat(c.convert(Currency.getInstance("USD")), is("USD"));

		// Locale
		assertThat(c.convert("ja-JP", Locale.class), is(Locale.JAPAN));
		assertThat(c.convert(Locale.US), is("en-US"));

		// URI
		assertThat(c.convert("https://github.com/orangesignal", URI.class), is(URI.create("https://github.com/orangesignal")));
		assertThat(c.convert(URI.create("mailto:info@example.com")), is("mailto:info@example.com"));

		// InetAddress
		assertThat(c.convert("127.0.0.1", InetAddress.class), is(InetAddress.getByName("127.0.0.1")));
		assertThat(c.convert(InetAddress.getByName("8.8.8.8")), is("8.8.8.8"));

		// Instant
		final Instant instant = Instant.parse("2026-03-11T12:34:56Z");
		assertThat(c.convert("2026-03-11T12:34:56Z", Instant.class), is(instant));
		assertThat(c.convert(instant), is("2026-03-11T12:34:56Z"));

		// MonthDay
		assertThat(c.convert("--10-27", MonthDay.class), is(MonthDay.of(10, 27)));
		assertThat(c.convert(MonthDay.of(3, 11)), is("--03-11"));

		// ZoneId / ZoneOffset
		assertThat(c.convert("Asia/Tokyo", ZoneId.class), is(ZoneId.of("Asia/Tokyo")));
		assertThat(c.convert(ZoneId.of("UTC")), is("UTC"));
		assertThat(c.convert("+09:00", ZoneOffset.class), is(ZoneOffset.of("+09:00")));

		// byte[] (HexFormat)
		final byte[] bytes = new byte[] { 0x01, 0x02, 0x0A, 0x0F, (byte) 0xFF };
		assertThat(c.convert("01020a0fff", byte[].class), is(bytes));
		assertThat(c.convert(bytes), is("01020a0fff"));
	}

}
