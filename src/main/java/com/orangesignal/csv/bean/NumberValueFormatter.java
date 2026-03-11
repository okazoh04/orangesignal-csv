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

/**
 * 数値型を扱う {@link ValueFormatter} の実装クラスです。
 * 
 * @author Koji Sugisawa
 * @since 3.0.0
 */
public class NumberValueFormatter implements ValueFormatter {

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
			final Number number = decimalFormat.parse(str);
			if (type.equals(Integer.class) || type.equals(int.class)) {
				return number.intValue();
			} else if (type.equals(Long.class) || type.equals(long.class)) {
				return number.longValue();
			} else if (type.equals(Float.class) || type.equals(float.class)) {
				return number.floatValue();
			} else if (type.equals(Double.class) || type.equals(double.class)) {
				return number.doubleValue();
			} else if (type.equals(Short.class) || type.equals(short.class)) {
				return number.shortValue();
			} else if (type.equals(Byte.class) || type.equals(byte.class)) {
				return number.byteValue();
			} else if (type.equals(BigDecimal.class)) {
				return new BigDecimal(number.toString());
			} else if (type.equals(BigInteger.class)) {
				return new BigInteger(number.toString());
			}
			return number;
		} catch (final ParseException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

}
