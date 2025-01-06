{:title "GitHub Actions で Leiningen が未サポートになった"
 :tags  ["Clojure"]
 :layout :post
 :toc true}

ブログのデプロイしてるGitHub Actionsが動かなくなって初めて知った。

## VMイメージ ubuntu-latest がバージョンアップしてる
`runs-on: ubuntu-latest`で実行していて、ubuntu-24.04が入るようになった。  
[runnner-images #10636](https://github.com/actions/runner-images/issues/10636) にあるように、`Leiningen`は未サポートになった。

## Workflow の中で Leiningen のインストールが必要になった
幸い、ubuntuのaptパッケージでとってこれるので、[これ](https://launchpad.net/ubuntu/+source/leiningen)を使う。  

毎回`apt install`してたら、時間がかかるので、キャッシュできないか探す。

[cache-apt-packages](https://github.com/marketplace/actions/cache-apt-packages) これがスターも多くてキャッシュしてくれそう。  
`packages`に`apt install <package name>`の`<package name>`を渡す。  
`version`はキャッシュのリビジョン。これを上げるとキャッシュクリアされるっぽい。

```yml
    - uses: awalsh128/cache-apt-pkgs-action@latest
      with:
        packages: leiningen
        version: 1.0
```

## 最終的なyml
```yml
name: GitHub Pages

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]
    
  workflow_dispatch:

permissions:
  contents: read
  pages: write
  id-token: write

concurrency:
  group: "pages"
  cancel-in-progress: true

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - uses: awalsh128/cache-apt-pkgs-action@latest
      with:
        packages: leiningen
        version: 1.0
    - name: Install dependencies
      run: lein deps
    - name: Run build
      run: lein run
    - uses: actions/upload-pages-artifact@v3
      with:
        path: public/

  deploy:
    environment:
      name: github-pages
      url: ${{ steps.deployment.outputs.page_url }}
    runs-on: ubuntu-latest
    needs: build
    steps:
      - name: Deploy GitHub Pages site
        id: deployment
        uses: actions/deploy-pages@v4
```

## 終わりに
GitHub Actions の ubuntu-24.04 には`clojre cli`もないから、`leiningen`→`clojure cli`へ移行の流れではない。  
clojureが簡単に使える環境がなくなったのが残念。



