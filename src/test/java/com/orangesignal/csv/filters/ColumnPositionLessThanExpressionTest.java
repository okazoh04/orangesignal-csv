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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link ColumnPositionLessThanExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
class ColumnPositionLessThanExpressionTest {

	@Test
	void testColumnPositionLessThanExpression() {
		new ColumnPositionLessThanExpression(0, "100");
	}

	@Test
	void testColumnPositionLessThanExpressionIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {

		new ColumnPositionLessThanExpression(0, null);
		});
	}

	@Test
	void testAcceptListOfString() {
		assertFalse(new ColumnPositionLessThanExpression(0, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new ColumnPositionLessThanExpression(1, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new ColumnPositionLessThanExpression(2, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new ColumnPositionLessThanExpression(3, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
	}

	@Test
	void testAcceptListOfStringListOfString() {
		assertFalse(new ColumnPositionLessThanExpression(0, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new ColumnPositionLessThanExpression(1, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new ColumnPositionLessThanExpression(2, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new ColumnPositionLessThanExpression(3, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
	}

	@Test
	void testToString() {
		assertThat(new ColumnPositionLessThanExpression(0, "100").toString(), is("ColumnPositionLessThanExpression"));
		
	}

}