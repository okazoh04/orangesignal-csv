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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link CsvExpressionUtils} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
class CsvExpressionUtilsTest {

	@Test
	void testIsNullIllegalArgumentException1() {
		assertThrows(IllegalArgumentException.class, () -> {

		CsvExpressionUtils.isNull(null, 0);
		});
	}

	@Test
	void testIsNullIllegalArgumentException2() {
		assertThrows(IllegalArgumentException.class, () -> {

		CsvExpressionUtils.isNull(Arrays.asList(new String[]{ null, "", "" }), -1);
		});
	}

	@Test
	void testIsNullIllegalArgumentException3() {
		assertThrows(IllegalArgumentException.class, () -> {

		CsvExpressionUtils.isNull(Arrays.asList(new String[]{ null, "", "" }), 3);
		});
	}

	@Test
	void testIsNull() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertTrue(CsvExpressionUtils.isNull(values, 0));
		assertFalse(CsvExpressionUtils.isNull(values, 1));
	}

	@Test
	void testIsNotNull() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertTrue(CsvExpressionUtils.isNotNull(values, 1));
		assertFalse(CsvExpressionUtils.isNotNull(values, 0));
	}

	@Test
	void testIsEmpty() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "" });
		assertTrue(CsvExpressionUtils.isEmpty(values, 0));
		assertFalse(CsvExpressionUtils.isEmpty(values, 1));
		assertTrue(CsvExpressionUtils.isEmpty(values, 2));
	}

	@Test
	void testIsNotEmpty() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "" });
		assertFalse(CsvExpressionUtils.isNotEmpty(values, 0));
		assertTrue(CsvExpressionUtils.isNotEmpty(values, 1));
		assertFalse(CsvExpressionUtils.isNotEmpty(values, 2));
	}

	@Test
	void testEqIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {

		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		CsvExpressionUtils.eq(values, 0, null, false);
		});
	}

	@Test
	void testEq() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.eq(values, 0, "aaa", false));
		assertTrue(CsvExpressionUtils.eq(values, 1, "aaa", false));
		assertFalse(CsvExpressionUtils.eq(values, 2, "aaa", false));
		assertFalse(CsvExpressionUtils.eq(values, 0, "AAA", true));
		assertTrue(CsvExpressionUtils.eq(values, 1, "AAA", true));
		assertFalse(CsvExpressionUtils.eq(values, 2, "AAA", true));
	}

	@Test
	void testNeIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {

		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		CsvExpressionUtils.ne(values, 0, null, false);
		});
	}

	@Test
	void testNe() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertTrue(CsvExpressionUtils.ne(values, 0, "aaa", false));
		assertFalse(CsvExpressionUtils.ne(values, 1, "aaa", false));
		assertTrue(CsvExpressionUtils.ne(values, 2, "aaa", false));
		assertTrue(CsvExpressionUtils.ne(values, 0, "AAA", true));
		assertFalse(CsvExpressionUtils.ne(values, 1, "AAA", true));
		assertTrue(CsvExpressionUtils.ne(values, 2, "AAA", true));
	}

	@Test
	void testInIllegalArgumentException1() {
		assertThrows(IllegalArgumentException.class, () -> {

		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		final String[] criterias = null;
		CsvExpressionUtils.in(values, 0, criterias, false);
		});
	}

	@Test
	void testInIllegalArgumentException2() {
		assertThrows(IllegalArgumentException.class, () -> {

		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		final String[] criterias = new String[]{ null };
		CsvExpressionUtils.in(values, 0, criterias, false);
		});
	}

	@Test
	void testIn() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.in(values, 0, new String[]{ "a", "aa", "aaa" }, false));
		assertTrue(CsvExpressionUtils.in(values, 1, new String[]{ "a", "aa", "aaa" }, false));
		assertFalse(CsvExpressionUtils.in(values, 2, new String[]{ "a", "aa", "aaa" }, false));
		assertFalse(CsvExpressionUtils.in(values, 0, new String[]{ "A", "AA", "AAA" }, true));
		assertTrue(CsvExpressionUtils.in(values, 1, new String[]{ "A", "AA", "AAA" }, true));
		assertFalse(CsvExpressionUtils.in(values, 2, new String[]{ "A", "AA", "AAA" }, true));
	}

	@Test
	void testNotInIllegalArgumentException1() {
		assertThrows(IllegalArgumentException.class, () -> {

		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		final String[] criterias = null;
		CsvExpressionUtils.notIn(values, 0, criterias, false);
		});
	}

	@Test
	void testNotInIllegalArgumentException2() {
		assertThrows(IllegalArgumentException.class, () -> {

		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		final String[] criterias = new String[]{ null };
		CsvExpressionUtils.notIn(values, 0, criterias, false);
		});
	}

	@Test
	void testNotIn() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertTrue(CsvExpressionUtils.notIn(values, 0, new String[]{ "a", "aa", "aaa" }, false));
		assertFalse(CsvExpressionUtils.notIn(values, 1, new String[]{ "a", "aa", "aaa" }, false));
		assertTrue(CsvExpressionUtils.notIn(values, 2, new String[]{ "a", "aa", "aaa" }, false));
		assertTrue(CsvExpressionUtils.notIn(values, 0, new String[]{ "A", "AA", "AAA" }, true));
		assertFalse(CsvExpressionUtils.notIn(values, 1, new String[]{ "A", "AA", "AAA" }, true));
		assertTrue(CsvExpressionUtils.notIn(values, 2, new String[]{ "A", "AA", "AAA" }, true));
	}

	@Test
	void testRegex() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.regex(values, 0, Pattern.compile("^[a]+$")));
		assertTrue(CsvExpressionUtils.regex(values, 1, Pattern.compile("^[a]+$")));
		assertFalse(CsvExpressionUtils.regex(values, 2, Pattern.compile("^[a]+$")));
	}

	@Test
	void testGt() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.gt(values, 0, "aaa"));
		assertFalse(CsvExpressionUtils.gt(values, 1, "aaa"));
		assertTrue(CsvExpressionUtils.gt(values, 2, "aaa"));
	}

	@Test
	void testLt() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.lt(values, 0, "bbb"));
		assertTrue(CsvExpressionUtils.lt(values, 1, "bbb"));
		assertFalse(CsvExpressionUtils.lt(values, 2, "bbb"));
	}

	@Test
	void testGe() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.ge(values, 0, "aaa"));
		assertTrue(CsvExpressionUtils.ge(values, 1, "aaa"));
		assertTrue(CsvExpressionUtils.ge(values, 2, "aaa"));
	}

	@Test
	void testLe() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.le(values, 0, "bbb"));
		assertTrue(CsvExpressionUtils.le(values, 1, "bbb"));
		assertTrue(CsvExpressionUtils.le(values, 2, "bbb"));
	}

	@Test
	void testBetween() {
		final List<String> values = Arrays.asList(new String[]{ null, "100", "10000" });
		assertFalse(CsvExpressionUtils.between(values, 0, "10", "1000"));
		assertTrue(CsvExpressionUtils.between(values, 1, "10", "1000"));
		assertFalse(CsvExpressionUtils.between(values, 2, "10", "1000"));
	}

	@Test
	void testValidate1() {
		assertThrows(IllegalArgumentException.class, () -> {

		CsvExpressionUtils.validate(null, 0);
		});
	}

	@Test
	void testValidate2() {
		assertThrows(IllegalArgumentException.class, () -> {

		CsvExpressionUtils.validate(Arrays.asList(new String[]{ "", "", "" }), -1);
		});
	}

	@Test
	void testValidateListOfStringInt3() {
		assertThrows(IllegalArgumentException.class, () -> {

		CsvExpressionUtils.validate(Arrays.asList(new String[]{ "", "", "" }), 3);
		});
	}

}