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

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.HashMap;
import java.util.Map;

/**
 * Java 8 日時 API の型を扱う {@link ValueFormatter} の実装クラスです。
 * 
 * @author Koji Sugisawa
 * @since 3.0.0
 */
public class TemporalValueFormatter implements ValueFormatter {

	private final DateTimeFormatter dateTimeFormatter;
	private static final Map<Class<?>, TemporalQuery<?>> QUERIES = new HashMap<>();

	static {
		QUERIES.put(java.time.LocalDate.class, java.time.LocalDate::from);
		QUERIES.put(java.time.LocalTime.class, java.time.LocalTime::from);
		QUERIES.put(java.time.LocalDateTime.class, java.time.LocalDateTime::from);
		QUERIES.put(java.time.ZonedDateTime.class, java.time.ZonedDateTime::from);
		QUERIES.put(java.time.OffsetDateTime.class, java.time.OffsetDateTime::from);
		QUERIES.put(java.time.OffsetTime.class, java.time.OffsetTime::from);
		QUERIES.put(java.time.Year.class, java.time.Year::from);
		QUERIES.put(java.time.YearMonth.class, java.time.YearMonth::from);
		QUERIES.put(java.time.MonthDay.class, java.time.MonthDay::from);
		QUERIES.put(java.time.Instant.class, java.time.Instant::from);
	}

	public TemporalValueFormatter(final DateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;
	}

	@Override
	public String format(final Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof TemporalAccessor) {
			return dateTimeFormatter.format((TemporalAccessor) value);
		}
		return value.toString();
	}

	@Override
	public Object parse(final String str, final Class<?> type) {
		if (str == null || str.isEmpty()) {
			return null;
		}
		final TemporalQuery<?> query = QUERIES.get(type);
		if (query != null) {
			return dateTimeFormatter.parse(str, query);
		}
		throw new IllegalArgumentException(String.format("Unsupported type %s", type.getName()));
	}

}
