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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * {@link Date} 型の値を扱う {@link ValueFormatter} の実装クラスです。
 * 
 * @author Koji Sugisawa
 * @since 3.0.0
 */
public class DateValueFormatter implements ValueFormatter {

	private final List<DateFormat> dateFormats;

	public DateValueFormatter(final DateFormat dateFormat) {
		this.dateFormats = new java.util.ArrayList<DateFormat>();
		this.dateFormats.add(dateFormat);
	}

	public DateValueFormatter(final List<DateFormat> dateFormats) {
		this.dateFormats = dateFormats;
	}

	@Override
	public String format(final Object value) {
		if (value == null) {
			return null;
		}
		// 書き出しには最初のフォーマットを使用します。
		return dateFormats.get(0).format(value);
	}

	@Override
	public Object parse(final String str, final Class<?> type) {
		if (str == null || str.isEmpty()) {
			return null;
		}
		for (final DateFormat df : dateFormats) {
			try {
				return parse(df, str, type);
			} catch (final ParseException e) {
				// 次のフォーマットを試すか、スペースを入れて再試行します。
				if (str.length() > 10 && str.charAt(10) != ' ') {
					try {
						return parse(df, str.substring(0, 10) + " " + str.substring(10), type);
					} catch (final Exception e2) {
						// 無視して次のフォーマットへ
					}
				}
			} catch (final Exception e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}
		throw new IllegalArgumentException(String.format("Unparseable date: \"%s\"", str));
	}

	private Object parse(final DateFormat df, final String str, final Class<?> type) throws Exception {
		final java.text.ParsePosition pos = new java.text.ParsePosition(0);
		final Date date = df.parse(str, pos);
		if (date == null || pos.getIndex() < str.length()) {
			throw new ParseException(String.format("Unparseable date: \"%s\"", str), pos.getErrorIndex());
		}
		if (type.equals(Date.class)) {
			return date;
		}
		// java.sql.Date, java.sql.Timestamp 等への対応
		return type.getConstructor(long.class).newInstance(date.getTime());
	}

}
