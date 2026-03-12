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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.orangesignal.csv.entity.Price;

/**
 * {@link BeanGreaterThanOrEqualExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
class BeanGreaterThanOrEqualExpressionTest {

	@Test
	void testBeanGreaterThanOrEqualExpressionIllegalArgumentException1() {
		assertThrows(IllegalArgumentException.class, () -> {

		new BeanGreaterThanOrEqualExpression(null, 1098.00);
		});
	}

	@Test
	void testBeanGreaterThanOrEqualExpressionIllegalArgumentException2() {
		assertThrows(IllegalArgumentException.class, () -> {

		new BeanGreaterThanOrEqualExpression("price", null);
		});
	}

	@Test
	void testBeanGreaterThanOrEqualExpression() {
		new BeanGreaterThanOrEqualExpression("price", 1098.00);
		new BeanGreaterThanOrEqualExpression("price", 1098.00, null);
		new BeanGreaterThanOrEqualExpression("date", new Date());
		new BeanGreaterThanOrEqualExpression("date", new Date(), null);
	}

	@Test
	void testAccept() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertFalse(new BeanGreaterThanOrEqualExpression("price", 1098.00).accept(price));
		assertTrue(new BeanGreaterThanOrEqualExpression("price", 1088.70).accept(price));
		assertTrue(new BeanGreaterThanOrEqualExpression("price", 1088.00).accept(price));
		assertFalse(new BeanGreaterThanOrEqualExpression("date", df.parse("2009/12/06")).accept(price));
		assertTrue(new BeanGreaterThanOrEqualExpression("date", df.parse("2009/11/06")).accept(price));
		assertTrue(new BeanGreaterThanOrEqualExpression("date", df.parse("2009/10/06")).accept(price));
		assertFalse(new BeanGreaterThanOrEqualExpression("price", 1098.00, null).accept(price));
		assertTrue(new BeanGreaterThanOrEqualExpression("price", 1088.70, null).accept(price));
		assertTrue(new BeanGreaterThanOrEqualExpression("price", 1088.00, null).accept(price));
		assertFalse(new BeanGreaterThanOrEqualExpression("date", df.parse("2009/12/06"), null).accept(price));
		assertTrue(new BeanGreaterThanOrEqualExpression("date", df.parse("2009/11/06"), null).accept(price));
		assertTrue(new BeanGreaterThanOrEqualExpression("date", df.parse("2009/10/06"), null).accept(price));
	}

	@Test
	void testToString() {
		assertThat(new BeanGreaterThanOrEqualExpression("price", 1098.00).toString(), is("BeanGreaterThanOrEqualExpression"));
		
	}

}