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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 数値型を扱う {@link ValueFormatter} の実装クラスです。
 * 
 * @author Koji Sugisawa
 * @since 3.0.0
 */
public class NumberValueFormatter implements ValueFormatter {

	/**
	 * 型ごとの解析処理（Number から各数値型への変換）を保持するマップです。
	 */
	private static final Map<Class<?>, Function<Number, Object>> PARSERS;

	static {
		final Map<Class<?>, Function<Number, Object>> map = new HashMap<>();
		// プリミティブ型およびラッパークラスの変換処理を登録します。
		map.put(Integer.class, Number::intValue);
		map.put(int.class, Number::intValue);
		map.put(Long.class, Number::longValue);
		map.put(long.class, Number::longValue);
		map.put(Float.class, Number::floatValue);
		map.put(float.class, Number::floatValue);
		map.put(Double.class, Number::doubleValue);
		map.put(double.class, Number::doubleValue);
		map.put(Short.class, Number::shortValue);
		map.put(short.class, Number::shortValue);
		map.put(Byte.class, Number::byteValue);
		map.put(byte.class, Number::byteValue);
		// 高精度数値型の変換処理を登録します。
		map.put(BigDecimal.class, n -> new BigDecimal(n.toString()));
		map.put(BigInteger.class, n -> new BigInteger(n.toString()));

		PARSERS = Collections.unmodifiableMap(map);
	}

	private final DecimalFormat decimalFormat;

	public NumberValueFormatter(final DecimalFormat decimalFormat) {
		this.decimalFormat = decimalFormat;
	}

	@Override
	public String format(final Object value) {
		if (value == null) {
			return null;
		}
		return decimalFormat.format(value);
	}

	@Override
	public Object parse(final String str, final Class<?> type) {
		if (str == null || str.isEmpty()) {
			return null;
		}
		try {
			// 文字列を一度 Number 型として解析します。
			final Number number = decimalFormat.parse(str);
			
			// 指定された型に対応するパーサーを取得して実行します。
			final Function<Number, Object> parser = PARSERS.get(type);
			if (parser != null) {
				return parser.apply(number);
			}
			// 対応する型がない場合は Number 型のまま返します。
			return number;
		} catch (final ParseException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

}
