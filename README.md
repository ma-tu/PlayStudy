# PlayWorld

sbtのプロジェクトに対して環境構築していくための情報

## 最小限のPlay環境追加

以下設定を行うことにより playが起動します

### conf/plugins.sbt

- 以下記述を追加する。
- ここで追加したバージョンが Play のバージョンになります

```
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.3")
```

### build.sbt

- 以下記述を追加する
- rootの名前は指定はなさそうであるが、単一プロジェクトの場合は慣習的にで root にする
- file(".")の指定によりsbt直下をプロジェクトフォルダとします。
- enablePlugins を指定することで play のプロジェクトとなります
- guice は Play2.6 以降含まれなくなったので個別に追加が必要


```
lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice
)
```

### conf/application.conf

- conf/application.conf のファイルが最低限必要です
- Play の設定ファイルとなります
- ファイルの中身はなくても問題なく稼働します

### conf/routes

- conf/routes のファイルが最低限必要です
- Play の HTTP routerの定義ファイルになります。
- 記述が必要なので、今回は以下記述としました
  - / にアクセスすると not Found を返します

```
GET   /     controllers.Default.notFound
``` 


