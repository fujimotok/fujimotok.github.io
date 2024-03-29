{:title "Emacsの正規表現と置換のTips"
 :tags  ["Emacs"]
 :layout :post
 :toc true}

Emacsの正規表現は一部独特なところがある。  
加えて、置換`query-replace-regexp`にも知らなかった機能があったのでまとめる。

## 正規表現
一番よく使うのに、一般的な正規表現と違う書き方になるのが‘グループ化`(pattern)`。  
Emacsではバックスラッシュ`\`でエスケープしないといけない。  
つまり、`\(pattern\)`と書く必要がある。  
エスケープしなかった場合には、カッコにマッチする。  
回数指定の`{}`にもエスケープが必要。

次に困るのが、改行コードとタブ文字。  
文字列の中では`\n`とか`\t`とか使えるが、正規表現では使えない。  
代わりに、直接入力する。  
改行コードは`C-q C-j`、タブは`C-q C-i`で入力する。

## query-replace-regexp
置換後に使える特殊な文字として、`\1`、`\2`などの数値指定がある。  
これは、正規表現に指定したグループの前から何番目かを指定する。  
これはよく使う機能なので知っていたが、他にも特殊な文字が存在する。

`\#`：0スタートの数字が入る。置換が実施されるたびにインクリメント。  
`\,`：Lisp式を指定できる。``\,(upcase \1)`とすれば対象を全大文字変換できる。

これがあれば、連番付与に役立ちそう。  
たとえば`\.(1+ \#)`にすれば、1スタートで連番付与できる。  
Lisp式が使えるのはかなりのチートに思える。
