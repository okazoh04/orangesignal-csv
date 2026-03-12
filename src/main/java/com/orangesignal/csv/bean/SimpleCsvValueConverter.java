/*
 * Copyright 2013 the original author or authors.
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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * {@link CsvValueConverter} を実装したデフォルトのシンプルな実装クラスを提供します。
 *
 * @author Koji Sugisawa
 */
public class SimpleCsvValueConverter implements CsvValueConverter {

	/**
	 * 型別の変換を行うインターフェースです。
	 * 
	 * @since 3.0.0
	 */
	public interface TypeConverter {
		/**
		 * 指定された型をこのコンバータが扱えるかどうかを返します。
		 * 
		 * @param type 型
		 * @return 扱える場合は {@code true}
		 */
		boolean canConvert(Class<?> type);

		/**
		 * 文字列からオブジェクトへ変換します。
		 * 
		 * @param str 文字列
		 * @param type 変換する型
		 * @param context コンテキスト（dateFormat 等へのアクセス用）
		 * @return 変換されたオブジェクト
		 * @throws Exception 変換に失敗した場合
		 */
		Object parse(String str, Class<?> type, SimpleCsvValueConverter context) throws Exception;

		/**
		 * オブジェクトから文字列へ変換します。
		 * 
		 * @param value オブジェクト
		 * @param context コンテキスト（dateFormat 等へのアクセス用）
		 * @return 変換された文字列
		 */
		default String format(Object value, SimpleCsvValueConverter context) {
			return value.toString();
		}
	}

	/**
	 * プリミティブ型とプリミティブ型デフォルト値のマップです。
	 */
	private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS;

	/**
	 * 論理型として扱う文字列と値のマップです。
	 */
	private static final Map<String, Boolean> BOOLEAN_DEFAULTS;

	/**
	 * デフォルトで登録される型別コンバータのリストです。
	 */
	private static final List<TypeConverter> DEFAULT_CONVERTERS;

