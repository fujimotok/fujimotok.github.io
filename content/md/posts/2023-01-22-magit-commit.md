{:title "Magit で commit するときに diff のウィンドウが出るのを止める"
 :tags ["Emacs"]
 :layout :post
 :toc true}

Magitでcommitするときにコミットメッセージのウィンドウに加えて、diffのウィンドウが出てきて鬱陶しい。  
diffのウィンドウの表示を抑制しつつ、コミットメッセージ側にdiffの内容を表示する方法を編み出した。

## diffのウィンドウの表示の抑制
あのウィンドウを出してる関数は`magit-commit-diff`。  
こいつが`server-switch-hook`と`with-editor-filter-visit-hook`でフックされている。  
この2つをMagitのモードフックにて登録解除することで表示を抑制できる。
```lisp
(leaf magit
  :ensure t
  :hook (magit-mode-hook . (lambda ()
                             (remove-hook 'server-switch-hook 'magit-commit-diff)
                             (remove-hook 'with-editor-filter-visit-hook 'magit-commit-diff))))
```


## コミットメッセージへのdiffの内容の表示
コミットメッセージに追記されている情報は`git`の仕組みで表示されている。  
`$ git commit -v`というオプションでコミットメッセージにdiffの内容が表示される。  
これをするためにEmacsから`-v`オプションを設定するには、Magitで`c`押したあとに`-v`を打てばいい。

しかし、これを毎回やるのは面倒。  
どうしたもんかと調べていたら、Magitの出してるメニュー（transientと呼ぶ）における選択の永続化ができると分かった。  
詳細は[ここ](https://magit.vc/manual/transient/Saving-Values.html)を参照。  

コミットのメニューを出してる状態で`C-x C-s`と押せば永続化できる。  
Emacs再起動しても永続化されていた。


## あとがき
頑張って調べれば解決策が見つかるのはEmacsのいいところ。  
逆に言うと、バッドノウハウばかりとも言える。

init.elってバッドノウハウの集まりな感じもしてきた。  
ちゃんと理由をドキュメント化しないといけないかな。  
きれいなinit.elを目指したい。

init.elにgifとか埋めて、htmlに吐き出せたら、設定と動作カタログになってうれしいのでは。  
orgからinit.el生成するという逆のアプローチはよくあるんだけど。
