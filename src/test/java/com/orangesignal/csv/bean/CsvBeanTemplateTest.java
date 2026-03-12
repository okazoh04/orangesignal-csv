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

package com.orangesignal.csv.bean;

import org.junit.jupiter.api.Test;

import com.orangesignal.csv.model.SampleBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link CsvBeanTemplate} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 * @since 1.4.0
 */
class CsvBeanTemplateTest {

	@Test
	void testIncludesIllegalArgumentException() {
		final IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
			new CsvBeanTemplate<SampleBean>(SampleBean.class).excludes("aaa").includes("bbb");
		});
		assertEquals("Only includes or excludes may be specified.", e.getMessage());
	}

	@Test
	void testExcludesIllegalArgumentException() {
		final IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
			new CsvBeanTemplate<SampleBean>(SampleBean.class).includes("aaa").excludes("bbb");
		});
		assertEquals("Only includes or excludes may be specified.", e.getMessage());
	}

}