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

## Controllerの追加

notFoundだとつまらないので controllerクラスを追加する

### controllersファイル追加

- 以下ファイルを追加
  - controllers.IndexController
``` scala
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

class IndexController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  def index = Action{
    Ok("Hello Play World")
  }
}
```
- Controllerは Action を生成するもの
- Action とは request をもらって処理して結果を Result にして返すもの
- Ok() は HttpStatus = 200 のシンプルな Result生成方法
- Injectアノテーションは JSR330 の java.inject.Inject を利用するほうが、guice への依存が軽減されてよい
- ControllerComponent は Controllerの便利クラス。Action メソッドが利用できているのもこのおかげ
- ControllerComponent 自体の定義方法はいくつか種類あるが、上記の AbstractController を利用するケースが調べた感じ多い
- コントローラーは Play のルータの依存先となり、Play の DI の機構により、コントローラーが生成され、ルータに依存性注入されます
- コントローラーは 開発者が作成したクラスを DI で利用できますし、作成したクラスでも @Inject を指定することにより芋づる式に DI できます

### conf/routes の記述変更

- 先程用意した conf/routes ファイルを以下に書き換えます
- / にアクセスすると IndexController.indexの結果を返すになります
```
GET   /     controllers.IndexController.index()
```

## テストの追加

### org.scalatestplus.play ライブラリ追加

- build.sbt に以下記述追加
```
libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
)
```
- テストとして以下を追加
- プロジェクト直下に test フォルダを追加して追加

```scala
package controllers

import org.scalatest.FunSpec
import play.api.mvc.Result
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers._

import scala.concurrent.Future

class IndexControllerSpec extends FunSpec {
  describe("Controllerを作成して GET / を実行する") {
    val controller = new IndexController(Helpers.stubControllerComponents())

    val request = FakeRequest(GET, "/")
    val result: Future[Result] = controller.index().apply(request)
    val bodyText: String = contentAsString(result)

    it("Hello Play Worldを返す") {
      assert(bodyText === "Hello Play World")
    }
  }
}
```

- ControlComponent は Helpersクラスで作成できる
- テスト対象のコントローラーを new して作成することでテスト可能
- Reqquest は FakeRequest で作成して呼び出す
- contentAsString は play.api.test.Helpers._ 以下に用意されている

## Slickを利用したデータベース処理（slickCodeGen)

### build.sbt で SlickCodeGen を行うために project 以下の sbt ファイルに以下の dependencies を追加

```
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick-codegen" % "3.3.2",
  "mysql" % "mysql-connector-java" % "8.0.17",
)
```

### slickCodeGen の実装

- project/SlickCodeGeneratir を参考
  - application.conf の設定に従い Tables.scala を生成する
  - outputToMultipleFiles を true にすると Table毎にファイルが生成されます
- build.sbt にてタスクとして登録

```
val slickCodeGen = TaskKey[Unit]("slick-code-generator")
slickCodeGen := {///}
```  

- TaskKey の label タイトルをキャメルケースに置き換えたコマンドが sbt で利用可能になります

## Slickを利用したデータベース処理（slick実行)

### 依存関係の追加

- build.sbt に以下依存関係追加
```
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "4.0.2",
  "mysql" % "mysql-connector-java" % "8.0.17"
}
```
- play-slick を追加すれば slick は自動的に利用可能になります

### application.conf の記述追加

- play-slick で利用する DBの設定になります
```
slick{
  dbs {
    default {
      profile = "slick.jdbc.MySQLProfile$"
      db {
        driver = "com.mysql.cj.jdbc.Driver"
        url = "jdbc:mysql://localhost:3306/play_world"
        user = "root"
        password = ""
      }
    }
  }
}
```

- 上記設定を以下の DatabaseConfigProvider にて読み込んで利用します
- mysql-connector-java の 8.x から driver は 上記のように cj が追加になっています

### db の取得

- 呼び出したい対象のクラスで以下記述を追加

```
class SlickController @Inject()(
  cc: ControllerComponents,
  protected val dbConfigProvider: DatabaseConfigProvider
)
(implicit ec: ExecutionContext)
  extends AbstractController(cc)
    with HasDatabaseConfigProvider[JdbcProfile]{
    ///
}
```

- HasDatabaseConfigProvicder[JdbcProfile] を mixinする
- 上記 trait では dbConfigProvider が未解決なので、dbConfigProvider を DIにて追加する
- 上記 trait では db 変数が宣言されており、内部では 上記 dbConfigProvide より作成されている
- @NamedDatabase("<db-name>") protected val dbConfigProvider: DatabaseConfigProvider の記述で別のdatabaseへの接続も可能

### slick での query

- query を記述したいクラスには slickCodeGen で作成した Tables と profile のインポートが必要です

```
# tables パッケージ以下に Tables がある場合
import tables.Tables._
import tables.Tables.profile.api._
```

- profile.api._ によって result や insert が利用可能になります
- 他 slickの記述方法は別途 slick 調べましょう

### slick から future、future から Action への変換

- slick の Query は result によって DBIOActionになります
- DBIOAction を db.run() で呼び出すことにより Futureになります
- Future[xxx] を map を利用して Future[Result]に変換します
- Future[Result] を Action.async{} で利用することにより、Result が呼び出しもとに返ります

```
    val query = Person.filter(_.id === id.bind).map(_.name)
    db.run(query.result.headOption).map {
      case Some(name) => Ok(name)
      case _ => NotFound
    }
```
