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
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

/**
 * {@link SimpleCsvValueConverter} クラスの Java 8 (java.time) に関する単体テストです。
 */
public class SimpleCsvValueConverterJava8Test {

	@Test
	public void testConvertJavaTime() {
		final SimpleCsvValueConverter c = new SimpleCsvValueConverter();

		// LocalDate
		assertThat(c.convert(null, LocalDate.class), is(nullValue()));
		assertThat(c.convert("2026-03-11", LocalDate.class), is(LocalDate.of(2026, 3, 11)));

		// LocalTime
		assertThat(c.convert(null, LocalTime.class), is(nullValue()));
		assertThat(c.convert("12:34:56", LocalTime.class), is(LocalTime.of(12, 34, 56)));

		// LocalDateTime
		assertThat(c.convert(null, LocalDateTime.class), is(nullValue()));
		assertThat(c.convert("2026-03-11T12:34:56", LocalDateTime.class), is(LocalDateTime.of(2026, 3, 11, 12, 34, 56)));

		// ZonedDateTime
		assertThat(c.convert(null, ZonedDateTime.class), is(nullValue()));
		assertThat(c.convert("2026-03-11T12:34:56+09:00[Asia/Tokyo]", ZonedDateTime.class), is(ZonedDateTime.parse("2026-03-11T12:34:56+09:00[Asia/Tokyo]")));

		// OffsetDateTime
		assertThat(c.convert(null, OffsetDateTime.class), is(nullValue()));
		assertThat(c.convert("2026-03-11T12:34:56+09:00", OffsetDateTime.class), is(OffsetDateTime.parse("2026-03-11T12:34:56+09:00")));

		// OffsetTime
		assertThat(c.convert(null, OffsetTime.class), is(nullValue()));
		assertThat(c.convert("12:34:56+09:00", OffsetTime.class), is(OffsetTime.parse("12:34:56+09:00")));

		// Year
		assertThat(c.convert(null, Year.class), is(nullValue()));
		assertThat(c.convert("2026", Year.class), is(Year.of(2026)));

		// YearMonth
		assertThat(c.convert(null, YearMonth.class), is(nullValue()));
		assertThat(c.convert("2026-03", YearMonth.class), is(YearMonth.of(2026, 3)));
	}

	@Test
	public void testConvertJavaTimeWithFormatter() {
		final SimpleCsvValueConverter c = new SimpleCsvValueConverter();
		c.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

		assertThat(c.convert("2026/03/11", LocalDate.class), is(LocalDate.of(2026, 3, 11)));
		assertThat(c.convert(LocalDate.of(2026, 3, 11)), is("2026/03/11"));
	}

}
