{:title "Emacs Lispで http request"
 :tags  ["Emacs"]
 :layout :post
 :toc true}

Emacs Lispで http requestを実装するときのあんちょこ

## httpリクエスト
標準では`url-retrieve-*`が用意されている。  
`url-retrieve`は非同期呼び出しで、コールバック関数を渡す  
`url-retrieve-synchronously`は同期呼び出しで、バッファが返ってくる  
いづれも、レスポンスはバッファに吐かれる。

呼び出し後は文字列で操作したいと思うので、バッファからレスポンスを取り出すコードを書いた。  
注意が必要なのが文字コードで、バッファのデータをそのまま文字にして返しても文字化ける。

```elisp
(defun cons-to-url-query-param (cons)
  (let ((key (->> (car cons)
                  (format "%s")
                  (url-hexify-string)))
        (val (->> (cdr cons)
                  (format "%s")
                  (url-hexify-string))))
    (concat key "=" val)))

(defun url-query-param-concat (url &optional params)
  (if (not params) url
      (concat url "?" (mapconcat #'cons-to-url-query-param params "&"))))

(defun http-request (url &optional params method header body)
  (let ((url-request-method (or method "GET"))  ;; url-retrieve-* が勝手に参照する
        (url-request-extra-headers (or header nil)) ;; url-retrieve-* が勝手に参照する
        (url-request-data (or body nil))        ;; url-retrieve-* が勝手に参照する
        (request-url (url-query-param-concat url params))
        (response-string nil)
        (coding-system-string nil))
    ;; url-retrieve-synchronously はレスポンスデータの入ったバッファを返す
    (with-current-buffer (url-retrieve-synchronously request-url t nil 3)
      ;; レスポンスの文字コードを判別するためにcharsetを検索
      (goto-char (point-min))
      (re-search-forward "charset=")
      (setq coding-system-string
            (buffer-substring-no-properties (point) (point-at-eol)))
      ;; レスポンスボディに移動しデータを取得
      (re-search-forward "\n\n")
      (setq response-string
            (buffer-substring-no-properties (point) (point-max))))
    ;; デコードして文字列で返却
    (decode-coding-string response-string
                          (coding-system-from-name coding-system-string))))
```

## Jsonを扱う
リクエストボディに詰めたり、レスポンスを解析したりでJsonが必要になるはず。  
EmacsにはJson↔lisp objectの相互変換が用意されている。

```elisp
(json-parse-string "{\"key1\":\"value1\",
                     \"key2\":\"value2\",
                     \"array\":[\"a\",\"b\"]}")
```

```elisp
(json-encode '(("key1" . "value1")
               ("key2" . "value2")
               ("array" . ["a" "b"])))
```

plist形式でもいいみたいだけど、key-valueの対応が分かりにくいと思ってるのでalistで書いた。

あとは、Jsonの一部を可変にしてやりたいと思うはず。  
しかし、クォート内で変数渡しても展開されない
```elisp
(defun test (val)
  (json-encode '(("key1" . "value1")
                 ("key2" . "value2")
                 ("text" . val))))

(test "Hello World") ;; => "{\"key1\":\"value1\",\"key2\":\"value2\",\"text\":\"val\"}"
```

クォート内で展開したいときはクォートの代わりにバッククォートを使い、  
展開したい変数を`(,@ var)`で囲う。
```elisp
(defun test (val)
  (json-encode `(("key1" . "value1")
                 ("key2" . "value2")
                 ("text" . (,@ val)))))

(test "Hello World")  ;; => "{\"key1\":\"value1\",\"key2\":\"value2\",\"text\":\"Hello World\"}"
```

## 終わりに
標準のhttp request関数がもう少し使いやすかったら苦労しないのにと思う。
