/*
 * Copyright 2009-2013 the original author or authors.
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
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/**
 * {@link CsvConfig} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
final class CsvConfigTest {

	@Test
	void testCsvConfig() {
		new CsvConfig();
	}

	@Test
	void testCsvConfigChar() {
		new CsvConfig('\t');
	}

	@Test
	void testCsvConfigCharCharChar() {
		new CsvConfig('\t', '\'', '|');
	}

	@Test
	void testCsvConfigCharCharCharBooleanBoolean() {
		new CsvConfig('\t', '\'', '|', false, false);
	}

	@Test
	void testValidate1() {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvConfig('\r').validate());
		assertThat(e.getMessage(), is("Invalid separator character"));
	}

	@Test
	void testValidate2() {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvConfig('\n').validate());
		assertThat(e.getMessage(), is("Invalid separator character"));
	}

	@Test
	void testValidate3() {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvConfig(',', ',', '"').validate());
		assertThat(e.getMessage(), is("Invalid quote character"));
	}

	@Test
	void testValidate4() {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvConfig(',', '\r', '"').validate());
		assertThat(e.getMessage(), is("Invalid quote character"));
	}

	@Test
	void testValidate5() {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvConfig(',', '\n', '"').validate());
		assertThat(e.getMessage(), is("Invalid quote character"));
	}

	@Test
	void testValidate6() {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvConfig(',', '"', ',').validate());
		assertThat(e.getMessage(), is("Invalid escape character"));
	}

	@Test
	void testValidate7() {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvConfig(',', '"', '\r').validate());
		assertThat(e.getMessage(), is("Invalid escape character"));
	}

	@Test
	void testValidate8() {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvConfig(',', '"', '\n').validate());
		assertThat(e.getMessage(), is("Invalid escape character"));
	}

	@Test
	void testGetSeparator() {
		assertThat(new CsvConfig().getSeparator(), is(','));
	}

	@Test
	void testSetSeparator() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setSeparator('\t');
		assertThat(cfg.getSeparator(), is('\t'));
	}

	@Test
	void testWithSeparator() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withSeparator('\t');
		assertThat(cfg.getSeparator(), is('\t'));
	}

	@Test
	void testGetQuote() {
		assertThat(new CsvConfig().getQuote(), is('"'));
	}

	@Test
	void testSetQuote() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setQuote('\u0000');
		assertThat(cfg.getQuote(), is('\u0000'));
	}

	@Test
	void testWithQuote() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withQuote('\u0000');
		assertThat(cfg.getQuote(), is('\u0000'));
	}

	@Test
	void testGetEscape() {
		assertThat(new CsvConfig().getEscape(), is('\\'));
	}

	@Test
	void testSetEscape() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setEscape('"');
		assertThat(cfg.getEscape(), is('"'));
	}

	@Test
	void testWithEscape() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withEscape('"');
		assertThat(cfg.getEscape(), is('"'));
	}

	@Test
	void testIsQuoteDisabled() {
		assertThat(new CsvConfig().isQuoteDisabled(), is(true));
	}

	@Test
	void testSetQuoteDisabled() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setQuoteDisabled(false);
		assertThat(cfg.isQuoteDisabled(), is(false));
	}

	@Test
	void testWithQuoteDisabled() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withQuoteDisabled(false);
		assertThat(cfg.isQuoteDisabled(), is(false));
	}

	@Test
	void testIsEscapeDisabled() {
		assertThat(new CsvConfig().isEscapeDisabled(), is(true));
	}

	@Test
	void testSetEscapeDisabled() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setEscapeDisabled(false);
		assertThat(cfg.isEscapeDisabled(), is(false));
	}

	@Test
	void testWithEscapeDisabled() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withEscapeDisabled(false);
		assertThat(cfg.isEscapeDisabled(), is(false));
	}

	@Test
	void testGetBreakString() {
		final CsvConfig cfg = new CsvConfig();
		assertThat(cfg.getBreakString(), nullValue());
	}

	@Test
	void testSetBreakString() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setBreakString("\n");
		assertThat(cfg.getBreakString(), is("\n"));
	}

	@Test
	void testWithBreakString() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withBreakString("\n");
		assertThat(cfg.getBreakString(), is("\n"));
	}

	@Test
	void testGetNullString() {
		final CsvConfig cfg = new CsvConfig();
		assertThat(cfg.getNullString(), nullValue());
		assertThat(cfg.isIgnoreCaseNullString(), is(false));
	}

	@Test
	void testSetNullStringString() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setNullString("null");
		assertThat(cfg.getNullString(), is("null"));
		assertThat(cfg.isIgnoreCaseNullString(), is(false));
	}

	@Test
	void testWithNullStringString() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withNullString("null");
		assertThat(cfg.getNullString(), is("null"));
		assertThat(cfg.isIgnoreCaseNullString(), is(false));
	}

	@Test
	void testSetNullStringStringBoolean() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setNullString("null", true);
		assertThat(cfg.getNullString(), is("null"));
		assertThat(cfg.isIgnoreCaseNullString(), is(true));
	}

	@Test
	void testWithNullStringStringBoolean() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withNullString("null", true);
		assertThat(cfg.getNullString(), is("null"));
		assertThat(cfg.isIgnoreCaseNullString(), is(true));
	}

	@Test
	void testIsIgnoreLeadingWhitespaces() {
		assertThat(new CsvConfig().isIgnoreLeadingWhitespaces(), is(false));
	}

	@Test
	void testSetIgnoreLeadingWhitespaces() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setIgnoreLeadingWhitespaces(true);
		assertThat(cfg.isIgnoreLeadingWhitespaces(), is(true));
	}

	@Test
	void testWithIgnoreLeadingWhitespaces() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withIgnoreLeadingWhitespaces(true);
		assertThat(cfg.isIgnoreLeadingWhitespaces(), is(true));
	}


	@Test
	void testIsIgnoreTrailingWhitespaces() {
		assertThat(new CsvConfig().isIgnoreTrailingWhitespaces(), is(false));
	}

	@Test
	void testSetIgnoreTrailingWhitespaces() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setIgnoreTrailingWhitespaces(true);
		assertThat(cfg.isIgnoreTrailingWhitespaces(), is(true));
	}

	@Test
	void testWithIgnoreTrailingWhitespaces() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withIgnoreTrailingWhitespaces(true);
		assertThat(cfg.isIgnoreTrailingWhitespaces(), is(true));
	}

	@Test
	void testIsIgnoreEmptyLines() {
		assertThat(new CsvConfig().isIgnoreEmptyLines(), is(false));
	}

	@Test
	void testSetIgnoreEmptyLines() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setIgnoreEmptyLines(true);
		assertThat(cfg.isIgnoreEmptyLines(), is(true));
	}

	@Test
	void testWithIgnoreEmptyLines() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withIgnoreEmptyLines(true);
		assertThat(cfg.isIgnoreEmptyLines(), is(true));
	}

	@Test
	void testGetIgnoreLinePatterns() {
		assertThat(new CsvConfig().getIgnoreLinePatterns(), nullValue());
	}

	@Test
	void testSetIgnoreLinePatterns() {
		final Pattern pattern = Pattern.compile("^#[ ]*$");
		final CsvConfig cfg = new CsvConfig();
		cfg.setIgnoreLinePatterns(pattern);
		assertThat(cfg.getIgnoreLinePatterns().length, is(1));
		assertThat(cfg.getIgnoreLinePatterns()[0], is(pattern));
	}

	@Test
	void testWithIgnoreLinePatterns() {
		final Pattern pattern = Pattern.compile("^#[ ]*$");
		final CsvConfig cfg = new CsvConfig();
		cfg.withIgnoreLinePatterns(pattern);
		assertThat(cfg.getIgnoreLinePatterns().length, is(1));
		assertThat(cfg.getIgnoreLinePatterns()[0], is(pattern));
	}

	@Test
	void testGetSkipLines() {
		assertThat(new CsvConfig().getSkipLines(), is(0));
	}

	@Test
	void testSetSkipLines() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setSkipLines(2);
		assertThat(cfg.getSkipLines(), is(2));
	}

	@Test
	void testWithSkipLines() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withSkipLines(2);
		assertThat(cfg.getSkipLines(), is(2));
	}


	@Test
	void testGetLineSeparator() {
		assertThat(new CsvConfig().getLineSeparator(), is(System.getProperty("line.separator")));
	}

	@Test
	void testSetLineSeparator() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setLineSeparator("\n");
		assertThat(cfg.getLineSeparator(), is("\n"));
	}

	@Test
	void testWithLineSeparator() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withLineSeparator("\n");
		assertThat(cfg.getLineSeparator(), is("\n"));
	}

	@Test
	void testGetQuotePolicy() {
		assertThat(new CsvConfig().getQuotePolicy(), is(QuotePolicy.ALL));
	}

	@Test
	void testSetQuotePolicy() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setQuotePolicy(QuotePolicy.MINIMAL);
		assertThat(cfg.getQuotePolicy(), is(QuotePolicy.MINIMAL));
	}

	@Test
	void testWithQuotePolicy() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withQuotePolicy(QuotePolicy.MINIMAL);
		assertThat(cfg.getQuotePolicy(), is(QuotePolicy.MINIMAL));
	}

	@Test
	void testSetQuotePolicyIllegalArgumentException() {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvConfig().setQuotePolicy(null));
		assertThat(e.getMessage(), is("QuotePolicy must not be null"));
	}

	@Test
	void testWithQuotePolicyIllegalArgumentException() {
		final Throwable e = assertThrows(IllegalArgumentException.class, () -> new CsvConfig().withQuotePolicy(null));
		assertThat(e.getMessage(), is("QuotePolicy must not be null"));
	}

	@Test
	void testIsUtf8bomPolicy() {
		assertThat(new CsvConfig().isUtf8bomPolicy(), is(false));
	}

	@Test
	void testSetUtf8bomPolicy() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setUtf8bomPolicy(true);
		assertThat(cfg.isUtf8bomPolicy(), is(true));
	}

	@Test
	void testWithUtf8bomPolicy() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withUtf8bomPolicy(true);
		assertThat(cfg.isUtf8bomPolicy(), is(true));
	}

	@Test
	void testIsVariableColumns() {
		assertThat(new CsvConfig().isVariableColumns(), is(true));
	}
	@Test
	void testSetVariableColumns() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setVariableColumns(false);
		assertThat(cfg.isVariableColumns(), is(false));
	}

	@Test
	void testWithVariableColumns() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withVariableColumns(false);
		assertThat(cfg.isVariableColumns(), is(false));
	}

	@Test
	void testWithAllMethods() {
		final Pattern pattern = Pattern.compile("^#[ ]*$");
		final CsvConfig cfg = new CsvConfig();
		cfg.withSeparator('\t')
			.withQuote('\u0000')
			.withEscape('"')
			.withQuoteDisabled(false)
			.withEscapeDisabled(false)
			.withBreakString("\n")
			.withNullString("null", true)
			.withIgnoreLeadingWhitespaces(true)
			.withIgnoreTrailingWhitespaces(true)
			.withIgnoreEmptyLines(true)
			.withIgnoreLinePatterns(pattern)
			.withSkipLines(2)
			.withLineSeparator("\n")
			.withQuotePolicy(QuotePolicy.MINIMAL)
			.withUtf8bomPolicy(true)
			.withVariableColumns(false);
		assertThat(cfg.getSeparator(), is('\t'));
		assertThat(cfg.getQuote(), is('\u0000'));
		assertThat(cfg.getEscape(), is('"'));
		assertThat(cfg.isQuoteDisabled(), is(false));
		assertThat(cfg.isEscapeDisabled(), is(false));
		assertThat(cfg.getBreakString(), is("\n"));
		assertThat(cfg.getNullString(), is("null"));
		assertThat(cfg.isIgnoreCaseNullString(), is(true));
		assertThat(cfg.isIgnoreLeadingWhitespaces(), is(true));
		assertThat(cfg.isIgnoreTrailingWhitespaces(), is(true));
		assertThat(cfg.isIgnoreEmptyLines(), is(true));
		assertThat(cfg.getIgnoreLinePatterns().length, is(1));
		assertThat(cfg.getIgnoreLinePatterns()[0], is(pattern));
		assertThat(cfg.getSkipLines(), is(2));
		assertThat(cfg.getLineSeparator(), is("\n"));
		assertThat(cfg.getQuotePolicy(), is(QuotePolicy.MINIMAL));
		assertThat(cfg.isUtf8bomPolicy(), is(true));
		assertThat(cfg.isVariableColumns(), is(false));
	}

	@Test
	void testClone() {
		final CsvConfig cfg = new CsvConfig('\t', '^', '|').clone();
		assertThat(cfg.getSeparator(), is('\t'));
		assertThat(cfg.getQuote(), is('^'));
		assertThat(cfg.getEscape(), is('|'));
	}

}