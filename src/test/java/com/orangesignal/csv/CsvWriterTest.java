/*
 * Copyright 2009-2010 the original author or authors.
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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link CsvWriter} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
class CsvWriterTest {

	@Test
	void testCsvWriterWriterIntCsvConfig() throws IOException {
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter(), 8192, new CsvConfig());
		writer.close();
	}

	@Test
	void testCsvWriterWriterIntCsvConfigIllegalArgumentException1() throws IOException {
		// Act
		assertThrows(IllegalArgumentException.class, () -> {
			final CsvWriter writer = new CsvWriter(new StringWriter(), 0, new CsvConfig());
			writer.close();
		});
	}

	@Test
	void testCsvWriterWriterIntCsvConfigIllegalArgumentException2() throws IOException {
		// Act
		assertThrows(IllegalArgumentException.class, () -> {
			final CsvWriter writer = new CsvWriter(new StringWriter(), -8192, new CsvConfig());
			writer.close();
		});
	}

	@Test
	void testCsvWriterWriterIntCsvConfigIllegalArgumentException3() throws IOException {
		// Act
		final IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
			final CsvWriter writer = new CsvWriter(new StringWriter(), 8192, null);
			writer.close();
		});
		assertThat(e.getMessage(), is("CsvConfig must not be null"));
	}

	@Test
	void testCsvWriterWriterCsvConfig() throws IOException {
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter(), new CsvConfig());
		writer.close();
	}

	@Test
	void testCsvWriterWriterCsvConfigIllegalArgumentException() throws IOException {
		// Act
		final IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
			final CsvWriter writer = new CsvWriter(new StringWriter(), null);
			writer.close();
		});
		assertThat(e.getMessage(), is("CsvConfig must not be null"));
	}

	@Test
	void testCsvWriterWriterInt() throws IOException {
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter(), 8192);
		writer.close();
	}

	@Test
	void testCsvWriterWriterIntIllegalArgumentException1() throws IOException {
		// Act
		assertThrows(IllegalArgumentException.class, () -> {
			final CsvWriter writer = new CsvWriter(new StringWriter(), 0);
			writer.close();
		});
	}

	@Test
	void testCsvWriterWriterIntIllegalArgumentException2() throws IOException {
		// Act
		assertThrows(IllegalArgumentException.class, () -> {
			final CsvWriter writer = new CsvWriter(new StringWriter(), -8192);
			writer.close();
		});
	}

	@Test
	void testCsvWriterWriter() throws IOException {
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter());
		writer.close();
	}

	@Test
	void testWriteValues() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setLineSeparator("\r\n");

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			// Act
			writer.writeValues(Arrays.asList(new String[]{ "aaa", "b\nb\\\\b", "c\"cc" }));
			writer.writeValues(Arrays.asList(new String[]{ "zzz", "yyy", null }));
			writer.flush();
			// Assert
			assertThat(sw.getBuffer().toString(), is("\"aaa\",\"b\nb\\\\b\",\"c\\\"cc\"\r\n\"zzz\",\"yyy\",NULL\r\n"));
		} finally {
			writer.close();
		}
	}

	@Test
	void testWriteValues2() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setQuotePolicy(QuotePolicy.MINIMAL);
		cfg.setLineSeparator("\r\n");

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			// Act
			writer.writeValues(Arrays.asList(new String[]{ "aaa", "b\nb\\\\b", "c\"cc" }));
			writer.writeValues(Arrays.asList(new String[]{ "zzz", "yyy", null }));
			writer.flush();
			// Assert
			assertThat(sw.getBuffer().toString(), is("aaa,\"b\nb\\\\b\",\"c\\\"cc\"\r\nzzz,yyy,NULL\r\n"));
		} finally {
			writer.close();
		}
	}

	@Test
	void testWriteValues3() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setQuoteDisabled(true);
		cfg.setNullString("NULL");
		cfg.setLineSeparator("\r\n");

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			// Act
			writer.writeValues(Arrays.asList(new String[]{ "aaa", "b,bb", "ccc" }));
			writer.writeValues(Arrays.asList(new String[]{ "zzz", "yyy", null }));
			writer.flush();
			// Assert
			assertThat(sw.getBuffer().toString(), is("aaa,b\\,bb,ccc\r\nzzz,yyy,NULL\r\n"));
		} finally {
			writer.close();
		}
	}

/*
	@Test
	void testWriteUtf8bomToStringWriter() throws IOException {
		final CsvConfig cfg = new CsvConfig();
		cfg.setLineSeparator("\r\n");
		cfg.setUtf8bomPolicy(true);

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			// Act
			writer.writeValues(Arrays.asList(new String[]{ "佐藤", "鈴木" }));
			writer.flush();
			// Assert
			assertThat(sw.getBuffer().toString(), is("\uFEFF佐藤,鈴木\r\n"));
		} finally {
			writer.close();
		}
	}
*/

	@Test
	void testWriteValuesCsvValueException() throws IOException {
		final CsvConfig cfg = new CsvConfig();
		cfg.setVariableColumns(false);

		final CsvWriter writer = new CsvWriter(new StringWriter(), cfg);
		try {
			// Act
			writer.writeValues(Arrays.asList(new String[]{ "a", "bb", "c" }));
			writer.writeValues(Arrays.asList(new String[]{ "x", "y" }));
			writer.flush();
		} catch (final CsvValueException e) {
			// Assert
			assertThat(e.getMessage(), is("Invalid column count."));
			final List<String> tokens = e.getValues();
			assertThat(tokens.size(), is(2));
			assertThat(tokens.get(0), is("x"));
			assertThat(tokens.get(1), is("y"));
		} finally {
			writer.close();
		}
	}

	@Test
	void testWriteRfc4180() throws IOException {
		// エスケープ文字を '"' に設定 (RFC 4180 モード)
		final CsvConfig cfg = new CsvConfig(',', '"', '"');
		cfg.setLineSeparator("\r\n");

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			// Act: データ内に '"' を含める
			writer.writeValues(Arrays.asList("a", "b\"b", "c"));
			writer.flush();
			// Assert: '"' が '""' にエスケープされていること
			assertThat(sw.getBuffer().toString(), is("\"a\",\"b\"\"b\",\"c\"\r\n"));
		} finally {
			writer.close();
		}
	}

	@Test
	void testWriteDataWithMixedLineBreaks() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '"');
		cfg.setLineSeparator("\r\n");
		cfg.setQuotePolicy(QuotePolicy.MINIMAL);

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			// Act: \r, \n, \r\n が混在するデータ
			writer.writeValues(Arrays.asList("r:\r", "n:\n", "rn:\r\n"));
			writer.flush();
			// Assert: 各改行を含むフィールドが適切にクォートされていること
			assertThat(sw.getBuffer().toString(), is("\"r:\r\",\"n:\n\",\"rn:\r\n\"\r\n"));
		} finally {
			writer.close();
		}
	}

	@Test
	void testWriteRfc4180_EmptyAndCommas() throws IOException {
		// RFC 4180 設定を使用
		final CsvConfig cfg = CsvConfig.rfc4180();
		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			// Act: 空文字、カンマ入り、引用符入りの混在
			writer.writeValues(Arrays.asList("a", "", "b,c", "\"d\""));
			writer.flush();
			// Assert: 空はそのまま、カンマと引用符を含むものは囲まれる、引用符は二重化
			assertThat(sw.getBuffer().toString(), is("a,,\"b,c\",\"\"\"d\"\"\"\r\n"));
		} finally {
			writer.close();
		}
	}

	@Test
	void testWriteExcel() throws IOException {
		// Excel 設定を使用
		final CsvConfig cfg = CsvConfig.excel();
		cfg.setLineSeparator("\r\n");

		// 1. 文字列ベースの検証 (QuotePolicy.MINIMAL の確認)
		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			writer.writeValues(Arrays.asList("a", "b\"b", "c,d"));
			writer.flush();
			// excel() はデフォルトで BOM 有効。a は囲まれない。
			assertThat(sw.getBuffer().toString(), is("\uFEFFa,\"b\"\"b\",\"c,d\"\r\n"));
		} finally {
			writer.close();
		}

		// 2. バイトベースの検証 (UTF-8 BOM の確認)
		final java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		final java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(out, "UTF-8");
		final CsvWriter bomWriter = new CsvWriter(osw, cfg);
		try {
			bomWriter.writeValues(Arrays.asList("a"));
			bomWriter.flush();
			
			final byte[] bytes = out.toByteArray();
			// UTF-8 BOM (EF BB BF) が先頭にあることを確認
			assertThat(bytes.length > 3, is(true));
			assertThat(bytes[0], is((byte) 0xEF));
			assertThat(bytes[1], is((byte) 0xBB));
			assertThat(bytes[2], is((byte) 0xBF));
		} finally {
			bomWriter.close();
		}
	}

	@Test
	void testClosed() throws IOException {
		// Arrange
		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, new CsvConfig());
		writer.close();
		// Act
		final IOException e = assertThrows(IOException.class, () -> {
			writer.flush();
		});
		assertThat(e.getMessage(), is("Stream closed"));
	}

}