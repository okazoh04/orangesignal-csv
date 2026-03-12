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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * {@link BeanAndExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
class BeanAndExpressionTest {

	@Test
	void testBeanAndExpression() {
		// Act
		new BeanAndExpression();
		// Act
		new BeanAndExpression(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
	}

	@Test
	void testBeanAndExpressionIllegalArgumentException() {
		// Arrange
		final BeanFilter[] filters = null;
		// Act
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new BeanAndExpression(filters));
		assertThat(e.getMessage(), is("BeanFilter must not be null"));
	}

	@Test
	void testAccept() throws IOException {
		assertFalse(new BeanAndExpression(
				new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } },
				new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } }
			).accept(null));

		assertFalse(new BeanAndExpression(
				new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } },
				new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } }
			).accept(null));

		assertFalse(new BeanAndExpression(
				new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } },
				new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } }
			).accept(null));

		assertTrue(new BeanAndExpression(
				new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } },
				new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } }
			).accept(null));

		final BeanAndExpression expr1 = new BeanAndExpression();
		expr1.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } });
		expr1.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } });
		assertFalse(expr1.accept(null));

		final BeanAndExpression expr2 = new BeanAndExpression();
		expr2.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
		expr2.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } });
		assertFalse(expr2.accept(null));

		final BeanAndExpression expr3 = new BeanAndExpression();
		expr3.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } });
		expr3.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
		assertFalse(expr3.accept(null));

		final BeanAndExpression expr4 = new BeanAndExpression();
		expr4.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
		expr4.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
		assertTrue(expr4.accept(null));
	}

	@Test
	void testAcceptIllegalArgumentException() throws IOException {
		// Act
		assertThrows(IllegalArgumentException.class, () -> new BeanAndExpression().accept(null));
	}

	@Test
	void testToString() {
		assertThat(new BeanAndExpression().toString(), is("BeanAndExpression"));
	}

}