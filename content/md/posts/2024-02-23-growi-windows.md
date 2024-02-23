{:title "GrowiをWindowsサーバで動かす"
 :tags  ["Web"]
 :layout :post
 :toc true}

[Growi](https://github.com/weseek/growi)をWindowsサーバで動かすのにバッドノウハウが生まれたのでメモ。

## Growiとは
[Growi](https://github.com/weseek/growi)とは、オープンソースのWikiサーバ。  
Crowiをフォークしたもので、マークダウンでWikiが書きやすいようにしてくれてる。

## Windowsサーバで動かす際の壁
公式には、[Docker-Compose](https://docs.growi.org/en/admin-guide/getting-started/docker-compose.html)を使うのをおすすめしてる。  
つまり、Dockerが使えれば楽々環境構築できる。

しかし、WindowsでDocker動かすのはハードルがある。  
なぜなら、仮想化支援機能が必要であり、AWSのWindowsサーバではそれが使えない。

## DockerなしでGrowiを動かす
先駆者様がおり、ありがたいことに記事を上げてくれてる。  
[GrowiをWindowsサーバーに導入する](https://github.com/weseek/growi)

必要なことをまとめると、以下となる。
1. Nodeで動くGrowi本体のビルドと起動
2. データを置くMongoDBの起動
3. 必要ならErasticSearchを起動

## Growiのビルドが通らない
先駆者様の導入バージョンはv4.2.5。  
しかし、このバージョンが対応してる`Node 14`はサポート期限を迎えている。

Growiもメジャーバージョンが上がり、2024年2月現在の最新バージョンはv6.3.1。  
v4からv6に至るまでに、いろいろあったみたいで、ビルド方法も変わっている。  
以下は、v6.1.15での動作結果です。

v6では、[開発者スタートアップ v5](https://docs.growi.org/ja/dev/startup-v5/start-development.html)の手順でビルドできた。  
しかし、`turbo run build`すると、
```
Error: You must supply options.input to rollup
```
でいくつかのパッケージがビルドエラーとなる。


## エラーの解消
エラーの内容は`Vite`という開発ツールで、`Rollup`というバンドラの設定がおかしいという内容。  
しかし、対象のパッケージはライブラリタイプなので、`options.input`の指定は不要なはず。

色々調べて、[ここ](https://github.com/vitejs/vite/discussions/8098)にたどり着いた。  
`vite.config.ts`で使ってる`glob.sync()`が上手く動かないことがあるらしく、  
```befor.js
      entry: glob.sync(path.resolve(__dirname, 'src/**/*.ts'), {
        ignore: '**/*.spec.ts',
      }),
```
これを
```after.js
      entry: glob.sync('src/**/*.ts', {
        ignore: '**/*.spec.ts',
      }).map(file => path.resolve(__dirname, file)),
```
こうしたら、ビルドが通るようになった。

変更が必要なパッケージは4つほどあるので、全部通るまで根気よく変更してください。


## 起動時のエラー
ビルドが通って、`yarn start`でサーバ起動しようとしたら、またしてもエラー。
```
basedir=$(dirname "$(echo "$0" | sed -e 's,\\,/,g')")
          ^^^^^^^

SyntaxError: missing ) after argument list
```

これも、先駆者様がいて、[解決方法](https://takabus.com/tips/2986/)が見つかった。


`node_modules/.bin`を使ってるとだめらしい。  
中にシンボリックリンクが入っていて、Windowsでは実行できないものと思われる。

`growi/apps/app/package.json`の`node_modules/.bin`を使ってるところを書き変え。  
`growi/node_modules`にリンクしてるみたいなので、2つ上の階層を見に行くようにする。

## さいごに
PRを送れたら良いのだけど、Windows環境限定の困りごとのようだし、どうPRすれば良いかわからないので、ここに書き留めておく。
