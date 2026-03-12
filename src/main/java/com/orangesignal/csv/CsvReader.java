/*
 * Copyright 2014 the original author or authors.
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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 区切り文字形式入力ストリームを提供します。
 * <p>
 * このクラスは、CSV ファイルの読込みにおいて文字単位の解析、行のフィルタリング、および
 * トークン（項目）の加工（エスケープ解除や空白除去など）を担当します。
 * RFC-4180 に準拠しつつ、柔軟なカスタマイズが可能です。
 * </p>
 *
 * @author Koji Sugisawa
 * @see <a href="http://www.ietf.org/rfc/rfc4180.txt">RFC-4180 Common Format and MIME Type for Comma-Separated Values (CSV) Files</a>
 */
public class CsvReader implements Closeable {

	/** 文字読込み用の内部 Reader */
	private Reader in;

	/** 区切り文字、囲み文字、エスケープ文字などの設定情報 */
	private CsvConfig cfg;

	/** 読込み中の「物理行」を保持するバッファ */
	private final StringBuilder line = new StringBuilder();

	/** 次の物理行から先読みした1文字。-1 は未読状態 */
	private int nextChar = -1;

	/** 現在の物理行バッファ内の読み取り位置 */
	private int pos;

	/** 先頭行のスキップ設定（skipLines）を処理済みかどうか */
	private boolean skiped;

	/** 項目（トークン）が開始された物理行番号 */
	private int startTokenLineNumber = 0;

	/** 項目（トークン）が終了した物理行番号。項目内に改行がある場合、開始と異なります */
	private int endTokenLineNumber = 0;

	/** 論理行（CSVとしての1行データ）が開始された物理行番号 */
	private int startLineNumber = 0;

	/** 論理行が終了した物理行番号 */
	private int endLineNumber = 0;

	/** 論理行のインデックス（0から始まるデータの行数） */
	private int lineNumber = 0;

	/** ファイル全体の終端に達したフラグ */
	private boolean endOfFile;

	/** 論理行（CSVの1行分）の終端に達したフラグ */
	private boolean endOfLine;

	/** 直前の文字が復帰文字（CR）であった場合、次の改行（LF）を適切にスキップするためのフラグ */
	private boolean cr = false;

	/** UTF-8 の場合にのみ、先頭の BOM (Byte Order Mark) を除去するか判定するためのフラグ */
	private final boolean utf8bom;

	/** 固定項目数チェック（variableColumns=false時）に使用する、前行の項目数 */
	private int countNumberOfColumns = -1;

	/** 復帰文字 (Carriage Return) */
	private static final char CR = '\r';

	/** 改行文字 (Line Feed) */
	private static final char LF = '\n';

	/** UTF-8 の BOM 文字コード */
	private static final int BOM = 0xFEFF;

	/** BufferedReader のデフォルトバッファサイズ */
	private static final int DEFAULT_CHAR_BUFFER_SIZE = 8192;

	/** 項目（トークン）解析中の一時バッファ */
	private final StringBuilder buf = new StringBuilder();

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定されたバッファーサイズと設定情報を使用してインスタンスを構築します。
	 */
	public CsvReader(final Reader in, final int sz, final CsvConfig cfg) {
		if (cfg == null) {
			throw new IllegalArgumentException("CsvConfig must not be null");
		}
		cfg.validate();
		this.in = new BufferedReader(in, sz);
		this.cfg = cfg;
		
		final String encoding;
		if (in instanceof InputStreamReader) {
			encoding = ((InputStreamReader) in).getEncoding();
		} else {
			encoding = Charset.defaultCharset().name();
		}
		// UTF-8 の場合にのみ、BOM（\uFEFF）を無視する処理を有効にします。
		this.utf8bom = encoding.toLowerCase().matches("^utf\\-{0,1}8$");
	}

	public CsvReader(final Reader in, final CsvConfig cfg) {
		this(in, DEFAULT_CHAR_BUFFER_SIZE, cfg);
	}

	public CsvReader(final Reader in, final int sz) {
		this(in, sz, new CsvConfig());
	}

	public CsvReader(final Reader in) {
		this(in, DEFAULT_CHAR_BUFFER_SIZE, new CsvConfig());
	}

