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
