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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;

/**
 * {@link CsvColumnNameMapWriter} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 * @since 1.4.0
 */
class CsvColumnNameMapWriterTest {

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
	void testConstructorCsvWriterIllegalArgumentException() throws IOException {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvColumnNameMapWriter(null));
		assertThat(e.getMessage(), is("CsvWriter must not be null"));
	}

	@Test
	void testConstructorCsvWriterListIllegalArgumentException() throws IOException {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvColumnNameMapWriter(null, Arrays.asList("symbol", "name", "price", "volume")));
		assertThat(e.getMessage(), is("CsvWriter must not be null"));
	}

	// ------------------------------------------------------------------------
	// オーバーライド メソッド

	@Test
	void testFlush() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(sw, cfg));
		try {
			final Map<String, String> m1 = new LinkedHashMap<String, String>(4);
			m1.put("symbol", "AAAA");
			m1.put("name", "aaa");
			m1.put("price", "10000");
			m1.put("volume", "10");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\n"));

			final Map<String, String> m2 = new LinkedHashMap<String, String>(4);
			m2.put("symbol", "BBBB");
			m2.put("name", "bbb");
			m2.put("price", null);
			m2.put("volume", "0");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);

			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));

		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	void testFlushIOException() throws IOException {
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(new StringWriter(), cfg));
		writer.close();
		// Act
		final Throwable e = assertThrows(IOException.class, () -> writer.flush());
		assertThat(e.getMessage(), is("CsvWriter closed"));
	}

	@Test
	void testCloseIOException() throws IOException {
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(new StringWriter(), cfg));
		writer.close();
		// Act
		final Throwable e = assertThrows(IOException.class, () -> writer.close());
		assertThat(e.getMessage(), is("CsvWriter closed"));
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	@Test
	void testWriteNoHeader() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(sw, cfg), false);
		try {
			final Map<String, String> m1 = new LinkedHashMap<String, String>(4);
			m1.put("symbol", "AAAA");
			m1.put("name", "aaa");
			m1.put("price", "10000");
			m1.put("volume", "10");

			writer.writeHeader(m1);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is(""));

			final boolean r1 = writer.write(m1);
			assertTrue(r1);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10000,10\r\n"));

			final Map<String, String> m2 = new LinkedHashMap<String, String>(4);
			m2.put("symbol", "BBBB");
			m2.put("name", "bbb");
			m2.put("price", null);
			m2.put("volume", "0");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	void testWriteHeader() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(sw, cfg));
		try {
			final Map<String, String> m1 = new LinkedHashMap<String, String>(4);
			m1.put("symbol", "AAAA");
			m1.put("name", "aaa");
			m1.put("price", "10000");
			m1.put("volume", "10");

			writer.writeHeader(m1);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\n"));

			final boolean r1 = writer.write(m1);
			assertTrue(r1);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\n"));

			final Map<String, String> m2 = new LinkedHashMap<String, String>(4);
			m2.put("symbol", "BBBB");
			m2.put("name", "bbb");
			m2.put("price", null);
			m2.put("volume", "0");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	void testNoHeader() throws IOException {
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(new StringWriter(), cfg));
		try {
			final Throwable e = assertThrows(IOException.class, () -> writer.write(null));
			assertThat(e.getMessage(), is("No header is available"));
		} finally {
			writer.close();
		}
	}

	@Test
	void testWrite1() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(sw, cfg));
		try {
			final Map<String, String> m1 = new LinkedHashMap<String, String>(4);
			m1.put("symbol", "AAAA");
			m1.put("name", "aaa");
			m1.put("price", "10000");
			m1.put("volume", "10");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			final Map<String, String> m2 = new LinkedHashMap<String, String>(4);
			m2.put("symbol", "BBBB");
			m2.put("name", "bbb");
			m2.put("price", null);
			m2.put("volume", "0");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	void testWrite2() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(
				new CsvWriter(sw, cfg),
				Arrays.asList("symbol", "name", "price", "volume")
			);
		try {
			final Map<String, String> m1 = new LinkedHashMap<String, String>(4);
			m1.put("symbol", "AAAA");
			m1.put("name", "aaa");
//			m1.put("price", "10000");
			m1.put("volume", "10");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			final Map<String, String> m2 = new LinkedHashMap<String, String>(4);
			m2.put("symbol", "BBBB");
			m2.put("name", "bbb");
//			m2.put("price", null);
			m2.put("volume", "0");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,NULL,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	void testWriteFilter() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(sw, cfg));
		try {
			writer.setFilter(new SimpleCsvNamedValueFilter().ne("symbol", "gcu09", true));

			final Map<String, String> m0 = new LinkedHashMap<String, String>(5);
			m0.put("symbol", "GCU09");
			m0.put("name", "COMEX 金 2009年09月限");
			m0.put("price", "1068.70");
			m0.put("volume", "10");
			m0.put("date", "2008/09/06");
			final boolean r0 = writer.write(m0);
			assertFalse(r0);

			final Map<String, String> m1 = new LinkedHashMap<String, String>(5);
			m1.put("symbol", "GCV09");
			m1.put("name", "COMEX 金 2009年10月限");
			m1.put("price", "1078.70");
			m1.put("volume", "11");
			m1.put("date", "2008/10/06");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			final Map<String, String> m2 = new LinkedHashMap<String, String>(5);
			m2.put("symbol", "GCX09");
			m2.put("name", "COMEX 金 2009年11月限");
			m2.put("price", "1088.70");
			m2.put("volume", "12");
			m2.put("date", "2008/11/06");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);

		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nGCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\nGCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"));
	}

	// ------------------------------------------------------------------------
	// セッター / ゲッター

	@Test
	void testFilter() throws Exception {
		final SimpleCsvNamedValueFilter filter = new SimpleCsvNamedValueFilter().ne("symbol", "gcu09", true);
		assertNotNull(filter);

		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(sw, cfg));
		try {
			writer.setFilter(filter);
			assertEquals(filter, writer.getFilter());

			final Map<String, String> m0 = new LinkedHashMap<String, String>(5);
			m0.put("symbol", "GCU09");
			m0.put("name", "COMEX 金 2009年09月限");
			m0.put("price", "1068.70");
			m0.put("volume", "10");
			m0.put("date", "2008/09/06");
			final boolean r0 = writer.write(m0);
			assertFalse(r0);

			assertEquals(filter, writer.getFilter());

			final Map<String, String> m1 = new LinkedHashMap<String, String>(5);
			m1.put("symbol", "GCV09");
			m1.put("name", "COMEX 金 2009年10月限");
			m1.put("price", "1078.70");
			m1.put("volume", "11");
			m1.put("date", "2008/10/06");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			assertEquals(filter, writer.getFilter());

			final Map<String, String> m2 = new LinkedHashMap<String, String>(5);
			m2.put("symbol", "GCX09");
			m2.put("name", "COMEX 金 2009年11月限");
			m2.put("price", "1088.70");
			m2.put("volume", "12");
			m2.put("date", "2008/11/06");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);

			assertEquals(filter, writer.getFilter());
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nGCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\nGCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"));
		assertEquals(filter, writer.getFilter());
	}

}