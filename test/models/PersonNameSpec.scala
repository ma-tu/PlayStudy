package models

import java.time.LocalDate

import models.person.PersonName
import org.scalatest.FunSpec
import play.api.libs.json.{JsSuccess, Json}

class PersonNameSpec extends FunSpec {

  describe("PersonName.read") {

    describe("単純な reads") {
      it("Personクラスに変換できること") {
        val jsonString =
          """{
            | "name": "Taro"
            |}""".stripMargin
        val expected = PersonName(name = "Taro")

        Json.parse(jsonString).validate[PersonName] match{
          case JsSuccess(value, _) => assert(expected === value)
          case _ => fail()
        }
      }
    }

    describe("pathを探索する reads") {
      it("Personクラスに変換できること") {
        val jsonString =
          """{
            | "person": {
            |    "fullName" : "Taro"
            |  }
            |}""".stripMargin
        val expected = PersonName(name = "Taro")

        val actual = Json.parse(jsonString).as[PersonName](PersonName.specialNameReads)
        assert(expected === actual)
      }
    }
  }
}
