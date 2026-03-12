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
 * {@link ColumnPositionBetweenExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
class ColumnPositionBetweenExpressionTest {

	@Test
	void testColumnPositionBetweenExpression() {
		new ColumnPositionBetweenExpression(0, "x002", "x003");
	}

	@Test
	void testColumnPositionBetweenExpressionIllegalArgumentException1() {
		assertThrows(IllegalArgumentException.class, () -> {

		new ColumnPositionBetweenExpression(0, null, "x003");
		});
	}

	@Test
	void testColumnPositionBetweenExpressionIllegalArgumentException2() {
		assertThrows(IllegalArgumentException.class, () -> {

		new ColumnPositionBetweenExpression(0, "x002", null);
		});
	}

	@Test
	void testAcceptListOfString() {
		assertFalse(new ColumnPositionBetweenExpression(0, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertFalse(new ColumnPositionBetweenExpression(1, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new ColumnPositionBetweenExpression(2, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new ColumnPositionBetweenExpression(3, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertFalse(new ColumnPositionBetweenExpression(4, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
	}

	@Test
	void testAcceptListOfStringListOfString() {
		assertFalse(new ColumnPositionBetweenExpression(0, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertFalse(new ColumnPositionBetweenExpression(1, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new ColumnPositionBetweenExpression(2, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new ColumnPositionBetweenExpression(3, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertFalse(new ColumnPositionBetweenExpression(4, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
	}

	@Test
	void testToString() {
		assertThat(new ColumnPositionBetweenExpression(0, "x002", "x003").toString(), is("ColumnPositionBetweenExpression"));
		
	}

}