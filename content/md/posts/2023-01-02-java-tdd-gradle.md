{:title "Java で TDD するために Gradle を設定した"
 :tags  ["TDD" "Java" "Gradle"]
 :layout :post
 :toc true}

Kent Beckの「テスト駆動開発」を実践するために、Eclipse使わずコマンドラインで動かす方法を調べた。

## 背景
[Kent Beck テスト駆動開発](https://www.amazon.co.jp/%E3%83%86%E3%82%B9%E3%83%88%E9%A7%86%E5%8B%95%E9%96%8B%E7%99%BA-Kent-Beck/dp/4274217884)を読みながら、実際にTDDのコーディングやろうと思って、ユニットテスト環境を設定した。

## Gradle
Javaにおける`Makefile`にあたるシステム。  
`Makefile`→`Ant`→`Maven`→`Gradle`という順番で生まれている。  
`Ant`は`Eclipse`で見たことあるし、`Gradle`は`Android Studio`で見たことあったが、何の役割なのかは気にしてなかった。  

## インストール
[Gradle公式](https://gradle.org/install/)からバイナリを落としてくるか、  
`Chocolatey`を使って`$ choco install gradle`でインストールする。  
`$ gradle --version`で動作確認。

## 手動でGradleを設定
`$ gradle init`で以下が生成される。
```bash
(project root)
|
+-- build.gradle
+-- .gitattributes
+-- .gitignore
+-- gradle
|   `-- wrapper
+-- .gradle
+-- gradlew
+-- gradlew.bat
`-- settings.gradle
```

ソースコードとテストコードはプロジェクトルートの下に`src`を作って以下のように配置する。
```bash
`-- src
    +-- main
    |   `-- java
    |       `-- (project name)
    |           `-- code.java
    `-- test
        `-- java
            `-- (project name)
                `-- test.java
```

`project name`は`settings.gradle`の`rootProject.name`に従う  
ドットがあれば逆順にディレクトリ階層作る。

## テストの準備
[ここ](https://qiita.com/niwasawa/items/cfcd37a3c2a795c336ba#buildgradle)を参考に`build.gradle`を設定。  
`dependencies`に`testImplementation 'org.junit.jupiter:junit-jupiter:5.x.x'`を設定する。
`test`に`useJUnitPratform()`を追加するの2点がキモだと思われる。

JUnitには4系と5系の情報がネットにあるが、[Kent Beck テスト駆動開発](https://www.amazon.co.jp/%E3%83%86%E3%82%B9%E3%83%88%E9%A7%86%E5%8B%95%E9%96%8B%E7%99%BA-Kent-Beck/dp/4274217884)のコードは5系のようなのでJUnit5を使う。

## テストコード
[JUnit User Guide](https://junit.org/junit5/docs/current/user-guide/)から引用。
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import example.util.Calculator;

class MyFirstJUnitJupiterTests {

    private final Calculator calculator = new Calculator();

    @Test
    void addition() {
        assertEquals(2, calculator.add(1, 1));
    }

}
```

JUnit5で使う依存関係は以下のような意味。
- `import org.junit.jupiter.api.Test;`：`@Test`を使えるようにしてくれる
- `import static org.junit.jupiter.api.Assertions.assertEquals;`：アサーション関数

## ビルドとテスト実行
`gradle build`でビルド実行。
`gradle test`でテスト実行（必要であればビルドもされる）。

## あとがき
とりあえずこれで、Emacsのshellから`$ gradle test`実行してユニットテスト実行できるようになった。  
しかし、保存時自動でテスト走らせたり、テストケースのレッドグリーンバーのEmacsフロントエンドが欲しい。  
一応[test-case-mode](https://github.com/TobiMarg/test-case-mode)というのがあってmelpaにも登録されているが、うまく動かない。  
コードみてもmeven前提っぽいけど、そもそもModeを有効にできないし、エラーメッセージも出ない。

[emacs-tdd](https://github.com/jorgenschaefer/emacs-tdd)はエラーのありなし、件数だけで、テストケース一覧とかは提供してくれないっぽい。

fly-checkみたいな標準化されてもいい気がするけど、なんでないんだろう？
