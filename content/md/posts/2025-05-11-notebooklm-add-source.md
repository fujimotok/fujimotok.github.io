{:title "NotebookLMのソース追加を自動化する"
 :tags  ["Javascript"]
 :layout :post
 :toc true}

NotebookLMのソース追加がURL1件ずつしか登録できなかったので自動化した

## コード
<script src="https://gist.github.com/fujimotok/d9bf2c9b6732f43318c85e79a0092b0d.js"></script>

## 使い方
F12でブラウザのデバッガを開いてコンソールにコピペして実行

## 仕組みと注意
WebAPIのリバースエンジニアリングではなく、DOM操作をJavascriptで行っているだけ。  
なので、動作中は画面が動きまわるので、操作不可です。  
NotebookLMの画面構成が変わると使えなくなる可能性が高いです。


