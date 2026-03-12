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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * {@link BeanOrExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
class BeanOrExpressionTest {

	@Test
	void testBeanOrExpression() {
		new BeanOrExpression();
		new BeanOrExpression(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
	}

	@Test
	void testBeanOrExpressionIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {
			final BeanFilter[] filters = null;
			new BeanOrExpression(filters);
		});
	}

	@Test
	void testAccept() throws IOException {
		assertFalse(new BeanOrExpression(
				new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } },
				new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } }
			).accept(null));

		assertTrue(new BeanOrExpression(
				new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } },
				new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } }
			).accept(null));

		assertTrue(new BeanOrExpression(
				new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } },
				new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } }
			).accept(null));

		assertTrue(new BeanOrExpression(
				new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } },
				new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } }
			).accept(null));

		final BeanOrExpression expr1 = new BeanOrExpression();
		expr1.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } });
		expr1.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } });
		assertFalse(expr1.accept(null));

		final BeanOrExpression expr2 = new BeanOrExpression();
		expr2.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
		expr2.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } });
		assertTrue(expr2.accept(null));

		final BeanOrExpression expr3 = new BeanOrExpression();
		expr3.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } });
		expr3.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
		assertTrue(expr3.accept(null));

		final BeanOrExpression expr4 = new BeanOrExpression();
		expr4.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
		expr4.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
		assertTrue(expr4.accept(null));
	}

	@Test
	void testAcceptIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {
			new BeanOrExpression().accept(null);
		});
	}

	@Test
	void testToString() {
		assertThat(new BeanOrExpression().toString(), is("BeanOrExpression"));
	}

}
