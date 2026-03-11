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

/**
 * デフォルトの値を扱う {@link ValueFormatter} の実装クラスです。
 * 特別の書式化を行わず、単純な文字列変換を提供します。
 * 
 * @author Koji Sugisawa
 * @since 3.0.0
 */
public class DefaultValueFormatter implements ValueFormatter {

	private final CsvValueConverter converter;

	public DefaultValueFormatter(final CsvValueConverter converter) {
		this.converter = converter;
	}

	@Override
	public String format(final Object value) {
		return converter.convert(value);
	}

	@Override
	public Object parse(final String str, final Class<?> type) {
		return converter.convert(str, type);
	}

}
