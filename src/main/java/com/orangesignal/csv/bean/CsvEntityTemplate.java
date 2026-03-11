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

import java.lang.reflect.Field;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvColumns;
import com.orangesignal.csv.annotation.CsvEntity;
import com.orangesignal.csv.filters.CsvNamedValueFilter;

/**
 * 区切り文字形式データ注釈要素 {@link CsvEntity} で注釈付けされた Java プログラム要素の操作を簡素化するヘルパークラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.4.0
 * @see CsvEntity
 * @see CsvColumn
 * @see CsvColumns
 */
public class CsvEntityTemplate<T> extends AbstractCsvBeanTemplate<T, CsvEntityTemplate<T>> implements CsvEntityOperation<CsvEntityTemplate<T>> {

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvNamedValueFilter filter;

	// ------------------------------------------------------------------------
	// 利便性のための静的メソッド

	/**
	 * 新しい {@link CsvEntityTemplate} のインスタンスを返します。
	 * 
	 * @param entityClass 区切り文字形式データ注釈要素 {@link CsvEntity} で注釈付けされた Java プログラム要素の型
	 * @return 新しい {@link CsvEntityTemplate} のインスタンス
	 * @throws IllegalArgumentException {@code entityClass} が {@code null} または不正な場合
	 */
	public static <T> CsvEntityTemplate<T> newInstance(final Class<T> entityClass) {
		return new CsvEntityTemplate<T>(entityClass);
	}

	// -----------------------------------------------------------------------
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param entityClass 区切り文字形式データ注釈要素 {@link com.orangesignal.csv.annotation.CsvEntity} で注釈付けされた Java プログラム要素の型
	 * @throws IllegalArgumentException {@code entityClass} が {@code null} または不正な場合
	 */
	public CsvEntityTemplate(final Class<T> entityClass) {
		super(entityClass);
		if (entityClass.getAnnotation(CsvEntity.class) == null) {
			throw new IllegalArgumentException(String.format("No CsvEntity is available %s", entityClass.getName()));
		}
	}

	// -----------------------------------------------------------------------
	// オーバーライド メソッド

