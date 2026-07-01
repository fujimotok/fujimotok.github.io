{:title "Vercelのpreviewで増えるサイトデータを消す戦略"
 :tags  ["Web"]
 :layout :post
 :toc true}

Vercelを使い始めて、悩みポイントが出たのでwork-around。

## 前置き
[Vercel](https://vercel.com/)使うと、Githubと連携して、プルリクエスト毎に自動でpreview環境をデプロイしてくれる。  
[Jules](https://jules.google.com/)と組み合わせて、スマホから指示出して、スマホからpreview環境見て、マージするワークフローができる。

## preview環境のサイトデータ
preview環境はプルリクエストごとに独自のドメインURLが発行される。  
IndexedDBのデータなどサイトデータはドメインごとに持つので、プルリクエストの数だけIndexedDBを持つことになってしまう。  
いろいろ調べたが、Vercelの仕様上ドメインを固定したサブディレクトリを作ることはできない。

## 行きついた運用方法：サイトデータを手動で消す
さすがに1個1個消すのは面倒なので、手動でもまとめて消せる方法を調べた。  
PC版Edgeは  
`設定 > プライバシー/検索/サービス > Cookie > すべての Cookie とサイト データ`  
スマホ版Edgeは  
`設定 > サイトの設定 > サイトのアクセス許可 > 保存データ (一番下)`  
でサイトデータのリストが出るので、検索窓に`vercel.app`を入れて、削除を実行。

## さらにpreview環境にのみ絞る
preview環境は`*-projects.vercel.app`になってるので、`projects.vercel.app`で絞り込んで削除すれば、本番は残せる。

## 終わりに
もっと良い方法があるかもしれないが、サイトデータの消し方を忘れそうなのでメモっておく。
