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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.orangesignal.csv.entity.Price;

/**
 * {@link BeanNotEqualExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
class BeanNotEqualExpressionTest {

	@Test
	void testBeanNotEqualExpressionIllegalArgumentException1() {
		assertThrows(IllegalArgumentException.class, () -> {

		new BeanNotEqualExpression(null, "aaa");
		});
	}

	@Test
	void testBeanNotEqualExpressionIllegalArgumentException2() {
		assertThrows(IllegalArgumentException.class, () -> {

		new BeanNotEqualExpression("col", null);
		});
	}

	@Test
	void testAccep() throws Exception {
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2009/11/06 19:14:46"));
		assertTrue(new BeanNotEqualExpression("symbol", "SIX09").accept(price));
		assertFalse(new BeanNotEqualExpression("symbol", "GCX09").accept(price));
		assertTrue(new BeanNotEqualExpression("symbol", "SIX09", false).accept(price));
		assertFalse(new BeanNotEqualExpression("symbol", "GCX09", false).accept(price));
		assertTrue(new BeanNotEqualExpression("symbol", "six09", true).accept(price));
		assertFalse(new BeanNotEqualExpression("symbol", "gcx09", true).accept(price));
		assertTrue(new BeanNotEqualExpression("name", "COMEX 銀 2009年11月限").accept(price));
		assertFalse(new BeanNotEqualExpression("name", "COMEX 金 2009年11月限").accept(price));
		assertTrue(new BeanNotEqualExpression("price", 1088.00).accept(price));
		assertFalse(new BeanNotEqualExpression("price", 1088.70).accept(price));
		assertTrue(new BeanNotEqualExpression("date", new Date()).accept(price));
		assertFalse(new BeanNotEqualExpression("date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2009/11/06 19:14:46")).accept(price));
	}

	@Test
	void testToString() {
		assertThat(new BeanNotEqualExpression("symbol", "GCX09").toString(), is("BeanNotEqualExpression"));
		
	}

}