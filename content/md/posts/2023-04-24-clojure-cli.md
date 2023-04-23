{:title "Clojure CLI を使ってみる"
 :tags  ["Clojure"]
 :layout :post
 :toc true}

Clojure-CLIを使う必要が出てきたのでまとめる。

## Leiningen と Clojure-CLI
[Clojure の project.clj と deps.edn を理解する](https://fujimotok.github.io/posts/2023-01-06-clojure-project-deps/)でも書いたが、  
Clojureにおける`npm`みたいな仕組みが2種類ある。  
流れ的には`clj`のほうに移るみたいだが、日本語の書籍は`lein`の頃の情報で参入障壁が高い。


## Clojure-CLIを使う必要性
`lein`で事足りるのに`clj`使うのは、依存関係の扱いが大きく異なってくるため。

`clj`で使われる`deps.edn`は、GitHubなどを依存関係に使える。  
対して、`lein`の`project.clj`は、`clojars`や`marven`といったリポジトリから`jar`取ってくるだけ。


## Clojure-CLI のコマンドチートシート
`npm`との対比で憶えるのが自分にはしっくりくる。  
Windows環境では`clojure`がPowershell空しか使えない。

### npm init
`clj/new`のインストールが必要。  
`$ clojure -Ttools install com.github.seancorfield/clj-new '{:git/tag "v1.2.399"}' :as clj-new`

WindowsのPS環境だとエラーが出る。  
`'{:git/tag "v1.2.399"}'`を`"{:git/tag """"v1.2.399""""}"`に変えるとうまくいく。

`$ clojure -Tclj-new <command> :name <project-name>`  
- app: アプリケーションプロジェクトのテンプレート
- template: `clj/new`用のメタテンプレート
- create: 公開されているテンプレートを元に作成。指定方法は`:template <template-name>`

`-T:project/new`という組込みの機能もあるみたい。  
`$ clojure -T:project/new :template app :name domain/appname :args '["+h2"]'`

### npm install <package-name>
deps.ednに手動で追加。

- Mavenの場合  
バージョンが必須  
`{:deps {org.clojure/clojure {:mvn/version "1.11.1"}}}`
- Gitの場合。  
sha1が必須  
`:{:deps {io.github.yourname/time-lib {:git/tag "v0.0.1" :git/sha "4c4a34d"}}}`

コマンド1発でできる手段はない。

### npm install （package.jsonに記載のすべての依存関係のインストール）
`$ clojure`でREPL立ち上げるとインストールされる。

### npm uninstall <package-name>
対応するものはない。  
`deps.edn`の`:deps`から削除すればよい。  
パッケージはどこにあるかというと、`~/.m2`にある。  
容量気になるなら削除すればよい。

### npm start
`$ clojure -M -m domain.main-namespace`  
`-main`と名の付く関数が呼ばれる。

### npm run <script-name>
直接対応するものはない。
`$ clojure -M:<alias名>`で`deps.edn`の`:aliases`を起動できるがシェルスクリプトではない。  

