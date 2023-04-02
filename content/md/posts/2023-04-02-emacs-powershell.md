{:title "EmacsでPowerShellの開発環境を整える"
 :tags  ["Emacs" "PowerShell"]
 :layout :post
 :toc true}

PowerShellを書くことが多くなってきたので、補完やREPLが欲しくなって整えた。

## シンタックスハイライト
`tree-sitter`をつかうのがイマドキな感じだが、PowerShellのはうまく動かなかった。  
しかたがないので、メジャーモードでやる。  
[powershell.el](https://github.com/jschaf/powershell.el)を使う。  
Melpaからとれるので`M-x package-install powershell`でインストールできる。

## Shell環境
NTEmacsのshellは`cmd`なので、PowerShellのスクリプトを実行するには、  
`$ powershell -File foo.ps1`  
のように呼ばないといけない。これは面倒。  
PowerShellのプロンプト環境をEmacsから使えれば、  
`$ foo.ps1`  
で呼び出せる。

[powershell.el](https://github.com/jschaf/powershell.el)にはそれがある。  
`M-x powershell`で`M-x shell`のようにPowerShellのプロンプト環境が立ち上がる。

## REPL環境 
前項で、スクリプト実行をEmacsの中でできるようになったが、  
次に欲しくなるのが、スクリプトの一部をshellに送って実行するREPL環境。

残念ながら、[powershell.el](https://github.com/jschaf/powershell.el)にはその機能はない。  
なので自作する。

以下のように関数を定義して、それをキーにバインドすればいい。
```elisp
;;; powershell-shell.el --- powershell shell sender
;;; Commentary:
;;; Code:
(require 'powershell)

;;; interactive
;;;###autoload
(defun my-powershell-shell-send-line ()
  "Send powershell shell pointd line."
  (interactive)
  (let ((string (replace-regexp-in-string
                 "^[ \t]+"
                 ""
                 (thing-at-point 'line t)))
        (process (get-buffer-process "*PowerShell*")))
    (my-powershell-shell-send-string string process)))

;;;###autoload
(defun my-powershell-shell-send-region (s e)
  "Send powershell shell region.
S region start point
E region end point"
  (interactive "r")
  (let ((region-string (buffer-substring-no-properties s e))
        (process (get-buffer-process "*PowerShell*")))
    (mapc (lambda (string)
            (my-powershell-shell-send-string string process)
            ;; Wait for powershell side standard output.
            (sleep-for 0.01))
          (split-string region-string "[\n\r]"))))

(defun my-powershell-shell-send-string (string process)
    "Send string to the process.
STRING substring up to newline is sent.
PROCESS process object"
    (with-current-buffer (process-buffer process)
    (goto-char (point-max))
    (insert (car (split-string string "[\n\r]")))
    (comint-send-input)
    (goto-char (point-max))))

(provide 'powershell-shell)
;;; powershell-shell.el ends here
```

キーバインドの設定例。
```elisp
(leaf *powershell-mode
  :doc "powershell用設定"
  :hook (powershell-mode-hook . my-powershell-mode-hook)
  :preface
  (defun my-powershell-mode-hook ()
    (local-set-key (kbd "<C-return>") 'my-powershell-shell-send-line)
    (local-set-key (kbd "C-x C-e") 'my-powershell-shell-send-region)
    (lsp))
  :config
  (leaf powershell
    :ensure t))
```

仕組みを解説する。
1. `(get-buffer-process "*PowerShell*")`でPowerShellのshellバッファを探し、そこで起動されているプロセスを取得
2. `(process-buffer process)`でPowerShellのshellバッファを探し、カレントバッファを一時的に切り替える
3. カーソルをPowerShellのshellバッファの末尾に移動
4. 渡されたstringをPowerShellのshellバッファに書き出す
5. `(comint-send-input)`で実際のプロセスに送る（Enterキー押したのと同じ）

shellバッファの探し方で1回プロセスを経由するのはPythonのときのコードを流用したからで、  
Pythonのときはスクリプトに結びついたrun-pythonのプロセスを取得する関数があったため。  
[powershell.el](https://github.com/jschaf/powershell.el)にはその機能はないので、バッファ名で直接指定。

## 補完とLint
`lsp`を使えばよい。Language Serverは`pwsh-ls`。  
`M-x lsp-install-server`で`pwsh-ls`を選べばOK。

`company`と`flycheck`使えるようにしてれば、補完とLintできるはず。

ただし、`lsp-mode`が古いとうまく起動しなかった。  
自分の環境だと`lsp-mode`が`8.0.0`でダメだったので、最新`20230401.434`だとうまくいった。

原因は、`pwsh-ls`を起動するためのスクリプトのパスが`lsp-mode`の`lsp-pwsh`で定義されているが、間違っている。

どのバージョンで正しくなったかまでは追ってないが、とりあえず最近のにすれば大丈夫そう。

## 自動成形
`M-x lsp-format-buffer`で整形してくれる。

まだ大きいスクリプトで試してないので、しばらくは手打ちで様子見。

問題なければ、`before-save-hook`に追加するつもり。

## あとがき
マイナーな言語なので、lspとかshellとかないだろうと思ってたが、どちらも用意されていて助かった。  

PowerShellのいいとこが最近わかってきて積極的に使いたいと思っている。
- Windows環境だとインストールレス
- HTTPリクエストがライブラリなしで使える
- 正規表現がライブラリなしで使える
- map,filterなどの集計に便利な機能がライブラリなしで使える
- 関数型っぽく使えて好き
- あたりまえだけどexe,batの呼び出しができるし、それをテキスト入力としてpipeできる
- UNIX的思想で好き
- DLLもロードして使うことができる
- マネージドDLLはAdd-Typeで一発だし、アンマネージドDLLでも.NETのDllImportのやり方でできる

