{:title "Npm パッケージを初めて公開した"
 :layout :post
 :tags  ["JavaScript"]
 :toc true}

# 背景
[前の投稿](posts/2023-01-05-honkit-plugin/)で出来たプラグインを公開した  
その際にやったことを記録しておく

# Npm のアカウント作る
Npmにパッケージを登録するにはアカウントが必要  
`$ npm adduser`で作った  
名前とパスワード、メールアドレスだけが必要  
このメールアドレスが面倒で、公開されてしまう  

# OSSコントリビューション用のメールアドレス作る
この際なので、OSSコントリビューション用のメールアドレスを作った  
普段使いのメールアドレスだと、情報漏れるの怖くて別にしたかった  
Gitのコミット用のメールアドレスもこれにすればよかったが、面倒だったので`github.noreply`のにした

# npm コマンドの公開関係のサブコマンド
`$ npm login` で、アカウントにログインした状態にする  
`$ npm publish` で、npmのリポジトリに公開する  
`$ npm unpublish` で、公開を取り消す（72時間以内でないとだめ）  
`$ npm logout` で、ログアウトする  

# ユニットテスト書いてコードきれいにしておく
`Jest`を初めて使った  
他の人の見ると`__test__`みたいなディレクトリに入れてるが  
とりあえず、チュートリアル通り、`***.test.js`で作った  

モジュールから公開しない関数をテストしたかったので`rewire`を使った  
defaultじゃないexportモジュールでもよかったのかもしれない  
あんまりexport周りの仕様を理解できていない

# package.json をきれいにする
公開にあたっては、この辺の情報をちゃんとしたものにしとかないとまずい  
なかったら公開できないのかはわからないが、  
npmのページで見た時にgithubとかにリンクしなくて使いづらくなると思われる  
```json
{
  "name": "",
  "version": "",
  "description": "",
  "author": "",
  "license": "MIT",
  "keywords": [  ],
  "repository": {
    "type": "git",
    "url": "git+https://github.com/user/project.git"
  },
  "bugs": {
    "url": "https://github.com/user/project/issues"
  },
  "homepage": "https://github.com/user/project",
}
```

# README.md をきれいにする
npmのページでも表示されるので、ここに使い方とか書いてないと採用されにくくなると思われる

# あとがき
最近、Kent Beckの「テスト駆動開発」読んでるので、JSでのテストの練習出来て良かった

公開作業って初めての時は緊張する

公開したものは[こちら](https://www.npmjs.com/package/honkit-plugin-breadcrumbs)

Emacsのmeplaパッケージとか、Clojureのライブラリとかの公開も挑戦してみたい  
まだ出せるモノがないが
