JLivePatcher
===================
JLivePatcher は、特定クラスのみを java 起動時、または既存プロセスに対して差し替える JVMTI java エージェントです。これにより、アプリケーションだけでなくフレームワーク等のパッチ作業でバイナリ全体を入れ替える必要がなくなります。

## ライセンス ##

The GNU Lesser General Public License, version 3.0

## 動作環境 ##
 Java SE 6 以降
 （動作確認は OpenJDK8 @ Fedora23 x64で行っています。

## コンパイル ##
 添付の build.xml を使って ant でビルドしてください (all ターゲット）。

## 使用方法 ##

1. 差し替えるクラスファイルを用意してください。
2. Java のプロパティファイル形式で、Key にクラス名（FQCN）、Value に新しいクラスファイルを記述してください。（test ディレクトリの retransform.properties を参考にしてください。）
  * 起動時差し替えの場合
    * java起動オプションに ```-javaagent:jlivepatcher.jar=<プロパティファイル>``` を追記します。
  * 既存プロセスに対する差し替えは、以下3つのいずれかの方法で可能です。

### 1. AttachAPI

自分で [Attach API](http://docs.oracle.com/javase/jp/6/technotes/guides/attach/index.html) を用いたプログラムを書いてアタッチします。JLivePatcher でも [AttacherMain.java](src/com/yasuenag/jlivepatcher/AttacherMain.java) で利用しています。

### 2. ランチャスクリプトの使用

`dist/jliveattacher` を使用し、以下のように起動します。

```shell
$ jlivepatcher <PID> <プロパティファイル>
```

### 3. jcmd ※Java 9以降

`jcmd` の `JVMTI.agent_load` サブコマンドを使用します。

```shell
$ jcmd <PID> /path/to/jlivepatcher.jar <プロパティファイル（フルパス）>
```

