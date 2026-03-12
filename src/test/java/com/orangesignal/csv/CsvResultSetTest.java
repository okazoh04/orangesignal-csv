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

package com.orangesignal.csv;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * {@link CsvResultSet} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
class CsvResultSetTest {

	private static CsvConfig cfg;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setBreakString("\n");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setIgnoreLinePatterns(Pattern.compile("^#.*$"));
	}

	@Test
	void testCsvResultSet() throws IOException {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("field_name\r\nxxx")));
		rs.close();
	}

	@Test
	void testCsvResultSetIllegalArgumentException() throws IOException  {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			try(CsvResultSet rs = new CsvResultSet(null)){}
		});
	}

	@Test
	void testCsvResultSetIOException() throws IOException  {
		Assertions.assertThrows(IOException.class, () -> {
			try(CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader(""), cfg))){}
		});
	}

	@Test
	void testEnsureOpen() throws Exception  {
		Assertions.assertThrows(SQLException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader(
				"code, market, name, price, date, time, datetime, active \r\n" +
				"9999, T1, OrangeSignal CSV test, NULL, 2009-01-01, 12:00:00, 2009-01-01 12:00:00, 0 \r\n" +
				"9999, T1, OrangeSignal CSV test, 500.05, 2009-01-01, 12:00:00, 2009-01-01 12:00:00, 1 \r\n"
			), cfg));
		rs.close();
		rs.next();
		});
	}

	@Test
	void test() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader(
				"code, market, name, price, date, time, datetime, active \r\n" +
				"9999, T1, OrangeSignal CSV test, NULL, 2009-01-01, 12:00:00, 2009-01-01 12:00:00, 0 \r\n" +
				"9999, T1, OrangeSignal CSV test, 500.05, 2009-01-01, 12:00:00, 2009-01-01 12:00:00, 1 \r\n"
			), cfg));
		try {
			assertThat(rs.getStatement(), nullValue());
			assertThat(rs.isClosed(), is(false));
			assertThat(rs.getFetchDirection(), is(ResultSet.FETCH_FORWARD));
			assertThat(rs.getFetchSize(), is(0));
			assertThat(rs.getType(), is(ResultSet.TYPE_FORWARD_ONLY));
			assertThat(rs.getConcurrency(), is(ResultSet.CONCUR_READ_ONLY));
			assertThat(rs.getHoldability(), is(ResultSet.HOLD_CURSORS_OVER_COMMIT));

			assertThat(rs.next(), is(true));
			assertThat(rs.getRow(), is(1));

			assertThat(rs.getString(1), is("9999"));
			assertThat(rs.wasNull(), is(false));
			assertThat(rs.getString("code"), is("9999"));
			assertThat(rs.wasNull(), is(false));

			assertThat(rs.getShort(1), is((short) 9999));
			assertThat(rs.getShort("code"), is((short) 9999));
			assertThat(rs.getInt(1), is(9999));
			assertThat(rs.getInt("code"), is(9999));

			assertThat(rs.getLong(4), is(0L));
			assertThat(rs.wasNull(), is(true));
			assertThat(rs.getLong("price"), is(0L));
			assertThat(rs.wasNull(), is(true));

			assertThat(rs.getBoolean(8), is(false));
			assertThat(rs.getBoolean("active"), is(false));

			assertThat(rs.getString(4), nullValue());
			assertThat(rs.wasNull(), is(true));
//			assertThat(rs.getString(1), is("aaa"));
//			assertThat(rs.getString("col1"), is("aaa"));

		} finally {
			rs.close();
			assertThat(rs.isClosed(), is(true));
		}
	}

	@Test
	@SuppressWarnings("deprecation")
	void testGetBigDecimalIntInt() throws Exception  {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\n1"), cfg));
		try {
			assertThat(rs.next(), is(true));
			assertThat(rs.getBigDecimal(1, 0), is(new BigDecimal("1")));
		} finally {
			rs.close();
		}
	}

	@Test
	void testGetUnicodeStreamInt() throws Exception  {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\naaa"), cfg));
		try {
			assertThat(rs.next(), is(true));
			try (@SuppressWarnings("deprecation")
			InputStream in = rs.getUnicodeStream(1)) {
				assertThat(in, is(notNullValue()));
			}
		} finally {
			rs.close();
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	void testGetBigDecimalStringInt() throws Exception  {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\n1"), cfg));
		try {
			assertThat(rs.next(), is(true));
			assertThat(rs.getBigDecimal("id", 0), is(new BigDecimal("1")));
		} finally {
			rs.close();
		}
	}

	@Test
	void testGetUnicodeStreamString() throws Exception  {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\naaa"), cfg));
		try {
			assertThat(rs.next(), is(true));
			try (@SuppressWarnings("deprecation")
			InputStream in = rs.getUnicodeStream("id")) {
				assertThat(in, is(notNullValue()));
			}
		} finally {
			rs.close();
		}
	}

	@Test
	void testGetCursorName() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getCursorName();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testIsBeforeFirst() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.isBeforeFirst();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testIsAfterLast() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.isAfterLast();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testIsFirst() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.isFirst();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testIsLast() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.isLast();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testBeforeFirst() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.beforeFirst();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testAfterLast() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.afterLast();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testFirst() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.first();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testLast() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.last();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testAbsolute() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.absolute(0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testRelative() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.relative(0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testPrevious() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.previous();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testSetFetchDirection() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.setFetchDirection(ResultSet.FETCH_FORWARD);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testSetFetchSize() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.setFetchSize(0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testRowUpdated() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.rowUpdated();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testRowInserted() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.rowInserted();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testRowDeleted() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.rowDeleted();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNullInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNull(1);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBooleanIntBoolean() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBoolean(1, false);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateByteIntByte() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateByte(1, (byte) 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateShortIntShort() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateShort(1, (short) 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateIntIntInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateInt(1, 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateLongIntLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateLong(1, 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateFloatIntFloat() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateFloat(1, 0F);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateDoubleIntDouble() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateDouble(1, 0D);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBigDecimalIntBigDecimal() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBigDecimal(1, null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateStringIntString() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateString(1, null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBytesIntByteArray() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBytes(1, null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateDateIntDate() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateDate(1, null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateTimeIntTime() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateTime(1, null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateTimestampIntTimestamp() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateTimestamp(1, null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateAsciiStreamIntInputStreamInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateAsciiStream(1, null, 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBinaryStreamIntInputStreamInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBinaryStream(1, null, 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateCharacterStreamIntReaderInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateCharacterStream(1, null, 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateObjectIntObjectInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateObject(1, null, 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateObjectIntObject() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateObject(1, null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNullString() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNull("id");
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBooleanStringBoolean() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBoolean("id", false);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateByteStringByte() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateByte("id", (byte) 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateShortStringShort() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateShort("id", (short) 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateIntStringInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateInt("id", 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateLongStringLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateLong("id", 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateFloatStringFloat() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateFloat("id", 0F);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateDoubleStringDouble() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateDouble("id", 0D);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBigDecimalStringBigDecimal() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBigDecimal("id", null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateStringStringString() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateString("id", null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBytesStringByteArray() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBytes("id", null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateDateStringDate() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateDate("id", null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateTimeStringTime() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateTime("id", null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateTimestampStringTimestamp() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateTimestamp("id", null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateAsciiStreamStringInputStreamInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateAsciiStream("id", null, 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBinaryStreamStringInputStreamInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBinaryStream("id", null, 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateCharacterStreamStringReaderInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateCharacterStream("id", new StringReader(""), 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateObjectStringObjectInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateObject("id", null, 0);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateObjectStringObject() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateObject("id", null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testInsertRow() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.insertRow();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateRow() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateRow();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testDeleteRow() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.deleteRow();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testRefreshRow() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.refreshRow();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testCancelRowUpdates() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.cancelRowUpdates();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testMoveToInsertRow() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.moveToInsertRow();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testMoveToCurrentRow() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.moveToCurrentRow();
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testGetObjectIntMapOfStringClassOfQ() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getObject(1, (Map<String, Class<?>>) null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testGetRefInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getRef(1);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testGetArrayInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getArray(1);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testGetObjectStringMapOfStringClassOfQ() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getObject("id", (Map<String, Class<?>>) null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testGetRefString() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getRef("id");
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateRefIntRef() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateRef(1, null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateRefStringRef() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateRef("id", null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBlobIntBlob() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			final Blob blob = null;
			rs.updateBlob(1, blob);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBlobStringBlob() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			final Blob blob = null;
			rs.updateBlob("id", blob);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateClobIntClob() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			final Clob clob = null;
			rs.updateClob(1, clob);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateClobStringClob() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			final Clob clob = null;
			rs.updateClob("id", clob);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateArrayIntArray() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateArray(1, null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateArrayStringArray() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateArray("id", null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testGetRowIdInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getRowId(1);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testGetRowIdString() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getRowId("id");
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateRowIdIntRowId() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateRowId(1, null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateRowIdStringRowId() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateRowId("id", null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNStringIntString() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNString(1, null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNStringStringString() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNString("id", null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNClobIntNClob() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			final NClob nclob = null;
			rs.updateNClob(1, nclob);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNClobStringNClob() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			final NClob nclob = null;
			rs.updateNClob("id", nclob);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testGetSQLXMLInt() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getSQLXML(1);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testGetSQLXMLString() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getSQLXML("id");
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateSQLXMLIntSQLXML() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateSQLXML(1, null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateSQLXMLStringSQLXML() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateSQLXML("id", null);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNCharacterStreamIntReaderLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNCharacterStream(1, new StringReader(""), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNCharacterStreamStringReaderLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNCharacterStream("id", new StringReader(""), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateAsciiStreamIntInputStreamLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateAsciiStream(1, new ByteArrayInputStream("".getBytes()), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBinaryStreamIntInputStreamLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBinaryStream(1, new ByteArrayInputStream("".getBytes()), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateCharacterStreamIntReaderLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateCharacterStream(1, new StringReader(""), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateAsciiStreamStringInputStreamLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateAsciiStream("id", new ByteArrayInputStream("".getBytes()), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBinaryStreamStringInputStreamLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBinaryStream("id", new ByteArrayInputStream("".getBytes()), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateCharacterStreamStringReaderLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateCharacterStream("id", new StringReader(""), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBlobIntInputStreamLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBlob(1, new ByteArrayInputStream("".getBytes()), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBlobStringInputStreamLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBlob("id", new ByteArrayInputStream("".getBytes()), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateClobIntReaderLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateClob(1, new StringReader(""), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateClobStringReaderLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateClob("id", new StringReader(""), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNClobIntReaderLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNClob(1, new StringReader(""), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNClobStringReaderLong() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNClob("id", new StringReader(""), 0L);
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNCharacterStreamIntReader() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNCharacterStream(1, new StringReader(""));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNCharacterStreamStringReader() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNCharacterStream("id", new StringReader(""));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateAsciiStreamIntInputStream() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateAsciiStream(1, new ByteArrayInputStream("".getBytes()));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBinaryStreamIntInputStream() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBinaryStream(1, new ByteArrayInputStream("".getBytes()));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateCharacterStreamIntReader() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateCharacterStream(1, new StringReader(""));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateAsciiStreamStringInputStream() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateAsciiStream("id", new ByteArrayInputStream("".getBytes()));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBinaryStreamStringInputStream() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBinaryStream("id", new ByteArrayInputStream("".getBytes()));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateCharacterStreamStringReader() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateCharacterStream("id", new StringReader(""));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBlobIntInputStream() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBlob(1, new ByteArrayInputStream("".getBytes()));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateBlobStringInputStream() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBlob("id", new ByteArrayInputStream("".getBytes()));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateClobIntReader() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateClob(1, new StringReader(""));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateClobStringReader() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateClob("id", new StringReader(""));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNClobIntReader() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNClob(1, new StringReader(""));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUpdateNClobStringReader() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNClob("id", new StringReader(""));
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testUnwrap() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.unwrap(this.getClass());
		} finally {
			rs.close();
		}
		});
	}

	@Test
	void testIsWrapperFor() throws Exception  {
		Assertions.assertThrows(SQLFeatureNotSupportedException.class, () -> {
			final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.isWrapperFor(this.getClass());
		} finally {
			rs.close();
		}
		});
	}

}