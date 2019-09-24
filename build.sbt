import java.io.File
import com.typesafe.config.ConfigFactory

name := "PlayWorld"

version := "0.1"

scalaVersion := "2.12.10"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "com.h2database" % "h2" % "1.4.199",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
)

//application.confに記述されている slick のデータベース設定を読みこんで Slick用 Tabalesを自動生成する
val slickCodeGen = TaskKey[Unit]("slick-code-generator")
slickCodeGen := {
  val config = ConfigFactory.parseFile(new File("conf/application.conf"))
  SlickCodeGenerator.run(config, database = "default", pkg = "tables", outputDif = "./app")
}