	static {
		final Map<Class<?>, Object> primitiveMap = new HashMap<Class<?>, Object>();
		primitiveMap.put(Boolean.TYPE, Boolean.FALSE);
		primitiveMap.put(Byte.TYPE, Byte.valueOf((byte) 0));
		primitiveMap.put(Character.TYPE, Character.valueOf('\u0000'));
		primitiveMap.put(Short.TYPE, Short.valueOf((short) 0));
		primitiveMap.put(Integer.TYPE, Integer.valueOf(0));
		primitiveMap.put(Long.TYPE, Long.valueOf(0L));
		primitiveMap.put(Float.TYPE, Float.valueOf(0F));
		primitiveMap.put(Double.TYPE, Double.valueOf(0D));
		PRIMITIVE_DEFAULTS = Collections.unmodifiableMap(primitiveMap);

		final Map<String, Boolean> booleanMap = new HashMap<String, Boolean>();
		putBooleanMap(booleanMap, "0", "1");
		putBooleanMap(booleanMap, "false", "true");
		putBooleanMap(booleanMap, "f", "t");
		putBooleanMap(booleanMap, "no", "yes");
		putBooleanMap(booleanMap, "n", "y");
		putBooleanMap(booleanMap, "off", "on");
		putBooleanMap(booleanMap, "x", "o");
		BOOLEAN_DEFAULTS = Collections.unmodifiableMap(booleanMap);

		final List<TypeConverter> converters = new ArrayList<>();

		// 論理型の変換登録
		converters.add(new SimpleTypeConverter(type -> type.equals(Boolean.TYPE) || type.equals(Boolean.class), (str, type, ctx) -> {
			for (final Map.Entry<String, Boolean> entry : BOOLEAN_DEFAULTS.entrySet()) {
				if (entry.getKey().equalsIgnoreCase(str)) {
					return entry.getValue();
				}
			}
			return null;
		}));

		// 数値型の変換登録
		addExact(converters, Byte.TYPE, (str, type, ctx) -> Byte.valueOf(str));
		addExact(converters, Byte.class, (str, type, ctx) -> Byte.valueOf(str));
		addExact(converters, Short.TYPE, (str, type, ctx) -> Short.valueOf(str));
		addExact(converters, Short.class, (str, type, ctx) -> Short.valueOf(str));
		addExact(converters, Integer.TYPE, (str, type, ctx) -> Integer.valueOf(str));
		addExact(converters, Integer.class, (str, type, ctx) -> Integer.valueOf(str));
		addExact(converters, Long.TYPE, (str, type, ctx) -> Long.valueOf(str));
		addExact(converters, Long.class, (str, type, ctx) -> Long.valueOf(str));
		addExact(converters, Float.TYPE, (str, type, ctx) -> Float.valueOf(str));
		addExact(converters, Float.class, (str, type, ctx) -> Float.valueOf(str));
		addExact(converters, Double.TYPE, (str, type, ctx) -> Double.valueOf(str));
		addExact(converters, Double.class, (str, type, ctx) -> Double.valueOf(str));
		addExact(converters, BigInteger.class, (str, type, ctx) -> new BigInteger(str));
		addExact(converters, BigDecimal.class, (str, type, ctx) -> new BigDecimal(str));
		addExact(converters, Number.class, (str, type, ctx) -> new BigDecimal(str));

		// 日付型の変換登録（java.util.Date のサブクラスも考慮）
		converters.add(new TypeConverter() {
			@Override public boolean canConvert(Class<?> type) { return Date.class.isAssignableFrom(type); }
			@Override public Object parse(String str, Class<?> type, SimpleCsvValueConverter context) throws Exception {
				if (context.dateFormat != null) {
					return type.getConstructor(Long.TYPE).newInstance(context.dateFormat.parse(str).getTime());
				}
				throw new IllegalArgumentException(String.format("Unknown convert type %s", type.getName()));
			}
			@Override public String format(Object value, SimpleCsvValueConverter context) {
				if (context.dateFormat != null) {
					return context.dateFormat.format(value);
				}
				return value.toString();
			}
		});

		// Java 8 日時 API の変換登録
		addTemporal(converters, LocalDate.class, (str, fmt) -> fmt != null ? LocalDate.parse(str, fmt) : LocalDate.parse(str));
		addTemporal(converters, LocalTime.class, (str, fmt) -> fmt != null ? LocalTime.parse(str, fmt) : LocalTime.parse(str));
		addTemporal(converters, LocalDateTime.class, (str, fmt) -> fmt != null ? LocalDateTime.parse(str, fmt) : LocalDateTime.parse(str));
		addTemporal(converters, ZonedDateTime.class, (str, fmt) -> fmt != null ? ZonedDateTime.parse(str, fmt) : ZonedDateTime.parse(str));
		addTemporal(converters, OffsetDateTime.class, (str, fmt) -> fmt != null ? OffsetDateTime.parse(str, fmt) : OffsetDateTime.parse(str));
		addTemporal(converters, OffsetTime.class, (str, fmt) -> fmt != null ? OffsetTime.parse(str, fmt) : OffsetTime.parse(str));
		addTemporal(converters, Year.class, (str, fmt) -> fmt != null ? Year.parse(str, fmt) : Year.parse(str));
		addTemporal(converters, YearMonth.class, (str, fmt) -> fmt != null ? YearMonth.parse(str, fmt) : YearMonth.parse(str));
		addTemporal(converters, MonthDay.class, (str, fmt) -> fmt != null ? MonthDay.parse(str, fmt) : MonthDay.parse(str));
		addExact(converters, Instant.class, (str, type, ctx) -> Instant.parse(str));
		addExact(converters, ZoneId.class, (str, type, ctx) -> ZoneId.of(str));
		addExact(converters, ZoneOffset.class, (str, type, ctx) -> ZoneOffset.of(str));

		// その他の標準型の変換登録
		addExact(converters, UUID.class, (str, type, ctx) -> UUID.fromString(str));
		addExact(converters, Currency.class, (str, type, ctx) -> Currency.getInstance(str));
		addExact(converters, Locale.class, (str, type, ctx) -> Locale.forLanguageTag(str), (val, ctx) -> ((Locale) val).toLanguageTag());
		addExact(converters, URI.class, (str, type, ctx) -> URI.create(str));
		// InetAddress はサブクラス（Inet4Address 等）を考慮して isAssignableFrom で判定
		converters.add(new SimpleTypeConverter(t -> InetAddress.class.isAssignableFrom(t), (str, type, ctx) -> InetAddress.getByName(str), (val, ctx) -> ((InetAddress) val).getHostAddress()));
		addExact(converters, byte[].class, (str, type, ctx) -> HexFormat.of().parseHex(str), (val, ctx) -> HexFormat.of().formatHex((byte[]) val));

		// Enum の変換登録
		converters.add(new TypeConverter() {
			@Override public boolean canConvert(Class<?> type) { return Enum.class.isAssignableFrom(type); }
			@Override public Object parse(String str, Class<?> type, SimpleCsvValueConverter context) throws Exception {
				try {
					return type.getMethod("valueOf", String.class).invoke(null, str);
				} catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					throw new IllegalArgumentException(String.format("Unknown convert type %s", type.getName()), e);
				}
			}
		});

