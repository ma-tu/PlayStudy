import com.typesafe.config.Config
import slick.codegen.SourceCodeGenerator

object SlickCodeGenerator {

  /**
    * application.confに記述されている slick のデータベース設定を読みこんで Slick用 Tabalesを自動生成する
    * @param config application.conf
    * @param database データベースの識別子（通常はdefault)
    * @param pkg 生成したTablesのパッケージ名
    * @param outputDif 生成ディレクトリ
    */
  def run(config: Config, database: String, pkg: String, outputDif: String): Unit = {
    SourceCodeGenerator.run(
      profile = config.getString(s"slick.dbs.$database.profile").replace("$",""),
      jdbcDriver = config.getString(s"slick.dbs.$database.profile"),
      url = config.getString(s"slick.dbs.$database.db.url"),
      user = getStringOption(config, s"slick.dbs.$database.db.user"),
      password = getStringOption(config, s"slick.dbs.$database.db.password"),
      pkg = pkg,
      outputDir = outputDif,
      ignoreInvalidDefaults = true,
      outputToMultipleFiles = false
    )
  }

  //設定があれば Some() なければ Noneを返す
  private def getStringOption(config: Config, path: String): Option[String] = {
    if (config.hasPath(path)){
      Some(config.getString(path))
    } else {
      None
    }
  }
}
