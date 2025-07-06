{:title "Reagentでinput使うたびにはまること"
 :tags  ["ClojureScript"]
 :layout :post
 :toc true}

Reagentでinput使うたびにうまく動かなくてはまってるのでメモ

## inputを含んだコンポーネント作る
ローカルにinputの入力変数を作りたいので、letでatom持たせる。  
しかしこれでは、@atom は最初の評価時に固定されてしまう

```clojure
(ns app.todo-input
  (:require [reagent.core :as reagent]
            [clojure.string :as str]))

(defn todo-input [add-task-fn]
  (let [task-title (reagent/atom "")]
      [:div.todo-input
       [:input {:type "text"
                :placeholder "タスクを追加"
                :value @task-title
                :onChange (fn [e] (reset! task-title (.. e -target -value)))}]
       [:button {:onClick (fn []
                                                   ; ↓ "" と置き換わった状態で定義されるので、ずっと””
                            (when-not (str/blank? @task-title) 
                            (add-task-fn @task-title)
                            (reset! task-title "")))}
        "追加"]]))
```

## fnで囲ってやる
letの戻り値を、コンポーネント（vector）ではなく、関数を返すと、
atomの更新時に、fn全体が再評価されて、初期値から動かない問題がなくなる

```clojure
(ns app.todo-input
  (:require [reagent.core :as reagent]
            [clojure.string :as str]))

(defn todo-input [add-task-fn]
  (let [task-title (reagent/atom "")]
    (fn []
      [:div.todo-input
       [:input {:type "text"
                :placeholder "タスクを追加"
                :value @task-title
                :onChange (fn [e] (reset! task-title (.. e -target -value)))}]
       [:button {:onClick (fn []
                            (when-not (str/blank? @task-title)
                            (add-task-fn @task-title)
                            (reset! task-title "")))}
        "追加"]])))
```

## これってOnChangeのたびに全体評価しなおしって、パフォーマンス良くないよね
ほかの方法を探してみる⇒https://mseeeen.msen.jp/react-update-input-defaultvalue/  
上記によると、Reactは制御コンポーネントと非制御コンポーネントの書き方があり、
Reactの変数にバインドしなくても、DOM操作で値引っ張る方法もある。

id設定して、getElementByIdから値をたどる例

```clojure
(ns app.todo-input
  (:require [reagent.core :as reagent]
            [clojure.string :as str]))

(defn todo-input [add-task-fn]
  (let [id (random-uuid)]
    [:div.todo-input
     [:input {:id id
              :type "text"
              :placeholder "タスクを追加"}]
     [:button {:onClick (fn []
                          (let [input (.getElementById js/document id)
                                value (.. input -value)]
                            (when-not (str/blank? value)
                              (add-task-fn value)
                              (set! (.. input -value) ""))))}
        "追加"]]))
```

## getElementById使わないほうがいいよね
相対位置で取得する関数を使う。
- previousElementSibling:自分の兄弟要素で、前の要素を取得
- nextElementSibling:自分の兄弟要素で、後の要素を取得

```clojure
(ns app.todo-input
  (:require [reagent.core :as reagent]
            [clojure.string :as str]))

(defn todo-input [add-task-fn]
  [:div.todo-input
   [:input {:type "text"
            :placeholder "タスクを追加"}]
   [:button {:onClick (fn [e]
                        (let [element (.. e -target)
                              input (.. element -previousElementSibling)
                              value (.. input -value)]
                          (when-not (str/blank? value)
                            (add-task-fn value)
                            (set! (.-value input) ""))))}
    "追加"]])
```


## refプロパティを使うと、相対位置やidじゃなくても取得できるらしい
LLMに聞いて出てきた答え。  
Reactの機能でuseRefというやつ。  
Reagentでは、属性で:refつけるだけでOK。

```clojure
(ns app.todo-input
  (:require [reagent.core :as reagent]
            [clojure.string :as str]))

(defn todo-input [add-task-fn]
  (let [input-ref (reagent/atom nil)]
    [:div.todo-input
     [:input {:ref #(reset! input-ref %) ; ← atomにthis elementを保持
              :type "text"
              :placeholder "タスクを追加"}]
     [:button {:onClick (fn [e]
                          (let [input @input-ref ; ← elementを参照
                                value (.. input -value)] ; ← jsで値とってくる
                            (when-not (str/blank? value)
                              (add-task-fn value)
                              (set! (.-value input) ""))))}
      "追加"]]))
```


## まとめ
ReagentもReactも理解浅い状態でやると、無駄な書き方してることが分かった。  
過去に、letとの組み合わせで、とりあえずfnで囲えば動くとしてたとこを見直したくなった。
