{:title "Emacs 30 で tree-sitter を使う"
 :tags ["Emacs"]
 :layout :post
 :toc true}

EmacsでJSX,TSXを書くのには、tree-sitterを使うのが良いと聞いていたが、使ったことなかった。  
Emacs 29以降でビルトインになってるのでそれを使ってる。


## Emacs 29以降から tree-sitter がビルトインされている
Emacs 29以降から`package install`をしなくても良くなっている。  
ただし、namespaceが重複しないように、`treesit-*`になっている。  
パッケージのほうの`emacs-tree-sitter.el`とはAPIが若干違ってるっぽい。  

## Grammer binaries
tree-sitterは各言語の文法バイナリが無いと動かない。  
MELPAの`tree-sitter-langs`をインストールすると、ある程度の言語の文法バイナリを落としてくれる。

あとは、これを`treesit-*`が見てくれるところに置けばいいのだが、どうも設定する変数がない。  
仕方がないので、パスの通ったところに置くこととした。

加えて、MELPAの`tree-sitter-langs`で提供されるバイナリの名前と、`treesit-*`の参照する名前に差があって、そのままでは使えない。  
`tree-sitter-langs`からとれるバイナリの名前は、言語名から始まってるので、`libtree-sitter-<言語名>`に変更すると読んでくれるようになった。


## あとがき
`treesit-language-source-alist`に言語名とGithubのリポジトリURLを設定して、
`treesit-install-language-grammar`でインストールするというの方法も用意されている。
詳しくは[こちら](https://www.nathanfurnal.xyz/posts/building-tree-sitter-langs-emacs/)を参照。
しかし、実行にはCコンパイラが必要的なエラー出たので使っていない。

Scoop.shとかにもこういったパッケージなさげなので、この作業の自働化は難しそう。


Emacs 29以降ではLanguage Server Protocolのクライアントとして`eglot`がビルトインされてるらしいので、それも使ってみようと思う。
[この辺](https://syohex.hatenablog.com/entry/2022/11/08/000610)を参考にしてみる。