	// ------------------------------------------------------------------------
	// 状態取得用アクセサ

	public int getStartLineNumber() { return startLineNumber; }
	public int getEndLineNumber() { return endLineNumber; }
	public int getLineNumber() { return lineNumber; }
	public boolean isEndOfFile() { return endOfFile; }

	/** ストリームが閉じられている場合に例外をスローします */
	private void ensureOpen() throws IOException {
		if (in == null) {
			throw new IOException("Reader closed");
		}
	}

	// ------------------------------------------------------------------------
	// 読込みロジック（高レベルAPI）

	/**
	 * 論理行を1行読込み、各項目の値を文字列のリストとして返します。
	 * 内部で {@link #readTokens()} を呼び出しています。
	 *
	 * @return 項目の文字列リスト。ファイルの終端に達した場合は null
	 * @throws IOException 入出力例外
	 */
	public List<String> readValues() throws IOException {
		final List<CsvToken> tokens = readTokens();
		if (tokens == null) {
			return null;
		}
		final List<String> results = new ArrayList<>(tokens.size());
		for (final CsvToken token : tokens) {
			results.add(token.getValue());
		}
		return results;
	}

	/**
	 * 論理行を1行読込み、各項目の詳細情報を含む {@link CsvToken} のリストを返します。
	 *
	 * @return トークンのリスト。ファイルの終端に達した場合は null
	 * @throws IOException 入出力例外
	 */
	public List<CsvToken> readTokens() throws IOException {
		synchronized (this) {
			ensureOpen();
			if (endOfFile) {
				return null;
			}
			// skipLines 設定がある場合、初回読込み時に指定行数分だけ物理行を読み飛ばします。
			if (!skiped) {
				for (int i = 0; i < cfg.getSkipLines(); i++) {
					cacheLine();
					endTokenLineNumber++;
					lineNumber++;
				}
				line.setLength(0);
				skiped = true;
			}
			return readCsvTokens();
		}
	}

	// ------------------------------------------------------------------------
	// 物理行管理ロジック

	/**
	 * 次の物理行（改行文字まで）を読込んで内部バッファにセットします。
	 * 改行コードの正規化（CRLF, CR, LF すべて対応）と、UTF-8 BOM の処理を行います。
	 *
	 * @return 行終端文字が見つかった位置（バッファ内の長さ）。見つからない（終端）は -1
	 * @throws IOException 入出力例外
	 */
	private int cacheLine() throws IOException {
		line.setLength(0);
		int c;
		if (nextChar != -1) {
			c = nextChar;
			nextChar = -1;
		} else {
			c = in.read();
			// UTF-8 の先頭かつ BOM 文字（\uFEFF）がある場合、それを読み飛ばします。
			if (lineNumber == 0 && utf8bom && c == BOM) {
				c = in.read();
			}
		}

		int breakPos = -1;
		while (c != -1) {
			line.append((char) c);
			if (c == CR) {
				breakPos = line.length();
				nextChar = in.read();
				// CR の次が LF の場合は CRLF なので、LF も現在の物理行に含めて読み飛ばし対象にします。
				if (nextChar == LF) {
					line.append((char) nextChar);
					nextChar = -1;
				}
				break;
			} else if (c == LF) {
				breakPos = line.length();
				break;
			}
			c = in.read();
		}
		pos = 0;
		return breakPos;
	}

	/**
	 * 内部物理行バッファから文字を1つ読み取ります。バッファが空なら {@link #cacheLine()} で補充します。
	 */
	private int read() throws IOException {
		synchronized (this) {
			ensureOpen();
			if (endOfFile) return -1;
			if (line.length() == 0 || line.length() <= pos) {
				cacheLine();
			}
			if (line.length() == 0) return -1;
			return line.charAt(pos++);
		}
	}

	// ------------------------------------------------------------------------
	// トークナイザー（字句解析）ロジック

