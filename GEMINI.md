# OrangeSignal CSV プロジェクト コンテキスト

このファイルは、OrangeSignal CSV プロジェクトの構造、ビルド方法、および開発規約に関する情報を提供します。

## プロジェクト概要

OrangeSignal CSV は、非常に柔軟な Java 用の CSV（区切り文字形式）読込み・書込みライブラリです。

- **主な機能:**
  - Java Bean と CSV の相互変換（アノテーション `@CsvEntity` によるマッピング）
  - 柔軟なフィルタリング、ソート、ページング機能
  - LHA および ZIP 形式で圧縮された CSV ファイルの直接読込み・書込み
  - 日本語環境への配慮（Javadoc やエラーメッセージが日本語に対応）
- **主な技術スタック:**
  - **言語:** Java 21 (LTS 互換性を維持)
  - **ビルドツール:** Maven (Compiler 3.13.0+, Surefire 3.2.5+)
  - **テスト:** JUnit 4, JUnit Benchmarks
  - **統合支援:** Spring Framework, Seasar2 (テストにて統合を検証)
- **サードパーティライブラリ:**
  - **jLHA:** LHA ライブラリを内蔵（`com.orangesignal.jlha` パッケージ）

## ビルドと実行

プロジェクトの管理には Maven を使用します。

### 基本コマンド

- **ビルドとインストール:**
  ```bash
  mvn clean install
  ```
- **テストの実行:**
  ```bash
  mvn test
  ```
- **ドキュメント（サイト）の生成:**
  ```bash
  mvn site
  ```
  ※ 日本語 (ja) ロケールでサイトが生成されます。

### 環境要件

- JDK 21 以上
- Maven 3.x

## 開発規約

### コーディングスタイル

- **Java 21 準拠:** Java 21 までの機能（Records, Pattern Matching, java.time, HexFormat など）が利用可能です。
- **拡張された型サポート:** `SimpleCsvValueConverter` により、以下の型が CSV カラムとして直接利用可能です。
  - `java.time.*` (`Instant`, `MonthDay`, `Year`, `YearMonth` など)
  - `java.util.UUID`
  - `java.util.Currency`
  - `java.util.Locale` (BCP 47 言語タグ形式)
  - `java.net.URI` / `java.net.InetAddress`
  - `byte[]` (16 進数文字列形式、Java 17 `HexFormat` を使用)
- **Javadoc/コメント:** 基本的に**日本語**で記述してください。
- **パッケージ構造:**
  - `com.orangesignal.csv`: コアロジック（Reader/Writer, Config）
  - `com.orangesignal.csv.annotation`: CSV マッピング用アノテーション
  - `com.orangesignal.csv.bean`: Bean マッピングロジック
  - `com.orangesignal.csv.manager`: ハイレベル API (`CsvManager`)
  - `com.orangesignal.jlha`: LHA 圧縮サポート（外部ライブラリの取り込み）

### テスト方針

- `src/test/java` に JUnit 4 によるテストコードを記述してください。
- パフォーマンステスト（ベンチマーク）は `CsvReaderBenchmarks.java` などを参考にしてください。
- 新機能の追加やバグ修正の際は、必ず対応するテストケースを追加してください。

### 特記事項

- `com.orangesignal.jlha` パッケージ内のコードは、サードパーティ製ライブラリをプロジェクトに取り込んだものです。Checkstyle や FindBugs の対象から除外される設定になっています。
- プロジェクトのライセンスは Apache License, Version 2.0 です。
