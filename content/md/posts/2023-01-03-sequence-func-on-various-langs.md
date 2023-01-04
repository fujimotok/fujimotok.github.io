{:title "いろんな言語でのシーケンス操作"
 :layout :post
 :tags  ["FP" "Clojure" "JavaScript" "C#" "Python"]
 :toc true}

# 背景
map, filter, reduce とか使いたいと思ったときに、言語ごとに違うので自分用にまとめたい

# map
`map fn seq`  
シーケンスの1つ1つに関数を適用した結果のシーケンスを返す  
例) `[1, 2, 3, 4] = inc => [2, 3, 4, 5]`

- Clojure  
```clojure
(def s [1 2 3 4])
(def t (map #(inc %) s))
(println t) => (2, 3, 4, 5)
```

- JavaScript  
```javascript
let s = [1, 2, 3, 4]
let t = s.map(x => x + 1)
console.log(t) => [2, 3, 4, 5]
```

- C#  
`map`じゃなくて`Select`

```csharp
using System;
using System.Collections.Generic;
using System.Linq;

public class Hello {
    public static void Main(){
        var s = new List<int>(){ 1, 2, 3, 4 };
        var t = s.Select(x => x + 1);
        System.Console.WriteLine(string.Join(",", t)); => 2,3,4,5
    }
}
```

- Python  
遅延シーケンスが返ってくるので`list()`で実体化が必要なのに注意

```python
s = [1, 2, 3, 4]
t = map(lambda x: x + 1, s)
print(list(t)) => [2, 3, 4, 5]
```

- ELisp  
`t`はtrueの意味で予約されている  
`map`ではなく`mapcar`なのはなんで？  
リスト返さない副作用だけのための`mapc`もある  
`seq-map`というのもある  
後から出てくる`seq-`族に合わせて再定義されているものと思われる

```elisp
(setq s '(1 2 3 4))
(setq l (mapcar '1+ s))
(print l) => (2, 3, 4, 5)
```

# filter
`filter fn seq`  
シーケンスの1つ1つに関数を適用した結果trueのものに絞ったシーケンスを返す  
例) `[1, 2, 3, 4] = even => [2, 4]`

- Clojure  
```clojure
(def s [1 2 3 4])
(def t (map #(even? %) s))
(println t) => (2, 4)
```

- JavaScript  
```javascript
let s = [1, 2, 3, 4]
let t = s.filter(x => x % 2 == 0)
console.log(t) => [2, 4]
```

- C#  
`fliter`じゃなくて`Where`

```csharp
using System;
using System.Collections.Generic;
using System.Linq;

public class Hello {
    public static void Main(){
        var s = new List<int>(){ 1, 2, 3, 4 };
        var t = s.Where(x => x % 2 == 0);
        System.Console.WriteLine(string.Join(",", t)); => 2,4
    }
}
```

- Python  
遅延シーケンスが返ってくるので`list()`で実体化が必要なのに注意

```python
s = [1, 2, 3, 4]
t = filter(lambda x: x % 2 == 0, s)
print(list(t)) => [2, 4]
```

- ELisp  
`seq-filter`という名前になっている

```elisp
(setq s '(1 2 3 4))
(setq l (seq-filter (lambda (x) (= (% x 2) 0)) s))
(print l) => (2 4)
```

# reduce
`reduce fn seq`  
シーケンスを前から順番に取り出したものと、前回の結果を引数にとって関数を呼び、１つの結果を返す  
例) `[1, 2, 3, 4] = + => 10`  
![reduce.svg](img/reduce.png)

- Clojure  
```clojure
(def s [1 2 3 4])
(def t (reduce + s))
(println t) => 10
```

- JavaScript  
```javascript
let s = [1, 2, 3, 4]
let t = s.reduce((a, b) => a + b)
console.log(t) => 10
```

- C#  
`reduce`じゃなくて`Aggregate`

```csharp
using System;
using System.Collections.Generic;
using System.Linq;

public class Hello {
    public static void Main(){
        var s = new List<int>(){ 1, 2, 3, 4 };
        var t = s.Aggregate((a, b) => a + b);
        System.Console.WriteLine(string.Join(",", t)); => 10
    }
}
```

- Python  
`reduce`は別途インポートが必要

```python
from functools import reduce

s = [1, 2, 3, 4]
t = reduce(lambda a, b: a + b, s)
print(t) => 10
```

- ELisp  
`seq-reduce`という名前になっている  
initalValueが省略できない

```elisp
(setq s '(1 2 3 4))
(setq l (seq-reduce '+ s 0))
(print l) => 10
```