	/**
	 * 1つの論理行（CSVとしての1レコード）を解析し、トークンのリストを構築します。
	 * 空行のスキップや、特定のパターンに一致する行の無視設定などを処理します。
	 */
	private List<CsvToken> readCsvTokens() throws IOException {
		List<CsvToken> results;
		int arraySize;

		// 新しい論理行の開始
		endTokenLineNumber++;
		startLineNumber = endTokenLineNumber;
		endOfLine = false;

		results = new ArrayList<>();
		do {
			// 現在の物理行バッファが空、または末尾まで読了しているなら補充
			if (line.length() == 0 || line.length() <= pos) {
				int breakLine = cacheLine();

				// 空行（空白のみの行）を無視する設定の場合のループ処理
				if (cfg.isIgnoreEmptyLines()) {
					while (line.length() > 0 && isWhitespaces(breakLine == -1 ? line : line.substring(0, breakLine - 1))) {
						endTokenLineNumber++;
						startLineNumber = endTokenLineNumber;
						lineNumber++;
						breakLine = cacheLine();
					}
				}

				// 特定のパターン（正規表現）に一致する行を無視する設定の場合のループ処理
				if (cfg.getIgnoreLinePatterns() != null) {
					boolean ignore = true;
					while (ignore && line.length() > 0) {
						ignore = false;
						String content = breakLine == -1 ? line.toString() : line.substring(0, breakLine - 1);
						for (final Pattern p : cfg.getIgnoreLinePatterns()) {
							if (p != null && p.matcher(content).matches()) {
								ignore = true;
								endTokenLineNumber++;
								startLineNumber = endTokenLineNumber;
								lineNumber++;
								breakLine = cacheLine();
								break;
							}
						}
					}
				}
			}
			// 1つずつの項目（トークン）を読込み
			startTokenLineNumber = endTokenLineNumber;
			try {
				results.add(readCsvToken());
			} catch (final IOException e) {
				if (e instanceof CsvTokenException) {
					throw e;
				}
				throw new CsvTokenException(e.getMessage(), results);
			}
		} while (!endOfLine); // 論理行の終わり（改行またはファイル終端）まで繰り返し

		endLineNumber = endTokenLineNumber;
		lineNumber++;
		arraySize = results.size();

		// 単一項目の論理行において、その値が「空行無視」または「無視パターン」に該当するか最終判定
		// ※解析結果が null であれば、その行は丸ごと無かったことにして null を返します
		if (arraySize == 1) {
			String val = results.get(0).getValue();
			if (endOfFile) {
				if (cfg.isIgnoreEmptyLines() && isWhitespaces(val)) return null;
				if (cfg.getIgnoreLinePatterns() != null) {
					for (Pattern p : cfg.getIgnoreLinePatterns()) {
						if (p != null && p.matcher(val).matches()) return null;
					}
				}
			} else {
				if (cfg.isIgnoreEmptyLines() && (line.length() == 0 || isWhitespaces(line))) return null;
			}
		}

		// 固定項目数チェック（設定されている場合）
		validateColumnCount(results);

		return results;
	}

	/** 固定項目数設定（variableColumns=false）の場合のバリデーション */
	private void validateColumnCount(List<CsvToken> results) throws IOException {
		if (!cfg.isVariableColumns()) {
			if (countNumberOfColumns >= 0 && countNumberOfColumns != results.size()) {
				throw new CsvTokenException(String.format("Invalid column count in CSV input on line %d.", startLineNumber), results);
			}
			countNumberOfColumns = results.size();
		}
	}

