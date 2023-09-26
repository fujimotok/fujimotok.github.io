{:title "Emacs の Cider から Shadow-cljs に接続して evalると 'No avaliable JS runtime.' がでる"
 :tags  ["ClojureScript"]
 :layout :post
 :toc true}

CiderでShadow-cljs使うので詰まったのでメモ。

## 解決策：Shadow-cljsで立ち上げたサーバにブラウザでアクセスする
Shadow-cljsで立ち上げたサーバにブラウザでアクセスしていないと、evalるのが失敗する。

## なぜブラウザでのアクセスが必須なのか？
Ciderからのアクセスの流れは、Shadow-cljsがサーバを立てて、これにアクセスしたブラウザとセッションを張り、ブラウザのJS実行環境に対して、流し込んでいる。
![](https://shadow-cljs.github.io/docs/assets/img/shadow-cljs-repl.png)

なのでブラウザがいないと実行環境がない状態になる。

## ついでに
Ciderの補完がうまくいかない問題がある。  
issueはすでにたてられている。[参考](https://github.com/clojure-emacs/clj-suitable/issues/33)

取り合えず、自分の手元では以下を修正したらうまく動くようになった。
```elisp :cider-completion.el
(defun cider-complete-at-point ()
  "Complete the symbol at point."
  (when-let* ((bounds (bounds-of-thing-at-point 'symbol)))
    (when (and (cider-connected-p)
               (not (or (cider-in-string-p) (cider-in-comment-p))))
      (list (car bounds) (cdr bounds)
            (lambda (prefix pred action)
              ;; When the 'action is 'metadata, this lambda returns metadata about this
              ;; capf, when action is (boundaries . suffix), it returns nil. With every
              ;; other value of 'action (t, nil, or lambda), 'action is forwarded to
              ;; (complete-with-action), together with (cider-complete), prefix and pred.
              ;; And that function performs the completion based on those arguments.
              ;;
              ;; This api is better described in the section
              ;; '21.6.7 Programmed Completion' of the elisp manual.
              (cond ((eq action 'metadata) `(metadata (category . cider)))
                    ((eq (car-safe action) 'boundaries) nil)
                    (t (with-current-buffer (current-buffer)
                         (setq prefix (buffer-substring (car bounds) (cdr bounds))) ;; <- ここを足した
                         (complete-with-action action
                                               (cider-complete prefix) prefix pred)))))
            :annotation-function #'cider-annotate-symbol
            :company-kind #'cider-company-symbol-kind
            :company-doc-buffer #'cider-create-doc-buffer
            :company-location #'cider-company-location
            :company-docsig #'cider-company-docsig))))
```
