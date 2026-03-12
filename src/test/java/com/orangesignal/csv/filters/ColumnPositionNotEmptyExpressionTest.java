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

/**
 * {@link ColumnPositionNotEmptyExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
class ColumnPositionNotEmptyExpressionTest {

	@Test
	void testColumnPositionNotEmptyExpression() {
		new ColumnPositionNotEmptyExpression(0);
	}

	@Test
	void testAcceptListOfString() {
		assertFalse(new ColumnPositionNotEmptyExpression(0).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(1).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(2).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new ColumnPositionNotEmptyExpression(0).accept(Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(1).accept(Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(2).accept(Arrays.asList(new String[]{ "", "aaa", "bbb" })));
	}

	@Test
	void testAcceptListOfStringListOfString() {
		assertFalse(new ColumnPositionNotEmptyExpression(0).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(1).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(2).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new ColumnPositionNotEmptyExpression(0).accept(null, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(1).accept(null, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(2).accept(null, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
	}

	@Test
	void testToString() {
		assertThat(new ColumnPositionNotEmptyExpression(0).toString(), is("ColumnPositionNotEmptyExpression"));
		
	}

}