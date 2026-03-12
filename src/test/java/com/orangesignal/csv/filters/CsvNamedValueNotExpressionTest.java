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

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link CsvNamedValueNotExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
class CsvNamedValueNotExpressionTest {

	@Test
	void testCsvNamedValueNotExpressionIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {

		new CsvNamedValueNotExpression(null);
		});
	}

	@Test
	void testAccept() {
		assertTrue(new CsvNamedValueNotExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } }
			).accept(null, null));
		assertFalse(new CsvNamedValueNotExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } }
			).accept(null, null));
	}

	@Test
	void testToString() {
		assertThat(new CsvNamedValueNotExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } }
			).toString(), is("CsvNamedValueNotExpression"));
		
	}

}