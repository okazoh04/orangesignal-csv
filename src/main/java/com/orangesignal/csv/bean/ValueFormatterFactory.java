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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.orangesignal.csv.annotation.CsvColumn;

/**
 * {@link ValueFormatter} のインスタンスを生成するファクトリクラスです。
 * 
 * @author Koji Sugisawa
 * @since 3.0.0
 */
public final class ValueFormatterFactory {

	private ValueFormatterFactory() {
	}

	/**
	 * 指定された注釈情報とフィールドの型から、適切な {@link ValueFormatter} を作成して返します。
	 * 
	 * @param column {@link CsvColumn} 注釈
	 * @param type フィールドの型
	 * @param converter デフォルトのコンバータ
	 * @return {@link ValueFormatter} インスタンス。書式指定がない場合は {@code null}
	 */
	public static ValueFormatter createValueFormatter(final CsvColumn column, final Class<?> type, final CsvValueConverter converter) {
		final String pattern = column.format();
		if (pattern.isEmpty()) {
			return new DefaultValueFormatter(converter);
		}

		final Locale locale = column.language().isEmpty() ? Locale.getDefault() : Locale.of(column.language(), column.country());
		final String timezone = column.timezone();
		final String currency = column.currency();

		return createValueFormatter(pattern, type, locale, timezone, currency, converter);
	}

	/**
	 * 指定されたパターン文字列と型から、適切な {@link ValueFormatter} を作成して返します。
	 * 
	 * @param pattern パターン文字列
	 * @param type 型
	 * @param locale ロケール
	 * @param timezone タイムゾーン (Date 型の場合のみ有効、オプション)
	 * @param currency 通貨 (Number 型の場合のみ有効、オプション)
	 * @param converter デフォルトのコンバータ
	 * @return {@link ValueFormatter} インスタンス
	 * @since 3.0.0
	 */
	public static ValueFormatter createValueFormatter(final String pattern, final Class<?> type, final Locale locale, final String timezone, final String currency, final CsvValueConverter converter) {
		if (pattern == null || pattern.isEmpty()) {
			return new DefaultValueFormatter(converter);
		}

		final Locale l = locale != null ? locale : Locale.getDefault();

		// java.time 系
		if (TemporalAccessor.class.isAssignableFrom(type)) {
			return new TemporalValueFormatter(DateTimeFormatter.ofPattern(pattern, l));
		}

		// java.util.Date 系
		if (Date.class.isAssignableFrom(type)) {
			final SimpleDateFormat format = new SimpleDateFormat(pattern, l);
			if (timezone != null && !timezone.isEmpty()) {
				format.setTimeZone(TimeZone.getTimeZone(timezone));
			}
			return new DateValueFormatter(format);
		}

		// 数値系
		if (Number.class.isAssignableFrom(type) || type.isPrimitive()) {
			if (isNumeric(type)) {
				final DecimalFormat format = new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(l));
				if (currency != null && !currency.isEmpty()) {
					format.setCurrency(Currency.getInstance(currency));
				}
				return new NumberValueFormatter(format);
			}
		}

		return new DefaultValueFormatter(converter);
	}

	private static boolean isNumeric(final Class<?> type) {
		return Number.class.isAssignableFrom(type) ||
				type.equals(int.class) || type.equals(long.class) ||
				type.equals(double.class) || type.equals(float.class) ||
				type.equals(short.class) || type.equals(byte.class);
	}

}
