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
 * {@link ColumnNameBetweenExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
class ColumnNameBetweenExpressionTest {

	@Test
	void testColumnNameBetweenExpressionIllegalArgumentException1() {
		assertThrows(IllegalArgumentException.class, () -> {

		new ColumnNameBetweenExpression(null, "x002", "x003");
		});
	}

	@Test
	void testColumnNameBetweenExpressionIllegalArgumentException2() {
		assertThrows(IllegalArgumentException.class, () -> {

		new ColumnNameBetweenExpression("col", null, "x003");
		});
	}

	@Test
	void testColumnNameBetweenExpressionIllegalArgumentException3() {
		assertThrows(IllegalArgumentException.class, () -> {

		new ColumnNameBetweenExpression("col", "x002", null);
		});
	}

	@Test
	void testAccept() {
		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" });
		assertFalse(new ColumnNameBetweenExpression("col0", "x002", "x003").accept(header, values));
		assertFalse(new ColumnNameBetweenExpression("col1", "x002", "x003").accept(header, values));
		assertTrue(new ColumnNameBetweenExpression("col2", "x002", "x003").accept(header, values));
		assertTrue(new ColumnNameBetweenExpression("col3", "x002", "x003").accept(header, values));
		assertFalse(new ColumnNameBetweenExpression("col4", "x002", "x003").accept(header, values));
	}

	@Test
	void testAcceptIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {

		new ColumnNameBetweenExpression("col", "x002", "x003").accept(Arrays.asList(new String[]{ "col0", "col1", "col2" }), null);
		});
	}

	@Test
	void testToString() {
		assertThat(new ColumnNameBetweenExpression("col", "x002", "x003").toString(), is("ColumnNameBetweenExpression"));
		
	}

}