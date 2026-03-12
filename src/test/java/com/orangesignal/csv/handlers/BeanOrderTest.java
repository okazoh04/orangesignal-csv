/*
 * Copyright 2010 the original author or authors.
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

package com.orangesignal.csv.handlers;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.orangesignal.csv.model.SamplePrice;

/**
 * {@link BeanOrder} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
class BeanOrderTest {

	@Test
	void testBeanOrder() {
		new BeanOrder("date", false, true);
	}

	@Test
	void testBeanOrderIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {

		new BeanOrder(null, false, true);
		});
	}

	@Test
	void testAscString() {
		BeanOrder.asc("date");
	}

	@Test
	void testAscStringIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {

		BeanOrder.asc(null);
		});
	}

	@Test
	void testAscStringBoolean() {
		BeanOrder.asc("symbol", true);
	}

	@Test
	void testAscStringBooleanIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {

		BeanOrder.asc(null, true);
		});
	}

	@Test
	void testDescString() {
		BeanOrder.desc("date");
	}

	@Test
	void testDescStringIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {

		BeanOrder.desc(null);
		});
	}

	@Test
	void testDescStringBoolean() {
		BeanOrder.desc("symbol", true);
	}

	@Test
	void testDescStringBooleanIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {

		BeanOrder.desc(null, true);
		});
	}

	@Test
	void testCompare() {
		final Date now = new Date();
		final SamplePrice p1 = new SamplePrice();
		p1.setName("Y");
		p1.setDate(now);
		p1.setPrice(100);

		final SamplePrice p2 = new SamplePrice();
		p2.setName("X");
		p2.setDate(now);
		p2.setPrice(200);

		assertThat(BeanOrder.asc("name").compare(p1, p2), is(1));
		assertThat(BeanOrder.asc("date").compare(p1, p2), is(0));
		assertThat(BeanOrder.asc("price").compare(p1, p2), is(-1));
	}

	@Test
	void testToString() {
		assertThat(BeanOrder.asc("date").toString(), is("date asc"));
		assertThat(BeanOrder.asc("symbol", true).toString(), is("symbol asc"));
		assertThat(BeanOrder.desc("date").toString(), is("date desc"));
		assertThat(BeanOrder.desc("symbol", true).toString(), is("symbol desc"));
	}

}