	@Override
	public CsvEntityTemplate<T> filter(final CsvNamedValueFilter filter) {
		this.filter = filter;
		return this;
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	/**
	 * 指定された区切り文字形式データの値リストが含まれる必要があるかどうかを判定します。
	 * 
	 * @param columnNames 区切り文字形式データの項目名リスト
	 * @param values 区切り文字形式データの項目値のリスト
	 * @return {@code values} が含まれる必要がある場合は {@code true}
	 */
	public boolean isAccept(final List<String> columnNames, final List<String> values) {
		return filter != null && !filter.accept(columnNames, values);
	}

	/**
	 * 項目名のリストを作成して返します。
	 * 
	 * @return 項目名のリスト
	 */
	public List<String> createColumnNames() {
		final SortedMap<Integer, String> positionMap = new TreeMap<Integer, String>();
		final List<String> adding = new ArrayList<String>();

		for (final Field f : getType().getDeclaredFields()) {
			final CsvColumns columns = f.getAnnotation(CsvColumns.class);
			if (columns != null) {
				for (final CsvColumn column : columns.value()) {
					final int pos = column.position();
					final String name = defaultIfEmpty(column.name(), f.getName());
					if (pos >= 0) {
						if (positionMap.containsKey(pos)) {
							continue;
						}
						positionMap.put(pos, name);
					} else {
						adding.add(name);
					}
				}
			}
			final CsvColumn column = f.getAnnotation(CsvColumn.class);
			if (column != null) {
				final int pos = column.position();
				final String name = defaultIfEmpty(column.name(), f.getName());
				if (pos >= 0) {
					if (positionMap.containsKey(pos)) {
						continue;
					}
					positionMap.put(pos, name);
				} else {
					adding.add(name);
				}
			}
		}

		final int max = positionMap.size() > 0 ? positionMap.lastKey().intValue() + 1 : 0;
		final String[] names = new String[max];
		for (final Map.Entry<Integer, String> entry : positionMap.entrySet()) {
			names[entry.getKey().intValue()] = entry.getValue();
		}

		final List<String> results = new ArrayList<String>(Arrays.asList(names));
		if (adding.size() > 0) {
			results.addAll(adding);
		}
		return results;
	}

	/**
	 * 出力可能な項目名のリストを作成して返します。
	 * 
	 * @return 項目名のリスト
	 * @since 2.2
	 */
	public List<String> createWritableColumnNames() {
		final SortedMap<Integer, String> positionMap = new TreeMap<Integer, String>();
		final List<String> adding = new ArrayList<String>();

		for (final Field f : getType().getDeclaredFields()) {
			final CsvColumns columns = f.getAnnotation(CsvColumns.class);
			if (columns != null) {
				for (final CsvColumn column : columns.value()) {
					if (!column.access().isWriteable()) {
						continue;
					}
					final int pos = column.position();
					final String name = defaultIfEmpty(column.name(), f.getName());
					if (pos >= 0) {
						if (positionMap.containsKey(pos)) {
							continue;
						}
						positionMap.put(pos, name);
					} else {
						adding.add(name);
					}
				}
			}
			final CsvColumn column = f.getAnnotation(CsvColumn.class);
			if (column != null && column.access().isWriteable()) {
				final int pos = column.position();
				final String name = defaultIfEmpty(column.name(), f.getName());
				if (pos >= 0) {
					if (positionMap.containsKey(pos)) {
						continue;
					}
					positionMap.put(pos, name);
				} else {
					adding.add(name);
				}
			}
		}

		final int max = positionMap.size() > 0 ? positionMap.lastKey().intValue() + 1 : 0;
		final String[] names = new String[max];
		for (final Map.Entry<Integer, String> entry : positionMap.entrySet()) {
			names[entry.getKey().intValue()] = entry.getValue();
		}

		final List<String> results = new ArrayList<String>(Arrays.asList(names));
		if (adding.size() > 0) {
			results.addAll(adding);
		}
		return results;
	}

	public void prepare(final List<String> names, final Field[] fields) {
		super.valueParserMapping(new HashMap<String, Format>(0));
		super.valueFormatterMapping(new HashMap<Object, Format>(0));

		// 書式オブジェクトの準備を行います。
		for (final Field f : fields) {
			final CsvColumns columns = f.getAnnotation(CsvColumns.class);
			if (columns != null) {
				final List<ValueFormatter> parsers = new ArrayList<ValueFormatter>();
				final StringBuilder pattern = new StringBuilder();
				String language = null;
				String country = null;
				String timezone = null;
				for (final CsvColumn column : columns.value()) {
					if (!column.format().isEmpty()) {
						if (pattern.length() > 0) {
							pattern.append(' ');
						}
						pattern.append(column.format());
						if (language == null && !column.language().isEmpty()) {
							language = column.language();
						}
						if (country == null && !column.country().isEmpty()) {
							country = column.country();
						}
						if (timezone == null && !column.timezone().isEmpty()) {
							timezone = column.timezone();
						}
					}
					final ValueFormatter vf = ValueFormatterFactory.createValueFormatter(column, f.getType(), getValueConverter());
					if (vf != null) {
						setValueParser(f.getName(), vf);
						final int pos = getPosition(column, f, names);
						if (pos != -1) {
							setValueFormatter(pos, vf);
						}
						parsers.add(vf);
					}
				}
				if (pattern.length() > 0) {
					final Locale locale = language != null ? (country != null ? Locale.of(language, country) : Locale.of(language)) : null;
					final ValueFormatter combined = ValueFormatterFactory.createValueFormatter(pattern.toString(), f.getType(), locale, timezone, null, getValueConverter());
					if (combined != null) {
						parsers.add(combined);
					}
				}
				setFieldColumnParsers(f.getName(), parsers);
			}
			final CsvColumn column = f.getAnnotation(CsvColumn.class);
			if (column != null) {
				final ValueFormatter vf = ValueFormatterFactory.createValueFormatter(column, f.getType(), getValueConverter());
				if (vf != null) {
					setValueParser(f.getName(), vf);
					final int pos = getPosition(column, f, names);
					if (pos != -1) {
						setValueFormatter(pos, vf);
					}
				}
			}
		}
	}

	// ------------------------------------------------------------------------

	public static int getPosition(final CsvColumn column, final Field f, final List<String> names) {
		// 項目位置が指定されている場合は、項目位置から値を取得します。
		int pos = column.position();
		// 項目位置が指定されておらずヘッダ行が使用可能な場合は項目名から値を取得します。
		if (pos < 0 && names != null) {
			pos = names.indexOf(defaultIfEmpty(column.name(), f.getName()));
		}
		return pos;
	}

	public static String defaultIfEmpty(final String str, final String defaultStr) {
		return str == null || str.isEmpty() ? defaultStr : str;
	}

}