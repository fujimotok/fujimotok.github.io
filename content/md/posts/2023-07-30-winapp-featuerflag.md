{:title "Windowsアプリでフィーチャーフラグ実現するには"
 :tags  ["C#"]
 :layout :post
 :toc true}

Windowsアプリでフィーチャーフラグが欲しくなったので検討した。

## フィーチャーフラグとは
機能の有効無効を切り替える仕組み。  
Webだと動作中の機能を動的に変更してリリースする。  
ランダムにスイッチすることで、ABテストができる。  
Windowsアプリでもユーザー環境で動的に切り替えたいことがある。  
実装はC#を対象とする。

## iniファイル
古いWindowsアプリでの設定の保存方法。  
Win32APIのGetPrivateProfileString()で読み取れる。  
iniファイルはkey-valueストア的なもの。  

ユーザーに見つかりやすいので、フィーチャーフラグに使うには微妙。

## app.config
.NET時代の標準的なアプリ設定の保存方法。  
VisualStudioで[追加]>[新しい項目]→[アプリケーション構成ファイル]から作ることができる。
XMLの中にkey-valueストア的なものがある。  
```xml
<configuration>
  <appSettings>
    <add key="occupation" value="dentist"/>
  </appSettings>
</configuration>
```

アクセスするには、`ConfigurationManager`で呼び出せる。  
```cs
string occupation = ConfigurationManager.AppSettings["occupation"];
```

こちらもユーザーに見つかりやすいので、フィーチャーフラグに使うには微妙。

## コンパイルスイッチ
動的変更できないので対象外。

## 独自ファイル
ファイルパスの管理や、読み取りの実装が面倒。

## レジストリ
古くからある方法。  
一般のユーザーが目にすることないので、フィーチャーフラグの置き場的に良さそう。  
C#ならアクセスの仕方も楽。  
```cs
using Microsoft.Win32;
RegistryKey key = Registry.LocalMachine.OpenSubKey(“SOFTWARE\\Microsoft\\Windows\\Notepad\\Capabilities”);
Console.WriteLine(key.GetValue(“ApplicationName”));
key.Close();
```

問題は書き込み方法。  
レジストリスクリプト`.reg`を使うのが手っ取り早い。  
レジストリエディター`regedit`で、キーを指定して右クリック>エクスポートで`.reg`ファイルが作れる。
