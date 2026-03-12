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

import java.util.Date;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.orangesignal.csv.entity.Price;

/**
 * {@link BeanNullExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
class BeanNullExpressionTest {

	@Test
	void testBeanNullExpressionIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {

		new BeanNullExpression(null);
		});
	}

	@Test
	void testAccep() throws Exception {
		assertTrue(new BeanNullExpression("symbol").accept(new Price(null, "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertFalse(new BeanNullExpression("symbol").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertTrue(new BeanNullExpression("name").accept(new Price("GCX09", null, 1088.70, 100, new Date())));
		assertFalse(new BeanNullExpression("name").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertTrue(new BeanNullExpression("price").accept(new Price("GCX09", "COMEX 金 2009年11月限", null, 100, new Date())));
		assertFalse(new BeanNullExpression("price").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertTrue(new BeanNullExpression("date").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, null)));
		assertFalse(new BeanNullExpression("date").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
	}

	@Test
	void testToString() {
		assertThat(new BeanNullExpression("symbol").toString(), is("BeanNullExpression"));
		
	}

}