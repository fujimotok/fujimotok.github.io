{:title "Honkit のプラグインを書いてみる"
 :tags  ["Honkit" "Gitbook" "JavaScript"]
 :layout :post
 :toc true}

Honkitのプラグインの書き方を調べてまとめた。

## Honkitとは
Gitbookと呼ばれていたものが、OSSの開発をやめて、  
プロプライエタリなサービスに移行したため、  
forkしてメンテが続けられているのがHonkit。  
基本的にOSS時代のGitbookと互換している。  

## プラグイン
npmで`gitbook-plugin-*`か`honkit-plugin-*`かで見つかるパッケージを、  
`npm instal`して、`book.json`の`plugins:[]`に足せばOK。

自分が欲しい機能として、パンくずリストをページトップに出すというのがあるが、見つからなかった。

## プラグインの作り方
[Honkit公式](https://honkit.netlify.app/plugins/create.html)に詳しい情報は書いてる。

自分がたどった手順を残しておく。
1. 適当なディレクトリで`npm init`
2. [Honkit公式 - Create and publish a plugin](https://honkit.netlify.app/plugins/create.html)に乗ってる`package.json`を真似る  
`engines`と`honkit.properties`というのがHonkit用に必要なもの。
```package.json
{
    "name": "honkit-plugin-mytest",
    "version": "0.0.1",
    "description": "This is my first HonKit plugin",
    "engines": {
        "honkit": ">1.x.x"
    },
    "honkit": {
        "properties": {
            "myConfigKey": {
                "type": "string",
                "default": "it's the default value",
                "description": "It defines my awesome config!"
            }
        }
    }
}
```
ここにかいたプロパティが`book.json`から設定できるプロパティになる。
```book.json
{
  "plugins": [
    "mytest"
  ],
  "pluginsConfig": {
    "mytest": {
      "myConfigKey": "conf"
    }
  }
}
```
3. エントリポイント（デフォルトは`index.js`）に以下を記載
```index.js
module.exports = {
    // Map of hooks
    hooks: {},

    // Map of new blocks
    blocks: {},

    // Map of new filters
    filters: {}
};
```
4. フックを追加したければ、`index.js`の`hooks:{}`に[Honkit公式 - Hooks](https://honkit.netlify.app/plugins/hooks.html)記載の名前と呼び出す関数を追加
```index.js
module.exports = {
    hooks: {
        "page": function(page) {
            page.content = page.content.replace("<b>", "<strong>")
                .replace("</b>", "</strong>");
            return page;
        }
    }
};
```
5. ブロックを追加したければ、`index.js`の`blocks:{}`に[Honkit公式 - Extend Blocks](https://honkit.netlify.app/plugins/blocks.html)記載の名前と呼び出す関数を追加
```index.js
module.exports = {
    blocks: {
        tag1: {
            process: function(block) {
                return "Hello "+block.body+", How are you?";
            }
        }
    }
};
```
6. フィルタを追加したければ、`index.js`の`filters:{}`に[Honkit公式 - Extend filters](https://honkit.netlify.app/plugins/Filters.html)記載の名前と呼び出す関数を追加
```index.js
module.exports = {
    filters: {
        hello: function(name) {
            return 'Hello '+name;
        }
    }
};
```

## フック、ブロック、フィルタとは
### フック
md→htmlに変換する前後とか様々なタイミングに割り込んで処理する。

### ブロック
markdonw内に独自のタグを用意して、md→htmlに変換するタイミングで関数によって独自にhtmlを出してあげる。
```md
{% tag1 "argument 1", "argument 2", name="Test" %}
This is the body of the block.
{% endtag1 %}
```

### フィルタ
markdown内でフォーマット変換を提供するシンボルを提供する。
```md
{{ "2022-10-10"|ISO8601 }} => 2022-10-10T00:00:00.000
```


## パンくずリスト
今回の要件は、hookの`page`のタイミングで、出来上がったhtmlの先頭にパンくずリストを表すhtmlを追加すれば良さそう

```js
/*
  test code
  
  let home = '🏠'
  let sep = '/'
  let path = ''
  let subdir = ''
  let success = false
  
  path = 'README.md'
  subdir = ''
  const breadcrumbs = createBreadCrumbs(path, subdir, home, sep)
  success = breadcrumbs === '<div><a href="/">🏠</a> /</div>'

  path = 'test.md'
  subdir = ''
  const breadcrumbs = createBreadCrumbs(path, subdir, home, sep)
  success = breadcrumbs === '<div><a href="/">🏠</a> /</div>'

  path = 'test/README.md'
  subdir = ''
  const breadcrumbs = createBreadCrumbs(path, subdir, home, sep)
  success = breadcrumbs === '<div><a href="/">🏠</a> / <a href="/test/">test</a> /</div>'

  path = 'test/test.md'
  subdir = ''
  const breadcrumbs = createBreadCrumbs(path, subdir, home, sep)
  success = breadcrumbs === '<div><a href="/">🏠</a> / <a href="/test/">test</a> /</div>'

  path = 'README.md'
  subdir = 'subdir'
  const breadcrumbs = createBreadCrumbs(path, subdir, home, sep)
  let success = breadcrumbs === '<div><a href="/subdir/">🏠</a> /</div>'

  path = 'test.md'
  subdir = 'subdir'
  const breadcrumbs = createBreadCrumbs(path, subdir, home, sep)
  success = breadcrumbs === '<div><a href="/subdir/">🏠</a> /</div>'

  path = 'test/README.md'
  subdir = 'subdir'
  const breadcrumbs = createBreadCrumbs(path, subdir, home, sep)
  success = breadcrumbs === '<div><a href="/subdir/">🏠</a> / <a href="/subdir/test/">test</a> /</div>'

  path = 'test/test.md'
  subdir = 'subdir'
  const breadcrumbs = createBreadCrumbs(path, subdir, home, sep)
  success = breadcrumbs === '<div><a href="/subdir/">🏠</a> / <a href="/subdir/test/">test</a> /</div>'
*/

/*
  path:   not starts with '/' and not ends with '/'
  subdir: starts with '/' and ends with '/'
  home:   home icon character
  sep:    separator character
 */
const createBreadCrumbs = (path, subdir, home, sep) => {
  let breadcrumbs = '<div>'

  breadcrumbs += `<a href="${subdir}">${home}</a> ${sep}`
  
  const dirs = path.split('/').slice(0, -1)
  let url = subdir
  for (const dir of dirs) {
    url += subdir + dir + '/'
    breadcrumbs += ` <a href="${url}">${dir}</a> ${sep}`
  }
  
  breadcrumbs += '</div>'
  return breadcrumbs
}

const normalizePath = (path) => {
  return path.replace(/[\/]+/g, '/').replace(/^\//, '').replace(/\/$/, '')
}

const normalizeSubdir = (subdir) => {
  const midpath = subdir.replace(/[\/]+/g, '/').replace(/^\//, '').replace(/\/$/, '')
  return midpath === '' ? '/' : '/' + midpath + '/'
}

module.exports = {
  hooks: {
    'page': function(page) {
      const config = this.config.get('pluginsConfig.breadcrumbs', {})
      
      const path = normalizePath(page.path)
      const subdir = normalizeSubdir(config.subdir || '')

      const home = config.home || '🏠'
      const sep = config.sep || '/'

      page.content =  createBreadCrumbs(path, subdir, home, sep) + page.content;
      return page;
    }
  },
};
```

## プラグインのデバッグ
プラグイン側のルートディレクトリで`npm link`。  
Honkit側のルートディレクトリで`npm link <プラグインのパッケージ名>`  
とやると、更新のたびに`npm install`しなくて済む。  

初めて知った 。 
これ、`npm link`したら、次の`npm link <プラグインのパッケージ名>`でリンクできるパッケージのリストに追加するって仕組みみたい。