	/**
	 * 単一の項目（トークン）を文字単位で読込み、バッファリングします。
	 * 囲み文字（引用符）、エスケープ文字、区切り文字を考慮した複雑な状態遷移を処理します。
	 */
	private CsvToken readCsvToken() throws IOException {
		buf.setLength(0);
		boolean inQuote = false;   // 現在、引用符（"）の内側を読み取っているか
		boolean enclosed = false;  // 正しく引用符で囲まれた項目として認識されたか
		boolean escaped = false;   // 直前の文字がエスケープ文字であり、現在の文字がエスケープ対象であることを示す（引用符内用）
		boolean _escaped = false;  // 直前の文字がエスケープ文字であることを示す（引用符外用）

		endTokenLineNumber = startTokenLineNumber;

		while (true) {
			final int c = read();

			// 改行（CRLF）の正規化。直前が CR だった場合、現在の LF は読み飛ばす
			if (cr) {
				cr = false;
				escaped = false;
				if (c == LF) {
					if (inQuote) buf.append((char) c);
					continue;
				}
			} else if (_escaped && c == cfg.getSeparator()) {
				// 引用符の外で、エスケープされた区切り文字をそのまま値として採用
				buf.append((char) c);
				_escaped = false;
				continue;
			}
			_escaped = false;

			// ストリーム終端の処理
			if (c == -1) {
				if (inQuote) {
					// エスケープ文字と引用符が同じ（RFC4180形式）で、直前が引用符だった場合は正常な閉じ引用符とみなす
					if (escaped && cfg.getQuote() == cfg.getEscape()) {
						endOfLine = true;
						endOfFile = true;
						break;
					}
					throw new IOException(String.format("Unterminated quoted field at EOF (line %d)", startTokenLineNumber));
				}
				if (escaped && cfg.getQuote() != cfg.getEscape()) {
					throw new IOException(String.format("Unterminated escape sequence at EOF (line %d)", startTokenLineNumber));
				}
				endOfLine = true;
				endOfFile = true;
				break;
			}

			if (!inQuote) {
				// --- 引用符の外側の解析ロジック ---
				if (c == cfg.getSeparator()) {
					// 区切り文字（カンマ等）が見つかったので項目終了
					break;
				} else if (c == CR) {
					// 行終端（CR）が見つかったので項目および行が終了
					endOfLine = true;
					cr = true;
					break;
				} else if (c == LF) {
					// 行終端（LF）が見つかったので項目および行が終了
					endOfLine = true;
					break;
				} else if (!cfg.isQuoteDisabled() && !enclosed && c == cfg.getQuote()) {
					// 項目開始時に（前方に空白しかない状態で）引用符が見つかれば「引用符内」モードへ
					if (isWhitespaces(buf)) inQuote = true;
				} else if (cfg.isQuoteDisabled() && !cfg.isEscapeDisabled() && c == cfg.getEscape()) {
					// 引用符が無効かつエスケープが有効な場合、次の文字をエスケープ対象とする
					_escaped = true;
				}
			} else {
				// --- 引用符の内側の解析ロジック ---
				
				// エスケープ文字と引用符が同じ文字（RFC4180形式）の場合の特殊判定
				if (!cfg.isEscapeDisabled() && cfg.getQuote() == cfg.getEscape()) {
					if (escaped) {
						// 引用符が2つ重なっていた場合（エスケープされた引用符）
						if (c == cfg.getSeparator()) break;
						if (c == CR) { endOfLine = true; cr = true; break; }
						if (c == LF) { endOfLine = true; break; }
						if (c == cfg.getEscape()) {
							// "" の2つ目の引用符。エスケープ解除フラグを落として引用符として追加
							escaped = false;
							buf.append((char) c);
							continue;
						}
						// 引用符の後に、区切り文字・改行・引用符以外の文字が続くのは RFC4180 違反
						throw new IOException(String.format("Invalid character '%c' after closing quote (line %d)", (char) c, startTokenLineNumber));
					} else if (c == cfg.getEscape()) {
						// 引用符内での最初の引用符。エスケープの可能性があるためフラグを立てて保留
						escaped = true;
						buf.append((char) c);
						continue;
					}
				}

				if (c == cfg.getQuote()) {
					if (escaped) {
						// エスケープされた引用符の場合
						escaped = false;
					} else {
						// 本物の閉じ引用符。引用符内モードを終了
						inQuote = false;
						enclosed = true;
					}
				} else if (c == CR) {
					// 引用符内での改行。物理行番号をカウントアップするが継続
					cr = true;
					endTokenLineNumber++;
				} else if (c == LF) {
					endTokenLineNumber++;
				}

				// エスケープ文字自体の判定
				if (!cfg.isEscapeDisabled() && c == cfg.getEscape()) {
					escaped = true;
				} else {
					escaped = false;
				}
			}
			// 文字を項目バッファに追加
			buf.append((char) c);
		}

		// ループを抜けた際にエスケープ中だった（末尾がエスケープ文字で終わった）場合も引用符扱いとする
		if (escaped) enclosed = true;

		// 最終的な値を整形してトークンとして返す
		String value = processTokenValue(buf.toString(), enclosed);
		return new SimpleCsvToken(value, startTokenLineNumber, endTokenLineNumber, enclosed);
	}