# find
- Clojure  
組込みでは用意されていないので`filter`と`first`の組み合わせ  
マッチしなかったときは`nil`になる  
例外とかじゃないから後処理しやすい😁

```clojure
(def s [1 2 3 4])
(def t (first (filter #(= % 1) s)))
(println t) => 1
```

- JavaScript  
```javascript
let s = [1, 2, 3, 4]
let t = s.find(x => x == 1)
console.log(t) => 1
```

- C#  
`First`と`FirstOrDefault`がある  
どちらも関数渡すと最初にマッチしたものを返す  
`FirstOrDefault`はマッチしなかったときに例外にならず、その型のDefault値を返す

```csharp
using System;
using System.Collections.Generic;
using System.Linq;

public class Hello {
    public static void Main(){
        var s = new List<int>(){ 1, 2, 3, 4 };
        var t = s.FirstOrDefault(x => x == 1);
        System.Console.WriteLine(t); => 1
    }
}
```

- Python  
組込みでは用意されていないので`filter`とインデックスアクセスする  
ただし、マッチしなかったとき例外になるので、その辺のチェックが必要

```python
s = [1, 2, 3, 4]
t = list(filter(lambda x: x == 1, s))[0]
print(t) => 1
```

- ELisp  
`seq-find`という名前になっている

```elisp
(setq s '(1 2 3 4))
(setq l (seq-find (lambda (x) (= x 1)) s))
(print l)
```

# every
- Clojure  
```clojure
(def s [1 2 3 4])
(def t (every? #(< 0 %) s))
(println t) => true
```

- JavaScript  
```javascript
let s = [1, 2, 3, 4]
let t = s.every(x => 0 < x)
console.log(t)
```

- C#  
`every`じゃなくて`All`

```csharp
using System;
using System.Collections.Generic;
using System.Linq;

public class Hello {
    public static void Main(){
        var s = new List<int>(){ 1, 2, 3, 4 };
        var t = s.All(x => 0 < x);
        System.Console.WriteLine(t); => true
    }
}
```

- Python  
関数は渡せないので事前にbooleanのシーケンスに変換が必要

```python
s = [1, 2, 3, 4]
t = all(map(lambda x: 0 < x, s))
print(t) => true
```

リスト内包表記を使うのが一般的みたい（読みにくくて個人的には嫌いだけど）
```python
s = [1, 2, 3, 4]
t = all(0 < x for x in s)
print(t) => true
```

- ELisp  
`seq-every-p`という名前になっている  
`-p`がついてるのはpredicate述語の略で、Clojureだと`?`を付ける

```elisp
((setq s '(1 2 3 4))
(setq l (seq-every-p (lambda (x) (< 0 x)) s))
(print l)
```

# some
- Clojure  
`some`はちょっと特殊で、?がついてないので、trueでないときに`nil`が返ってくる  
ちゃんとbool値で知りたかったら、`(boolean x)`をかませる必要がある

```clojure
(def s [1 2 3 4])
(def t (boolean (some #(< 0 %) s)))
(println t) => true
```

- JavaScript  
```javascript
let s = [1, 2, 3, 4]
let t = s.some(x => 0 < x)
console.log(t) => true
```

- C#  
`some`じゃなくて`Any`

```csharp
using System;
using System.Collections.Generic;
using System.Linq;

public class Hello {
    public static void Main(){
        var s = new List<int>(){ 1, 2, 3, 4 };
        var t = s.Any(x => 0 < x);
        System.Console.WriteLine(t);  => true
    }
}
```

- Python  
関数は渡せないので事前にbooleanのシーケンスに変換が必要

```python
s = [1, 2, 3, 4]
t = any(map(lambda x: 0 < x, s))
print(t) => true
```

リスト内包表記を使うのが一般的みたい（読みにくくて個人的には嫌いだけど）
```python
s = [1, 2, 3, 4]
t = any(0 < x for x in s)
print(t) => true
```

- ELisp  
`seq-some`という名前になっている  
`-p`がついていないのは、戻り値が条件の関数の戻り値に依存するため  
詳しく言うとnon-nilな値が返ってきた時点で探索やめて値を返す

```elisp
(setq s '(1 2 3 4))
(setq l  (seq-some (lambda (x) (< 1 x)) s))
(print l) => t
```

# あとがき
言語ごとに呼び方違ったりして検索がいつも戸惑う  
なんだかんだJavaScriptの呼び名に慣れちゃってる感があるのでJavaScriptの呼び名に対応で書いた  

動作確認には[piza.io](https://paiza.io/ja)が便利だった  

Clojureのシーケンス操作も迷うのでチートシート作りたい  
