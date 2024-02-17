{:title "Emacsで合字を使う設定"
 :tags  ["Emacs"]
 :layout :post
 :toc true}

Emacsで合字（リガチャ）を使うための設定ではまったのでメモ


## 合字（リガチャ）とは
`==`とか`!=`とかが特定の組み合わせの文字が別の文字で表示される仕組み。

## Emacsは合字（リガチャ）に対応しているか？
27.1から対応しているらしいが、フォントを入れるだけでは自分の環境（Scoop の Emacs29.1）では動かなかった。　
入れたフォントは[UDEV Gothic](https://github.com/yuru7/udev-gothic)。
Emacsには`(set-frame-font "UDEV Gothic NFLG-11.5")`で設定した。

## 合字（リガチャ）を有効にするには
`mickeynp/ligature.el`を使う。  
`ligature-set-ligatures`で合字として表示したい文字のセットを設定する。  
`ligature-generate-ligatures`を呼び出すことで、合字が表示される。  
`xxx-mode-hook`に`ligature-generate-ligatures`を設定しておけば、呼び出す手間がなくなる。

```
(leaf ligature
  :el-get "mickeynp/ligature.el"
  :hook ((prog-mode-hook . ligature-generate-ligatures))
  :config
  (global-ligature-mode 't)
  ;; Enable ligatures in programming modes
  (ligature-set-ligatures 'prog-mode '("www" "**" "***" "**/" "*>" "*/" "\\\\" "\\\\\\" "{-" "::"
                                     ":::" ":=" "!!" "!=" "!==" "-}" "----" "-->" "->" "->>"
                                     "-<" "-<<" "-~" "#{" "#[" "##" "###" "####" "#(" "#?" "#_"
                                     "#_(" ".-" ".=" ".." "..<" "..." "?=" "??" ";;" "/*" "/**"
                                     "/=" "/==" "/>" "//" "///" "&&" "||" "||=" "|=" "|>" "^=" "$>"
                                     "++" "+++" "+>" "=:=" "==" "===" "==>" "=>" "=>>" "<="
                                     "=<<" "=/=" ">-" ">=" ">=>" ">>" ">>-" ">>=" ">>>" "<*"
                                     "<*>" "<|" "<|>" "<$" "<$>" "<!--" "<-" "<--" "<->" "<+"
                                     "<+>" "<=" "<==" "<=>" "<=<" "<>" "<<" "<<-" "<<=" "<<<"
                                     "<~" "<~~" "</" "</>" "~@" "~-" "~>" "~~" "~~>" "%%"))
  )
```


## さいごに
合字使えるようにはなったが、やっぱり見慣れなくて、`ricty diminished`に戻してしまった。