	/**
	 * 抽出された生の文字列（バッファ内容）を、設定（空白除去、エスケープ解除など）に従って加工します。
	 */
	private String processTokenValue(String value, boolean enclosed) {
		// 引用符で囲まれていた場合の追加検証
		if (enclosed) {
			// 閉じ引用符（"）より後に、空白以外の不正な文字が含まれていないかチェック
			// もし不正な文字があれば、それは「引用符で囲まれた正しい形式」とはみなさない
			final int i = value.lastIndexOf(cfg.getQuote()) + 1;
			if (i < value.length() && !isWhitespaces(value.substring(i))) {
				enclosed = false;
			}
		}

		// 前後の空白（ホワイトスペース）除去の適用
		if (cfg.isIgnoreLeadingWhitespaces() || enclosed) value = removeLeadingWhitespaces(value);
		if (cfg.isIgnoreTrailingWhitespaces() || enclosed) value = removeTrailingWhitespaces(value);

		if (enclosed) {
			// 項目全体の最初と最後の引用符（"）を除去
			value = value.substring(1, value.length() - 1);
			// 項目内の改行（CRLF/CR/LF）を指定の改行文字列（breakString）に置換
			if (cfg.getBreakString() != null) {
				value = value.replaceAll("\r\n|\r|\n", cfg.getBreakString());
			}
			// 引用符内のエスケープ（\" -> "）を解除
			if (!cfg.isEscapeDisabled()) {
				value = unescapeQuote(value);
			}
		} else {
			// 引用符なし項目の場合、設定された Null文字列（"NULL" 等）と一致するか判定
			if (isNullString(value)) return null;
			// 引用符なし項目のエスケープ（\, -> ,）を解除
			if (value != null && !cfg.isEscapeDisabled()) {
				value = unescapeSeparator(value);
			}
		}
		return value;
	}

	/** 指定された文字列が Null文字列設定（大文字小文字の区別有無を含む）に合致するか判定します */
	private boolean isNullString(String value) {
		if (cfg.getNullString() == null) return false;
		return cfg.isIgnoreCaseNullString() ? cfg.getNullString().equalsIgnoreCase(value) : cfg.getNullString().equals(value);
	}

	/** 引用符のエスケープを解除します（例： \" を " に置換） */
	private String unescapeQuote(final String value) {
		return value.replace(String.valueOf(cfg.getEscape()) + cfg.getQuote(), String.valueOf(cfg.getQuote()));
	}

	/** 区切り文字のエスケープを解除します（例： \, を , に置換） */
	private String unescapeSeparator(final String value) {
		return value.replace(String.valueOf(cfg.getEscape()) + cfg.getSeparator(), String.valueOf(cfg.getSeparator()));
	}

	/** ストリームを閉じ、バッファをクリアします */
	@Override
	public void close() throws IOException {
		synchronized (this) {
			if (in != null) {
				in.close();
				in = null;
			}
			cfg = null;
			line.setLength(0);
		}
	}

	/** ホワイトスペースのみで構成される文字列か判定します */
	private static boolean isWhitespaces(final CharSequence value) {
		for (int i = 0, len = value.length(); i < len; i++) {
			if (!Character.isWhitespace(value.charAt(i))) return false;
		}
		return true;
	}

	/** 前方のホワイトスペースを除去します */
	private static String removeLeadingWhitespaces(final String value) {
		int pos = 0;
		while (pos < value.length() && Character.isWhitespace(value.charAt(pos))) pos++;
		return (pos == 0) ? value : value.substring(pos);
	}

	/** 後方のホワイトスペースを除去します */
	private static String removeTrailingWhitespaces(final String value) {
		int pos = value.length();
		while (pos > 0 && Character.isWhitespace(value.charAt(pos - 1))) pos--;
		return (pos == value.length()) ? value : value.substring(0, pos);
	}
}
