JLivePatcher
===================
JLivePatcher は、特定クラスのみを java 起動時、または既存プロセスに対して
差し替える JVMTI java エージェントです。これにより、アプリケーションだけで
なくフレームワーク等のパッチ作業でバイナリ全体を入れ替える必要がなくなります。

## ライセンス ##
 GNU General Public License, version 2

## 動作環境 ##
 Java SE 6 以降
 （動作確認は OpenJDK7 @ Fedora20 x64で行っています。

## コンパイル ##
 添付の build.xml を使って ant でビルドしてください (all ターゲット）。

## 使用方法 ##

　1. 差し替えるクラスファイルを用意してください。

　2. Java のプロパティファイル形式で、Key にクラス名（FQCN）、Value に新しい
     クラスファイルを記述してください。
     (test ディレクトリの retransform.properties を参考にしてください。

 (A) 起動時差し替えの場合
  A-3. java 軌道オプションに以下を追記します。
　       -javaagent:jlivepatcher.jar=<2.で作成したプロパティファイル>

 (B) 既存プロセスに対する差し替え
  B-3. AttachAPI (http://docs.oracle.com/javase/jp/6/technotes/guides/attach/index.html)
       を用いて既存プロセスにアタッチします。
       使用する JVMTI エージェントは $JAVA_HOME/jre/lib/<CPU>/libinstrument.so
       で、引数に jlivepatcher.jar=<2.で作成したプロパティファイル> を与えて
       ください。
         ※Java 障害解析支援ツール HeapStats 1.1、およびコミュニティサイトにて
          AttachAPI に準拠した動的アタッチプログラムを配布しています。
          http://icedtea.classpath.org/wiki/HeapStats/jp/tips/agent#.E6.97.A2.E5.AD.98.E3.83.97.E3.83.AD.E3.82.BB.E3.82.B9.E3.81.AB.E5.AF.BE.E3.81.97.E3.81.A6HeapStats.E3.82.A8.E3.83.BC.E3.82.B8.E3.82.A7.E3.83.B3.E3.83.88.E3.82.92.E3.82.A2.E3.82.BF.E3.83.83.E3.83.81.E3.81.99.E3.82.8B.E3.81.93.E3.81.A8.E3.81.8C.E3.81.A7.E3.81.8D.E3.81.BE.E3.81.99.E3.81.8B.EF.BC.9F

