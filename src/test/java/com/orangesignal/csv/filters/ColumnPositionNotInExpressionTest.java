/*
 * Copyright 2009 the original author or authors.
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

package com.orangesignal.csv.filters;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link ColumnPositionNotInExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
class ColumnPositionNotInExpressionTest {

	@Test
	void testColumnPositionNotInExpressionIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {

		final String[] criterias = null;
		new ColumnPositionNotInExpression(0, criterias);
		});
	}

	@Test
	void testAcceptListOfString() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertTrue(new ColumnPositionNotInExpression(0, "a", "aa", "aaa").accept(values));
		assertFalse(new ColumnPositionNotInExpression(1, "a", "aa", "aaa").accept(values));
		assertTrue(new ColumnPositionNotInExpression(2, "a", "aa", "aaa").accept(values));
		assertTrue(new ColumnPositionNotInExpression(0, new String[]{ "a", "aa", "aaa" }, false).accept(values));
		assertFalse(new ColumnPositionNotInExpression(1, new String[]{ "a", "aa", "aaa" }, false).accept(values));
		assertTrue(new ColumnPositionNotInExpression(2, new String[]{ "a", "aa", "aaa" }, false).accept(values));
		assertTrue(new ColumnPositionNotInExpression(0, new String[]{ "A", "AA", "AAA" }, true).accept(values));
		assertFalse(new ColumnPositionNotInExpression(1, new String[]{ "A", "AA", "AAA" }, true).accept(values));
		assertTrue(new ColumnPositionNotInExpression(2, new String[]{ "A", "AA", "AAA" }, true).accept(values));
	}

	@Test
	void testAcceptListOfStringListOfString() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertTrue(new ColumnPositionNotInExpression(0, "a", "aa", "aaa").accept(null, values));
		assertFalse(new ColumnPositionNotInExpression(1, "a", "aa", "aaa").accept(null, values));
		assertTrue(new ColumnPositionNotInExpression(2, "a", "aa", "aaa").accept(null, values));
		assertTrue(new ColumnPositionNotInExpression(0, new String[]{ "a", "aa", "aaa" }, false).accept(null, values));
		assertFalse(new ColumnPositionNotInExpression(1, new String[]{ "a", "aa", "aaa" }, false).accept(null, values));
		assertTrue(new ColumnPositionNotInExpression(2, new String[]{ "a", "aa", "aaa" }, false).accept(null, values));
		assertTrue(new ColumnPositionNotInExpression(0, new String[]{ "A", "AA", "AAA" }, true).accept(null, values));
		assertFalse(new ColumnPositionNotInExpression(1, new String[]{ "A", "AA", "AAA" }, true).accept(null, values));
		assertTrue(new ColumnPositionNotInExpression(2, new String[]{ "A", "AA", "AAA" }, true).accept(null, values));
	}

	@Test
	void testToString() {
		assertThat(new ColumnPositionNotInExpression(0, "a", "aa", "aaa").toString(), is("ColumnPositionNotInExpression"));
		
	}

}