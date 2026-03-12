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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * {@link SimpleCsvToken} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
class SimpleCsvTokenTest {

	@Test
	void testSimpleCsvToken() {
		new SimpleCsvToken();
		new SimpleCsvToken("a\nb\nc", 1, 3, true);
	}

	@Test
	void testGetValue() {
		assertThat(new SimpleCsvToken("a\nb\nc", 1, 3, true).getValue(), is("a\nb\nc"));
	}

	@Test
	void testGetStartLineNumber() {
		assertThat(new SimpleCsvToken("a\nb\nc", 1, 3, true).getStartLineNumber(), is(1));
	}

	@Test
	void testGetEndLineNumber() {
		assertThat(new SimpleCsvToken("a\nb\nc", 1, 3, true).getEndLineNumber(), is(3));
	}

	@Test
	void testIsEnclosed() {
		assertThat(new SimpleCsvToken("a\nb\nc", 1, 3, true).isEnclosed(), is(true));
	}

}