{:title "aiderの使い勝手向上のための設定"
 :tags  ["aider"]
 :layout :post
 :toc true}

watch-filesモードやmultilineモードを設定すると便利

## aiderのデフォルトoff機能
`watch-files`:ファイルの変更を検知して、AIメンションコメントを処理する。

`multiline`:Enterで送信しない。`/edit`の設定が不要になるくらい、このモードは神。

`chat-mode`:デフォルトは`code`モードで編集してしまう。設定で`ask`で起動すると便利。

## watch-files
起動引数: `--watch-files`  
config.yml: `watch-files: true`

どんな動きするかは以下参照  
[Aider in your IDE](https://aider.chat/docs/usage/watch.html)


## multiline
起動引数: `--multiline`  
config.yml: `multiline: true`

Enterで送信しない。改行になる。  
送信したいときは`Meta-Enter`。Window環境だと`Esc-Enter`。

もともと、改行したかったら`Meta-Enter`でできて、それを入れ替える機能。

日本語だと変換の確定でEnter押しまくって、間違って送信になってしまうので、  
デフォルトでmultilineモードが絶対便利！

複数行入れたかったら`{`から始めるという方法もあるみたいだけど、  
上記理由からも、multilineモードのほうが誤爆がなくていい。  
バックスペースで改行消すこともできる。

難点は、Enterで送信したつもりになってしまう。  
慣れるしかないか。

詳細は以下参照  
[Entering multi-line chat messages](https://aider.chat/docs/usage/commands.html#entering-multi-line-chat-messages)

## chat-mode
起動引数: `--chat-mode <code/ask/archtect>`  
config.yml: `chat-mode: ask`

デフォルトをaskモードにしておけば、編集勝手にしない。
修正方針固まってから、`/code 適用して`とcodeモードで実行するのが誤爆がなくて良い。

config.ymlの設定の仕方が、公式にはなかったが、ちゃんと反映された。

詳細は以下参照  
[Chat modes](https://aider.chat/docs/usage/modes.html)


## まとめ
Aiderの公式ドキュメントはそろってるが、使い勝手良くするベストプラクティス的な情報があんまり出回ってないので、備忘録残す必要がある。
