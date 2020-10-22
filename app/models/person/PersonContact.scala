package models.person

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class
PersonContact(
  zipCode: Option[String],
  address: Option[String],
  telephoneNumber: Option[String],
  email: Option[String]
)

object PersonContact{
  implicit val reads: Reads[PersonContact] = (
    (JsPath \ "zipCode").readNullable[String](minLength[String](3).keepAnd(maxLength[String](8))) ~
      (__ \ "address").readNullable[String](maxLength(100)) ~
      (__ \ "telephoneNumber").readNullable[String](pattern("""^0\d{1,4}-\d{1,4}-\d{4}$""".r, "telephone format invalid")) ~
      (__ \ "email").readNullable[String](email)
  )(PersonContact.apply _)
}