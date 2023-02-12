{:title "関数型プログラミングのパターンマッチング"
 :tags ["FP"]
 :layout :post
 :toc true}

switch文のcaseが1行で終わるようなコードを頻繫に書いてると思って、良い書き方がないかを調べてパターンマッチングに行きついた。  

## パターンマッチングとは
命令型プログラミングの`switch文`に相当。  
パターンマッチングの場合には、型推論使って、その型で表現される集合からマッチするものを絞り込むイメージ。  
命令型プログラミングの`switch文`は単なる複数の比較の省略。

## Clojure
デフォルトではサポートされていない。  
`core.match`をインストールが必要。

```clojure
(require '[clojure.core.match :refer [match]])

(doseq [n (range 1 101)]
  (println
    (match [(mod n 3) (mod n 5)]
      [0 0] "FizzBuzz"
      [0 _] "Fizz"
      [_ 0] "Buzz"
      :else n)))
```

## C#
C#8.0以降で使える。`switch式`と呼ばれる。

```csharp
using System;
using System.Linq;
					
public class Program
{
    public static void Main()
    {
        foreach (var x in Enumerable.Range(1, 15)) 
        {
            Console.WriteLine(FizzBuzz(x));
        }
    }

    public static string FizzBuzz(int num){
        return (num % 3, num % 5) switch {
            (0, 0) => "FizzBuzz",
            (0, _) => "Fizz",
            (_, 0) => "Buzz",
            _ => num.ToString()
        };
    }
}
```

パターンマッチングを使うとCSVのパースに生かせる。
```csv
04-01-2020, DEPOSIT,    Initial deposit,            2250.00
04-15-2020, DEPOSIT,    Refund,                      125.65
04-18-2020, DEPOSIT,    Paycheck,                    825.65
04-22-2020, WITHDRAWAL, Debit,           Groceries,  255.73
05-01-2020, WITHDRAWAL, #1102,           Rent, apt, 2100.00
05-02-2020, INTEREST,                                  0.65
05-07-2020, WITHDRAWAL, Debit,           Movies,      12.57
04-15-2020, FEE,                                       5.55
```

```csharp
decimal balance = 0m;

// 行数文回す
foreach (string[] transaction in ReadRecords())
{
    // パース結果; 数値 を balance に集計していく
    balance += transaction switch
    {
        // transaction はカンマでスプリット済みの配列
        // 行のアイテム種別ごとの読み替えを以下のように行う
        [_, "DEPOSIT", _, var amount]     => decimal.Parse(amount),
        [_, "WITHDRAWAL", .., var amount] => -decimal.Parse(amount),
        [_, "INTEREST", var amount]       => decimal.Parse(amount),
        [_, "FEE", var fee]               => -decimal.Parse(fee),
        _                                 => throw new InvalidOperationException($"Record {string.Join(", ", transaction)} is not in the expected format!"),
    };
    Console.WriteLine($"Record: {string.Join(", ", transaction)}, New balance: {balance:C}");
}
```

## あとがき
switch文のcaseが1行で終わるようなコードは、たいてい処理を関数化して戻り値を変数に入れて、そのあとの処理をしてることが多い。
```csharp
bool ret = false;
switch (some_enum)
{
    case SOME_ENUM.ALPHA:
        ret = funcA();
        break;
    case SOME_ENUM.BETA:
        ret = funcB();
        break;
    default:
        ret = func();
        break;
}

if (ret)
{
    something();
}
```

こういうのを`switch式`にしたら、代入を減らせて良い。

CSVの解析の例は可能性を感じる。
