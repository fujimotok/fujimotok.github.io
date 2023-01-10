{:title "GitHub Pages のユーザーサイトを GitHub Actions でデプロイした"
 :tags  ["GitHub Pages"]
 :layout :post
 :toc true}

GitHub Pagesのユーザーサイトで、Cryogenで作ったブログを公開した。  
2022年4月からはGitHub Actions使って生成物をコミットせずにGitHub Pagesへデプロイするのが主流なので、そのやり方を試した。

## ユーザーサイトとは
公開URLが`https://<ユーザー名>.github.io/`のものをユーザーサイトという。  
公開URLが`https://<ユーザー名>.github.io/<リポジトリ名>`のものはプロジェクトサイトという。

## ユーザーサイトとして公開するには
1. リポジトリ名を`<ユーザー名>.github.io`でリポジトリ作成
2. アクションを作る

アクションの中身は以下の流れ。
1. ビルドに必要な環境を準備
2. ビルドの実行
3. `actions/upload-pages-artifact@v1`で生成物をGitHub Pagesにアップロード
4. デプロイを実行し、新しい構成を有効にする

以下はCryogenを使った場合の例。
```yaml
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
    - uses: actions/checkout@v3
    - name: Install dependencies
      run: lein deps
    - name: Run build
      run: lein run
    - uses: actions/upload-pages-artifact@v1
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
        uses: actions/deploy-pages@v1
```

## あとがき
ユーザーサイトの作り方は古いやり方（docsディレクトリを使う）しか書いて無くて、  
GitHub Actionsの例には、プロジェクトサイトの例しかないのでやり方がわからなかった。  
ふたを開けたら大した手順なかった。
