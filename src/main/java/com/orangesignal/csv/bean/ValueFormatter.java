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
 * 値の書式化と解析を行うストラテジーのインターフェースです。
 * 
 * @author Koji Sugisawa
 * @since 3.0.0
 */
public interface ValueFormatter {

	/**
	 * 指定されたオブジェクトを書式化して文字列を返します。
	 * 
	 * @param value オブジェクト
	 * @return 書式化された文字列
	 */
	String format(Object value);

	/**
	 * 指定された文字列を解析してオブジェクトを返します。
	 * 
	 * @param str 文字列
	 * @param type 解析後の型
	 * @return 解析されたオブジェクト
	 */
	Object parse(String str, Class<?> type);

}
