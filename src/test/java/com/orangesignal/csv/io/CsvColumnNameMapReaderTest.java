/*
 * Copyright 2013 the original author or authors.
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

package com.orangesignal.csv.io;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;

/**
 * {@link CsvColumnNameMapReader} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 * @since 1.4.0
 */
class CsvColumnNameMapReaderTest {

	private CsvConfig cfg;

	@BeforeEach
	void setUp() throws Exception {
		cfg = new CsvConfig(',');
		cfg.setNullString("NULL");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setLineSeparator(Constants.CRLF);
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	@Test
	void testConstructorCsvReaderIllegalArgumentException() throws IOException {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvColumnNameMapReader(null));
		assertThat(e.getMessage(), is("CsvReader must not be null"));
	}

	@Test
	void testConstructorCsvReaderListIllegalArgumentException() throws IOException {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvColumnNameMapReader(null, Arrays.asList("symbol", "name", "price", "volume")));
		assertThat(e.getMessage(), is("CsvReader must not be null"));
	}

	// ------------------------------------------------------------------------
	// オーバーライド メソッド

	@Test
	void testClosed() throws IOException {
		final CsvColumnNameMapReader reader = new CsvColumnNameMapReader(new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg));
		reader.close();
		// Act
		final Throwable e = assertThrows(IOException.class, () -> reader.close());
		assertThat(e.getMessage(), is("CsvReader closed"));
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	@Test
	void testGetHeader() throws IOException {
		final CsvColumnNameMapReader reader = new CsvColumnNameMapReader(new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg));
		try {
			final List<String> h1 = reader.getHeader();
			assertThat(h1.size(), is(4));
			assertThat(h1.get(0), is("symbol"));
			assertThat(h1.get(1), is("name"));
			assertThat(h1.get(2), is("price"));
			assertThat(h1.get(3), is("volume"));

			final Map<String, String> m1 = reader.read();
			assertThat(m1.get("symbol"), is("AAAA"));
			assertThat(m1.get("name"), is("aaa"));
			assertThat(m1.get("price"), is("10000"));
			assertThat(m1.get("volume"), is("10"));

			final List<String> h2 = reader.getHeader();
			assertThat(h2.size(), is(4));
			assertThat(h2.get(0), is("symbol"));
			assertThat(h2.get(1), is("name"));
			assertThat(h2.get(2), is("price"));
			assertThat(h2.get(3), is("volume"));

			final Map<String, String> m2 = reader.read();
			assertThat(m2.get("symbol"), is("BBBB"));
			assertThat(m2.get("name"), is("bbb"));
			assertTrue(m2.containsKey("price"));
			assertNull(m2.get("price"));
			assertThat(m2.get("volume"), is("0"));

			final List<String> h3 = reader.getHeader();
			assertThat(h3.size(), is(4));
			assertThat(h3.get(0), is("symbol"));
			assertThat(h3.get(1), is("name"));
			assertThat(h3.get(2), is("price"));
			assertThat(h3.get(3), is("volume"));

			final Map<String, String> last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	@Test
	void testRead1() throws IOException {
		final CsvColumnNameMapReader reader = new CsvColumnNameMapReader(new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg));
		try {
			final Map<String, String> m1 = reader.read();
			assertThat(m1.get("symbol"), is("AAAA"));
			assertThat(m1.get("name"), is("aaa"));
			assertThat(m1.get("price"), is("10000"));
			assertThat(m1.get("volume"), is("10"));
			final Map<String, String> m2 = reader.read();
			assertThat(m2.get("symbol"), is("BBBB"));
			assertThat(m2.get("name"), is("bbb"));
			assertTrue(m2.containsKey("price"));
			assertNull(m2.get("price"));
			assertThat(m2.get("volume"), is("0"));
			final Map<String, String> last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	@Test
	void testRead2() throws IOException {
		cfg.setSkipLines(1);
		final CsvColumnNameMapReader reader = new CsvColumnNameMapReader(
				new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg),
				Arrays.asList("symbol", "name", "price", "volume")
			);
		try {
			final Map<String, String> m1 = reader.read();
			assertThat(m1.get("symbol"), is("AAAA"));
			assertThat(m1.get("name"), is("aaa"));
			assertThat(m1.get("price"), is("10000"));
			assertThat(m1.get("volume"), is("10"));
			final Map<String, String> m2 = reader.read();
			assertThat(m2.get("symbol"), is("BBBB"));
			assertThat(m2.get("name"), is("bbb"));
			assertTrue(m2.containsKey("price"));
			assertNull(m2.get("price"));
			assertThat(m2.get("volume"), is("0"));
			final Map<String, String> last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	@Test
	void testReadFilter() throws IOException {
		final CsvColumnNameMapReader reader = new CsvColumnNameMapReader(new CsvReader(new StringReader(
				"symbol,name,price,volume,date\r\n" +
				"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
				"GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n" +
				"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"
			), cfg));
		try {
			reader.setFilter(new SimpleCsvNamedValueFilter().ne(0, "gcu09", true));

			final Map<String, String> m1 = reader.read();
			assertThat(m1.get("symbol"), is("GCV09"));
			assertThat(m1.get("name"), is("COMEX 金 2009年10月限"));
			assertThat(m1.get("price"), is("1078.70"));
			assertThat(m1.get("volume"), is("11"));
			assertThat(m1.get("date"), is("2008/10/06"));
			final Map<String, String> m2 = reader.read();
			assertThat(m2.get("symbol"), is("GCX09"));
			assertThat(m2.get("name"), is("COMEX 金 2009年11月限"));
			assertThat(m2.get("price"), is("1088.70"));
			assertThat(m2.get("volume"), is("12"));
			assertThat(m2.get("date"), is("2008/11/06"));
			final Map<String, String> last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	// ------------------------------------------------------------------------
	// セッター / ゲッター

	@Test
	void testFilter() throws IOException {
		final SimpleCsvNamedValueFilter filter = new SimpleCsvNamedValueFilter().ne(0, "gcu09", true);
		assertNotNull(filter);

		final CsvColumnNameMapReader reader = new CsvColumnNameMapReader(new CsvReader(new StringReader(
				"symbol,name,price,volume,date\r\n" +
				"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
				"GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n" +
				"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"
			), cfg));
		try {
			reader.setFilter(filter);
			assertEquals(filter, reader.getFilter());

			final Map<String, String> m1 = reader.read();
			assertThat(m1.get("symbol"), is("GCV09"));
			assertThat(m1.get("name"), is("COMEX 金 2009年10月限"));
			assertThat(m1.get("price"), is("1078.70"));
			assertThat(m1.get("volume"), is("11"));
			assertThat(m1.get("date"), is("2008/10/06"));

			assertEquals(filter, reader.getFilter());

			final Map<String, String> m2 = reader.read();
			assertThat(m2.get("symbol"), is("GCX09"));
			assertThat(m2.get("name"), is("COMEX 金 2009年11月限"));
			assertThat(m2.get("price"), is("1088.70"));
			assertThat(m2.get("volume"), is("12"));
			assertThat(m2.get("date"), is("2008/11/06"));

			assertEquals(filter, reader.getFilter());

			final Map<String, String> last = reader.read();
			assertNull(last);

			assertEquals(filter, reader.getFilter());
		} finally {
			reader.close();
		}

		assertEquals(filter, reader.getFilter());
	}

}