		DEFAULT_CONVERTERS = Collections.unmodifiableList(converters);
	}

	private static void putBooleanMap(final Map<String, Boolean> map, final String falseValue, final String trueValue) {
		map.put(falseValue, Boolean.FALSE);
		map.put(trueValue, Boolean.TRUE);
	}

	/**
	 * 型に完全一致するパーサーを登録するヘルパーメソッドです。
	 */
	private static void addExact(List<TypeConverter> converters, Class<?> type, Parser parser) {
		converters.add(new SimpleTypeConverter(t -> t.equals(type), parser));
	}

	/**
	 * 型に完全一致するパーサーとフォーマッタを登録するヘルパーメソッドです。
	 */
	private static void addExact(List<TypeConverter> converters, Class<?> type, Parser parser, Formatter formatter) {
		converters.add(new SimpleTypeConverter(t -> t.equals(type), parser, formatter));
	}

	/**
	 * Java 8 日時 API 用の共通変換ロジックを登録するヘルパーメソッドです。
	 */
	private static void addTemporal(List<TypeConverter> converters, Class<?> type, BiFunction<String, DateTimeFormatter, Object> parser) {
		converters.add(new TypeConverter() {
			@Override public boolean canConvert(Class<?> t) { return t.equals(type); }
			@Override public Object parse(String str, Class<?> t, SimpleCsvValueConverter ctx) { return parser.apply(str, ctx.dateTimeFormatter); }
			@Override public String format(Object value, SimpleCsvValueConverter ctx) {
				if (ctx.dateTimeFormatter != null && value instanceof TemporalAccessor) {
					return ctx.dateTimeFormatter.format((TemporalAccessor) value);
				}
				return value.toString();
			}
		});
	}

	@FunctionalInterface private interface Parser { Object parse(String str, Class<?> type, SimpleCsvValueConverter context) throws Exception; }
	@FunctionalInterface private interface Formatter { String format(Object value, SimpleCsvValueConverter context); }

	/**
	 * 基本的な型別コンバータの実装クラスです。
	 */
	private static class SimpleTypeConverter implements TypeConverter {
		private final java.util.function.Predicate<Class<?>> predicate;
		private final Parser parser;
		private final Formatter formatter;
		SimpleTypeConverter(java.util.function.Predicate<Class<?>> predicate, Parser parser) { this(predicate, parser, (v, ctx) -> v.toString()); }
		SimpleTypeConverter(java.util.function.Predicate<Class<?>> predicate, Parser parser, Formatter formatter) {
			this.predicate = predicate;
			this.parser = parser;
			this.formatter = formatter;
		}
		@Override public boolean canConvert(Class<?> type) { return predicate.test(type); }
		@Override public Object parse(String str, Class<?> type, SimpleCsvValueConverter context) throws Exception { return parser.parse(str, type, context); }
		@Override public String format(Object value, SimpleCsvValueConverter context) { return formatter.format(value, context); }
	}

	/**
	 * 日時書式を保持します。
	 */
	private DateFormat dateFormat;

	/**
	 * 日時書式 (Java 8) を保持します。
	 */
	private DateTimeFormatter dateTimeFormatter;

	/**
	 * 外部から追加された型別のコンバータを保持します。
	 */
	private final List<TypeConverter> customConverters = new java.util.concurrent.CopyOnWriteArrayList<>();

	/**
	 * デフォルトコンストラクタです。
	 */
	public SimpleCsvValueConverter() {
	}

	/**
	 * 日時書式を返します。
	 * 
	 * @return 日時書式
	 */
	public DateFormat getDateFormat() { return dateFormat; }

	/**
	 * 日時書式を設定します。
	 * 
	 * @param dateFormat 日時書式
	 */
	public void setDateFormat(final DateFormat dateFormat) { this.dateFormat = dateFormat; }

	/**
	 * 日時書式 (Java 8) を返します。
	 * 
	 * @return 日時書式 (Java 8)
	 * @since 2.2.2
	 */
	public DateTimeFormatter getDateTimeFormatter() { return dateTimeFormatter; }

	/**
	 * 日時書式 (Java 8) を設定します。
	 * 
	 * @param dateTimeFormatter 日時書式 (Java 8)
	 * @since 2.2.2
	 */
	public void setDateTimeFormatter(final DateTimeFormatter dateTimeFormatter) { this.dateTimeFormatter = dateTimeFormatter; }

	/**
	 * 型別のコンバータを追加します。
	 * ここで追加されたコンバータは、デフォルトのコンバータよりも優先して使用されます。
	 * 
	 * @param converter 型別のコンバータ
	 * @since 3.0.0
	 */
	public void addTypeConverter(final TypeConverter converter) {
		this.customConverters.add(0, converter);
	}

	@Override
	public Object convert(final String str, final Class<?> type) {
		// 入力パラメータを検証します。
		if (type == null) {
			throw new IllegalArgumentException("Class must not be null");
		}

		if (type.equals(String.class)) {
			return str;
		}

		// null または空文字の場合のデフォルト値処理
		if (str == null || str.isEmpty()) {
			if (type.isPrimitive()) {
				return PRIMITIVE_DEFAULTS.get(type);
			}
			return null;
		}

		// カスタムコンバータによる変換を試行します。
		for (final TypeConverter converter : customConverters) {
			if (converter.canConvert(type)) {
				try {
					return converter.parse(str, type, this);
				} catch (final Exception e) {
					throw new IllegalArgumentException(e.getMessage(), e);
				}
			}
		}

		// デフォルトコンバータによる変換を試行します。
		for (final TypeConverter converter : DEFAULT_CONVERTERS) {
			if (converter.canConvert(type)) {
				try {
					final Object result = converter.parse(str, type, this);
					if (result != null) {
						return result;
					}
				} catch (final Exception e) {
					throw new IllegalArgumentException(e.getMessage(), e);
				}
			}
		}

		throw new IllegalArgumentException(String.format("Unknown convert type %s", type.getName()));
	}

	@Override
	public String convert(final Object value) {
		if (value == null) {
			return null;
		}

		final Class<?> type = value.getClass();
		// カスタムコンバータによるフォーマットを試行します。
		for (final TypeConverter converter : customConverters) {
			if (converter.canConvert(type)) {
				return converter.format(value, this);
			}
		}

		// デフォルトコンバータによるフォーマットを試行します。
		for (final TypeConverter converter : DEFAULT_CONVERTERS) {
			if (converter.canConvert(type)) {
				return converter.format(value, this);
			}
		}

		// 対応するコンバータがない場合は toString() を返します。
		return value.toString();
	}

}
