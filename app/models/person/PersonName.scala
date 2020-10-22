package models.person

import play.api.libs.json.Reads._
import play.api.libs.json._

case class PersonName(
  name: String
)

object PersonName {
  //単純なReads
  implicit val reads: Reads[PersonName] = Json.reads[PersonName]

  //特殊なツリー構造からの名前取得
  val specialNameReads: Reads[PersonName] = { json: JsValue =>
    json \ "person" \ "fullName" match {
      case JsDefined(name) => JsSuccess(new PersonName(name.as[String]))
      case err@JsUndefined() => JsError(err.toString)
    }
  }
}

