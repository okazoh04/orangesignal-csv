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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.SimpleCsvValueFilter;

/**
 * {@link CsvColumnPositionMapWriter} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 * @since 1.4.0
 */
class CsvColumnPositionMapWriterTest {
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
		assertThrows(IllegalArgumentException.class, () -> {
			final CsvColumnPositionMapWriter writer = new CsvColumnPositionMapWriter(null);
			writer.close();
		});
	}

	// ------------------------------------------------------------------------
	// オーバーライド メソッド

	@Test
	void testFlush() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnPositionMapWriter writer = new CsvColumnPositionMapWriter(new CsvWriter(sw, cfg));
		try {
			writer.setFilter(new SimpleCsvValueFilter().ne(0, "gcu09", true));

			final Map<Integer, String> m0 = new HashMap<Integer, String>(5);
			m0.put(0, "GCU09");
			m0.put(1, "COMEX 金 2009年09月限");
			m0.put(2, "1068.70");
			m0.put(3, "10");
			m0.put(4, "2008/09/06");
			final boolean r0 = writer.write(m0);
			assertFalse(r0);

			writer.flush();
			assertThat(sw.getBuffer().toString(), is(""));

			final Map<Integer, String> m1 = new HashMap<Integer, String>(5);
			m1.put(0, "GCV09");
			m1.put(1, "COMEX 金 2009年10月限");
			m1.put(2, "1078.70");
			m1.put(3, "11");
			m1.put(4, "2008/10/06");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			writer.flush();
			assertThat(sw.getBuffer().toString(), is("GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n"));

			final Map<Integer, String> m2 = new HashMap<Integer, String>(5);
			m2.put(0, "GCX09");
			m2.put(1, "COMEX 金 2009年11月限");
			m2.put(2, "1088.70");
			m2.put(3, "12");
			m2.put(4, "2008/11/06");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);

			writer.flush();
			assertThat(sw.getBuffer().toString(), is("GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\nGCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\nGCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"));
	}

	@Test
	void testFlushIOException() throws IOException {
		assertThrows(IOException.class, () -> {
			final CsvColumnPositionMapWriter writer = new CsvColumnPositionMapWriter(new CsvWriter(new StringWriter(), cfg));
			writer.close();
			// Act
			writer.flush();
		});
	}

	@Test
	void testCloseIOException() throws IOException {
		assertThrows(IOException.class, () -> {
			final CsvColumnPositionMapWriter writer = new CsvColumnPositionMapWriter(new CsvWriter(new StringWriter(), cfg));
			writer.close();
			// Act
			writer.close();
		});
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	@Test
	void testWrite1() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnPositionMapWriter writer = new CsvColumnPositionMapWriter(new CsvWriter(sw, cfg));
		try {
			final Map<Integer, String> m0 = new HashMap<Integer, String>(4);
			m0.put(0, "symbol");
			m0.put(1, "name");
			m0.put(2, "price");
			m0.put(3, "volume");
			final boolean r0 = writer.write(m0);
			assertTrue(r0);

			final Map<Integer, String> m1 = new HashMap<Integer, String>(4);
			m1.put(0, "AAAA");
			m1.put(1, "aaa");
			m1.put(2, "10000");
			m1.put(3, "10");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			final Map<Integer, String> m2 = new HashMap<Integer, String>(4);
			m2.put(0, "BBBB");
			m2.put(1, "bbb");
			m2.put(2, null);
			m2.put(3, "0");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	void testWriteFilter() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvColumnPositionMapWriter writer = new CsvColumnPositionMapWriter(new CsvWriter(sw, cfg));
		try {
			writer.setFilter(new SimpleCsvValueFilter().ne(0, "gcu09", true));

			final Map<Integer, String> m0 = new HashMap<Integer, String>(5);
			m0.put(0, "GCU09");
			m0.put(1, "COMEX 金 2009年09月限");
			m0.put(2, "1068.70");
			m0.put(3, "10");
			m0.put(4, "2008/09/06");
			final boolean r0 = writer.write(m0);
			assertFalse(r0);

			final Map<Integer, String> m1 = new HashMap<Integer, String>(5);
			m1.put(0, "GCV09");
			m1.put(1, "COMEX 金 2009年10月限");
			m1.put(2, "1078.70");
			m1.put(3, "11");
			m1.put(4, "2008/10/06");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			final Map<Integer, String> m2 = new HashMap<Integer, String>(5);
			m2.put(0, "GCX09");
			m2.put(1, "COMEX 金 2009年11月限");
			m2.put(2, "1088.70");
			m2.put(3, "12");
			m2.put(4, "2008/11/06");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\nGCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"));
	}

	// ------------------------------------------------------------------------
	// セッター / ゲッター

	@Test
	void testFilter() throws Exception {
		final SimpleCsvValueFilter filter = new SimpleCsvValueFilter().ne(0, "gcu09", true);
		assertNotNull(filter);

		final StringWriter sw = new StringWriter();
		final CsvColumnPositionMapWriter writer = new CsvColumnPositionMapWriter(new CsvWriter(sw, cfg));
		try {
			writer.setFilter(filter);
			assertEquals(filter, writer.getFilter());

			final Map<Integer, String> m0 = new HashMap<Integer, String>(5);
			m0.put(0, "GCU09");
			m0.put(1, "COMEX 金 2009年09月限");
			m0.put(2, "1068.70");
			m0.put(3, "10");
			m0.put(4, "2008/09/06");
			final boolean r0 = writer.write(m0);
			assertFalse(r0);

			assertEquals(filter, writer.getFilter());

			final Map<Integer, String> m1 = new HashMap<Integer, String>(5);
			m1.put(0, "GCV09");
			m1.put(1, "COMEX 金 2009年10月限");
			m1.put(2, "1078.70");
			m1.put(3, "11");
			m1.put(4, "2008/10/06");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			assertEquals(filter, writer.getFilter());

			final Map<Integer, String> m2 = new HashMap<Integer, String>(5);
			m2.put(0, "GCX09");
			m2.put(1, "COMEX 金 2009年11月限");
			m2.put(2, "1088.70");
			m2.put(3, "12");
			m2.put(4, "2008/11/06");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);

			assertEquals(filter, writer.getFilter());
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\nGCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"));
		assertEquals(filter, writer.getFilter());
	}

}