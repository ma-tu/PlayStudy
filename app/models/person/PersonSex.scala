package models.person

import play.api.libs.json.{JsString, JsSuccess, JsValue, Reads}

sealed abstract class PersonSex(val code: String, val text: String)

object PersonSex {
  case object MALE extends PersonSex("M", "男")
  case object WOMAN extends PersonSex("F", "女")
  case object UNKNOWN extends PersonSex("UNK", "不明")

  val values: Seq[PersonSex] = Seq(MALE, WOMAN)

  def apply(code: String): PersonSex = values.find(_.code == code).getOrElse(UNKNOWN)

  implicit val reads: Reads[PersonSex] = Reads[PersonSex] {
    case JsString("M") => JsSuccess(PersonSex.MALE)
    case JsString("Man") => JsSuccess(PersonSex.MALE)
    case JsString("F") => JsSuccess(PersonSex.WOMAN)
    case JsString("Woman") => JsSuccess(PersonSex.WOMAN)
    case _ => JsSuccess(PersonSex.UNKNOWN)
  }
}
