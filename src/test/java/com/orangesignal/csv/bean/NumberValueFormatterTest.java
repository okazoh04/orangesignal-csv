/*
 * Copyright 2026 the original author or authors.
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

package com.orangesignal.csv.bean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

import org.junit.jupiter.api.Test;

/**
 * {@link NumberValueFormatter} クラスの単体テストです。
 */
class NumberValueFormatterTest {

	@Test
	void testFormat() {
		final NumberValueFormatter f = new NumberValueFormatter(new DecimalFormat("#,##0"));
		assertThat(f.format(null), is(nullValue()));
		assertThat(f.format(1234), is("1,234"));
		assertThat(f.format(1234.567), is("1,235"));
	}

	@Test
	void testParse() {
		final NumberValueFormatter f = new NumberValueFormatter(new DecimalFormat("#,##0.###"));
		assertThat(f.parse(null, Integer.class), is(nullValue()));
		assertThat(f.parse("", Integer.class), is(nullValue()));

		assertThat(f.parse("1,234", Integer.class), is(1234));
		assertThat(f.parse("1,234", int.class), is(1234));
		assertThat(f.parse("1,234", Long.class), is(1234L));
		assertThat(f.parse("1,234", long.class), is(1234L));
		assertThat(f.parse("1,234.567", Float.class), is(1234.567f));
		assertThat(f.parse("1,234.567", float.class), is(1234.567f));
		assertThat(f.parse("1,234.567", Double.class), is(1234.567d));
		assertThat(f.parse("1,234.567", double.class), is(1234.567d));
		assertThat(f.parse("1,234", Short.class), is((short) 1234));
		assertThat(f.parse("1,234", short.class), is((short) 1234));
		assertThat(f.parse("123", Byte.class), is((byte) 123));
		assertThat(f.parse("123", byte.class), is((byte) 123));
		assertThat(f.parse("1,234.567", BigDecimal.class), is(new BigDecimal("1234.567")));
		assertThat(f.parse("1,234", BigInteger.class), is(new BigInteger("1234")));
		
		assertThat(f.parse("1,234", Number.class), is(1234L)); // Default from DecimalFormat.parse for non-decimal
	}

}
