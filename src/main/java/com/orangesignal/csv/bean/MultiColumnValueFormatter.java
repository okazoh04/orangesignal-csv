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

import java.util.List;

/**
 * 複数の {@link ValueFormatter} をまとめる {@link ValueFormatter} の実装クラスです。
 * 
 * @author Koji Sugisawa
 * @since 3.0.0
 */
public class MultiColumnValueFormatter implements ValueFormatter {

	private final List<ValueFormatter> formatters;

	/**
	 * コンストラクタです。
	 * 
	 * @param formatters {@link ValueFormatter} のリスト
	 */
	public MultiColumnValueFormatter(final List<ValueFormatter> formatters) {
		this.formatters = formatters;
	}

	@Override
	public String format(final Object value) {
		if (value == null) {
			return null;
		}
		// 書き出しには最初のフォーマットを使用します。
		return formatters.get(0).format(value);
	}

	@Override
	public Object parse(final String str, final Class<?> type) {
		if (str == null || str.isEmpty()) {
			return null;
		}
		for (final ValueFormatter f : formatters) {
			try {
				return f.parse(str, type);
			} catch (final Exception e) {
				// 次のフォーマットを試します。
			}
		}
		throw new IllegalArgumentException(String.format("Unparseable value: \"%s\"", str));
	}

}
