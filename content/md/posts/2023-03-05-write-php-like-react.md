{:title "PHPをReact風に書く"
 :tags ["React" "PHP"]
 :layout :post
 :toc true}

素のPHPでReact風にコンポーネント単位で実装したいと考えて、方法を編み出してみた。

## JavaScritpでふつうに書くと
List.js
```javascript: List.js
import React from 'react'

const List = props => {
  const num = props.num
  const range = [...Array(num).keys()]
  
  return (
    <ul>
      {range.map(i => <li key={i}>{i}</li>)}
    </ul>
  )
}

export default List
```

App.js
```javascript: App.js
import React from 'react'
import { List } from 'List'

const App = () => {
    return (
    <div>
      <List num={10}/>
    </div>
    )
}

export default App
```

## コンポーネントの定義
PHPでもラムダ関数があるのでそれを使う。  
ラムダ関数をリターンまたは、ラムダ関数を受け取った変数をリターンすることで、インポート側で受け取れる。

Hello.php
```php: Hello.php
<?php
$func = function () {
  return '<h1>Hello World!<h1>'
};

return $func;
?>
```

index.php
```php: index.php
<?php
$func = require 'Hello.php';

// PHPのラムダ関数は外部変数をキャプチャしたい場合にはuseを使う
$app = function () use ($func){
  // 呼び出しは変数名に()付ける
  return $func();
}

// render()の代わりにecho
echo $app();
?>
```

## JSXの表現
ヒアドキュメント+変数展開を使う。  
関数も変数に代入することで利用できる。  

index.php
```php: index.php
<?php
$func = require 'Hello.php';

// PHPのラムダ関数は外部変数をキャプチャしたい場合にはuseを使う
$app = function () use ($func) {
  // return () のカッコの代わりにヒアドキュメントを使う
  return <<<EOS
    <div>
      {$func()}
    </div>
EOS;
}

// render()の代わりにecho
echo $app();
?>
```

ヒアドキュメントの変数展開では式は利用できないので、  
JSXのようにifとかmapとか使うのはできない。  
そういうのは、関数内でやってもらう。

## プロパティの定義
関数の引数をオブジェクトにすればいい。

プロパティにアクセスする側は、アロー演算子`$obj->key`を使う。
```php: Hello.php
<?php
$func = function ($props) {
  return "<h1>Hello {$props->name}<h1>"
};

return $func;
?>
```

プロパティをセットする側は、アロー演算子`(object)["key" => "val",...]`を使う。  
残念ながらJSXのプロパティの指定の様には出来ない。
```php: index.php
<?php
$func = require 'Hello.php';

$app = function () use ($func){
  return <<<EOS
    <div>
      {$func((object)["num" => 10])}
    </div>
EOS;
}

// render()の代わりにecho
echo $app();
?>
```

## 最初のJavaScriptの例をPHPに置き換えた結果
List.php
```php: List.php
<?php

$List = function ($props) {
  $out = '';
  for ($i=0; $i < $props->num; $i++){
      $li = `<li>{$i}</li>`
      $out = $out . $li;
  }
  return '<ul>' . $out . '</ul>';
};

return $List;
?>
```

index.php
```php: index.php
<?php
$List = require 'List.php';

$app = function () use ($List) {
  return <<<EOS
    <div>
      {$List((object)["num" => 10])}
    </div>
EOS;
};

// render()の代わりにecho
echo $app();
?>
```

## あとがき
childが残っているが、xml構造をパースしないといけないから簡単にはできないと思われる。  
関数にchildを受け取るように作れば、それっぽくはできそうだが、ヒアドキュメントの中にヒアドキュメントを置けるかが未検証。

あと、useStateとかuseEffectとかがないが、PHPは普通、静的なHTMLを返す前提だからあんまり困らないか。  
やろうとすれば、JavaScriptを埋め込んでDOM操作とかFetchとかを埋め込むとかか。
