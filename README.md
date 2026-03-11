# [OrangeSignal CSV](http://orangesignal.github.io/orangesignal-csv/)

OrangeSignal CSV は、非常に柔軟な Java 用の CSV（区切り文字形式）読込み・書込みライブラリです。

Java Bean と CSV の相互変換、フィルタリング、ソート、ページング機能、そして LHA および ZIP 形式で圧縮された CSV ファイルの直接読込み・書込みをサポートしています。

本バージョン（3.0.0）では、Java 21 への対応、および `java.time` パッケージなどのモダンな Java API のサポートが追加されています。

## 特徴

* **柔軟なマッピング**: `@CsvEntity` アノテーションによる Java Bean との相互変換。
* **拡張された型サポート**: `java.time.*`, `java.util.UUID`, `java.util.Currency`, `java.util.Locale`, `java.net.URI`, `byte[]`（16進数文字列）などを直接サポート。
* **強力なフィルタリング**: `CsvManager` を介した柔軟な検索・抽出。
* **圧縮ファイル対応**: LHA (jLHA内蔵) および ZIP 圧縮された CSV の読込み・書込み。
* **日本語対応**: Javadoc やエラーメッセージが日本語に対応。

## 動作環境

* Java 21 以上
* Maven 3.x

## インストール

### Maven をご利用の場合

`pom.xml` に以下の依存関係を追加してください。

```xml
<dependency>
    <groupId>com.orangesignal</groupId>
    <artifactId>orangesignal-csv</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

## 使用例

### CSV エンティティクラス

```java
import java.time.LocalDate;
import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvEntity;

@CsvEntity(header = true)
public class Customer {

    @CsvColumn(name = "氏名")
    public String name;

    @CsvColumn(name = "生年月日", format = "yyyy/MM/dd")
    public LocalDate birthday;

    @CsvColumn(name = "年齢")
    public Integer age;

}
```

### 読込みの例

```java
CsvConfig cfg = new CsvConfig(',', '"', '"');
cfg.setNullString("NULL");
cfg.setIgnoreLeadingWhitespaces(true);
cfg.setIgnoreTrailingWhitespaces(true);
cfg.setIgnoreEmptyLines(true);

List<Customer> list = Csv.load(Customer.class)
    .config(cfg)
    .filter(new SimpleBeanFilter().gt("age", 20))
    .offset(0)
    .limit(100)
    .order(BeanOrder.asc("氏名"))
    .from(reader);
```

## ライセンス

* [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
* 内蔵されている jLHA は、独自のライセンス（[JLHA-LICENSE.txt](JLHA-LICENSE.txt)）の下で配布されています。
