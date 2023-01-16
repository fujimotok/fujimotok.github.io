{:title "HTML+CSS と XAML の対応"
 :tags ["HTML" "CSS" "XAML"]
 :layout :post
 :toc true}

仕事ではWPF（XAML）でUIを書くので、HTMLを書くときはレイアウトの仕方で困る。  
XAMLであのパターンはどうするかで対応をまとめる。

## Grid
XAMLでレイアウトするなら必ずお世話になるコンテナ。

HTMLの世界だと`Flexbox`を使ってレイアウトする。  
これを理解しないといけない。  
と思ったら、2017年から`CSS Grid`ができてた。

## 例題
メニューバー、サイドバー、メイン、ステータスバーで構成されるレイアウトを考えてみる。
```text
____________
| メニュー |
|__________|
|サ|メ     |
|イ|  イ   |
|ド|    ン |
|__|_______|
|ステータス|
|__________|
```

## XAML
```xml
<Window x:Class="TemplateTest.MainWindow"
        xmlns="http://schemas.microsoft.com/winfx/2006/xaml/presentation"
        xmlns:x="http://schemas.microsoft.com/winfx/2006/xaml"
        xmlns:d="http://schemas.microsoft.com/expression/blend/2008"
        xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006"
        mc:Ignorable="d"
        Title="MainWindow" Height="450" Width="800">
    <Grid>
        <Grid.RowDefinitions>
            <RowDefinition Height="30"/>
            <RowDefinition Height="*"/>
            <RowDefinition Height="30"/>
        </Grid.RowDefinitions>
        <Grid.ColumnDefinitions>
            <ColumnDefinition Width="100"/>
            <ColumnDefinition Width="*"/>
        </Grid.ColumnDefinitions>
        <Border Grid.Row="0" Grid.Column="0" Grid.ColumnSpan="2" Background="red"/>
        <Border Grid.Row="1" Grid.Column="0" Background="Yellow"/>
        <Border Grid.Row="1" Grid.Column="1" Background="Green"/>
        <Border Grid.Row="2" Grid.Column="0" Grid.ColumnSpan="2" Background="Blue"/>
    </Grid>
</Window>
```

## CSS Grid
Gridコンテナになるものへ、`grid-template-rows`と`grid-template-colmns`を指定する。  
指定の仕方は、スペース区切りで行や列の数だけpxかfr（スター*みたいなもの）を指定。

最後に中の要素で、`grid-row`と`grid-colmun`を指定する。  
指定の仕方はXAMLと違い1スタートで、`span`指定にはスラッシュで区切る。

ちょっと記法が違うだけで、レイアウト組みの指定はXAMLに近くて理解しやすい。

```html
<div style="display: grid; width: 100%; height: 100vh; grid-template-rows: 30px 1fr 30px; grid-template-columns: 100px 1fr;">
  <div style="grid-row: 1; grid-column: 1 / span 2; background: #FF0000;">menu</div>
  <div style="grid-row: 2; grid-column: 1; background: #FFFF00;">side</div>
  <div style="grid-row: 2; grid-column: 2; background: #00FF00;">main</div>
  <div style="grid-row: 3; grid-column: 1 / span 2; background: #0000FF;">status</div>
</div>
```

## Flexboxの場合
まず縦に並べるコンテナとして、`display: box`の`div`使う。  
`div`はデフォルトで`display: box`。

次に横に並べるコンテナとして、`display: flex`の`div`使う。

最後に、中の要素で`flex: 1`とかで幅の比率を指定する。  
固定幅の要素と`flex: 1`の組み合わせなら、`flex: 1`は残りの部分を埋める。  
これは、比率で計算される部分が、固定幅引いた分で計算されるため。

縦のほうは`flex: 1`による比率は使えない。  
`height: 100%`とかは使える。  
残りを埋めるようにしたかったら、`calc()`使って、100％からの引き算で計算する。


```html
<div style="width: 100%; height: 100vh;">
    <div style="display: flex; height: 30px;">
      <div style="background: #FF0000; flex: 1;">menu</div>
    </div>
    
    <div style="display: flex; height: calc(100% - 60px);">
      <div style="background: #FFFF00; width: 100px;">side</div>
      <div style="background: #00FF00; flex: 1;">main</div>
    </div>
    
    <div style="display: flex; height: 30px;">
      <div style="background: #0000FF; flex: 1;">status</div>
    </div>
</div>
```

ちなみにしれっと書いてるが、ベースの要素で`height: 100vh`を指定することで、ビューポートの大きさ≒ウィンドウの大きさに合わせることができる。

とりあえず、縦に並べるときは何もしなくてよくて、横に並べるときは`display: flex`を指定する。  
横並びの時の幅の比率は、`flex: x`で指定する。  
縦並びの時の高さの比率は、`height: x %`や`height: calc(x % - x px)`で指定する。

## あとがき
Flexboxの理解が弱く、XAMLのGridレイアウトに慣れてると、こうしたいってのが実現できなくて困っていた。  
調べてみると、Gridレイアウトサポートされてたので、拍子抜けした。  
一応Flexboxでのレイアウトもまとめたが、これからはHTMLでもGridでレイアウトしたい。
