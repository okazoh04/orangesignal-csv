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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.manager.CsvEntityManager;

public class JavaTimeFormatTest {

	@Test
	public void testJavaTimeFormat() throws Exception {
		final List<SampleJavaTimeBean> list = List.of(
				new SampleJavaTimeBean(LocalDate.of(2026, 3, 11), LocalDateTime.of(2026, 3, 11, 12, 34, 56))
		);

		final CsvEntityManager manager = new CsvEntityManager(new CsvConfig().withIgnoreEmptyLines(true));

		// 書き出しテスト
		final StringWriter sw = new StringWriter();
		manager.save(list, SampleJavaTimeBean.class).to(sw);

		final String csv = sw.toString();
		// フォーマットされていることを確認
		assertThat("CSV content: " + csv, csv.contains("2026/03/11"), is(true));
		assertThat("CSV content: " + csv, csv.contains("2026-03-11 12:34:56"), is(true));

		// 読み込みテスト
		try {
			final List<SampleJavaTimeBean> loaded = manager.load(SampleJavaTimeBean.class).from(new StringReader(csv));
			assertThat("Loaded items: " + loaded, loaded.size(), is(1));
			assertThat(loaded.get(0).date, is(LocalDate.of(2026, 3, 11)));
			assertThat(loaded.get(0).dateTime, is(LocalDateTime.of(2026, 3, 11, 12, 34, 56)));
		} catch (Exception e) {
			throw new Exception("Error during load. CSV was: \n" + csv, e);
		}
	}

}
