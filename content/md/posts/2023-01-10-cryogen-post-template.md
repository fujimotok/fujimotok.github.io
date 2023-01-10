{:title "Cryogenの投稿の書き方"
 :tags ["Cryogen"]
 :layout :post
 :toc true}

Cryogen使ってブログ記事書くときの注意点をまとめた。  
最初のパラグラフがdiscriptionに入るので重要。  
見出しレベルはh2を使う。

## ページの最初のパラグラフ
RSSの`discription`やメタデータの`discription`に使われる。  
最初の行から、`config.edn`の`:description-include-elements #{:p}`で指定されたタグの終わりまでが対象。  
デフォルトで全部の見出しレベルが指定されていて、2つ目の見出しの内容までが表示されて見づらいので、パラグラフだけを対象にした。

## 見出しレベル
タイトルは自動でh1になってる。  
なので見出しレベルはh2が良いと思われる。  
GoogleのSEO的にもh1多すぎるとダメとかいう記事見た。

これに合わせて自分のEmacsの設定を変えた。  
Markdownを開いたときに、自動で見出しレベルがh1だけ表示されるように折りたたみ状態にしていたが、  
見出しレベルh1が存在しないときに、何も表示されなくなる問題があった。  
回避策として以下のように修正した。2を指定するのがみそ。
```lisp
(leaf markdown-mode
  :doc "markdown用設定"
  :hook ((markdown-mode-hook . (lambda nil (outline-hide-sublevels 2)))))
```

## textlintを導入
文章の表現の正確性を確保するため、`textlint`を入れることにした。  
EmacsのMarkdown-modeでflycheckすると`textlint`が自動で使われる設定になっていた。  
必要なパッケージを`npm install`で解決した。

Cryogenメタデータと`textlint`のルールがバッティングするので、`textlint-filter-rule-allowlist`を使う。  
文頭の`{`から文末の`}`までをルールの例外にした。
```json
{
  "filters": {
    "allowlist": {
      "allow": [
        "/^{[\\s\\S]*?}$/m"
      ]
    }
  },
  "rules": {
    "preset-ja-spacing": true,
    "preset-ja-technical-writing": true,
    "spellcheck-tech-word": true
  }
}
```

## あとがき
スタイルが確立できてすっきりした。  
あと気になっているのは、新規ポスト作る手順を簡略化したいというのと、  
リンク貼る手順を簡略化したい。  
EmacsLispで作るか。
