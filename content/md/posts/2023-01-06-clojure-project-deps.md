{:title "Clojure の project.clj と deps.edn を理解する"
 :tags  ["Clojure"]
 :layout :post
 :toc true}

Clojureのプロジェクト構成管理周りのしくみの理解があいまいだったので、調べてまとめた。

## project.clj？ deps.edn？
`cryogen`とか`shadow cljs`とかのプロジェクト生成のときは、README.mdの通りにコマンド打っただけで、  
`project.clj`とか`deps.edn`のことがわかってなかった。

依存関係を追加するときに迷ったので、整理する・  
あと、`lein **`も忘れるので整理する。

## Leiningen と Clojure-CLI
`node.js`の`npm`、`ruby`の`gem`、`python`の`pip`、`perl`の`cpan`に相当するもの。  
`Leiningen`の方が古くからあり、`Leiningen`の動き自体を拡張する仕組みもあるので、いろんな機能を持つ。  
対して`Clojure-CLI`はシンプルな単機能を持つ複数のCLIツールで目的を達成しようとする。  

| ツール      | 設定ファイル | 備考                                                 |
|-------------|--------------|------------------------------------------------------|
| Leiningen   | project.clj  | JVMの設定とか、scriptingとかいろんな情報が入っている |
| Clojure-CLI | deps.edn     | 本当に依存関係だけの情報                             |

ちなみに`.edn`はclojure用の`.json`みたいなもんらしい。  
設定ファイルに使われるのも納得。

## Leiningen のコマンドチートシート
`npm`との対比で憶えるのが自分にはしっくりくる。

### npm init
`$ lein new <template-name> <project-name>`  
`create-xx-app`のほうが近いか。  
ネットからテンプレート探してきてそれに基づいて、ディレクトリ構成とか依存関係の情報を生成してくれる。  
以下はオフラインでも利用可能。  
- default: ライブラリのためのテンプレート
- plugin: Leiningenのプラグインを作るためのテンプレート
- app: アプリケーションプロジェクトのテンプレート
- template: `lein new`用のメタテンプレート

### npm install <package-name>
`$ lein search`  
で検索して、`project.clj`の`:dependencies`に手で追加。  
`$ lein deps` でインストール。

コマンド1発でできる手段はない。

### npm install （package.jsonに記載のすべての依存関係のインストール）
`$ lein deps`

### npm uninstall <package-name>
対応するものはない。  
`project.clj`の`:dependencies`から削除すればよい。  
パッケージはどこにあるかというと、`~/.m2`にある。  
容量気になるなら削除すればよい。

### npm start
`$ lein run`  
エントリポイントは、`project.clj`の`:main`に書かれている。  

### npm run <script-name>
直接対応するものはない。
`$ lein <aliase-name>`で`project.clj`の`:aliases`を起動できるがシェルスクリプトではない。  
プラグインを入れるとかで対応はできると思われる。

### npm publish
`$ lein publish`で`Clojars`リポジトリに登録できる。  
怖い。

## あとがき
[cljdoc](https://cljdoc.org/)すごい。  
Gitリポジトリソースあればドキュメントが生成できる。  
他の言語ではこういうの見たことない。  
と思って調べたら`cpan`でもドキュメントホスティングしてる。  

`javadoc`とか`rdoc`とかは組込みで、`dir`と`doc`があるから十分。
```clojure
(require 'clojure.repl)
(dir sample.core)
(doc sample.core/main)
```
