package models.person

import org.scalatest.FunSpec
import play.api.libs.json.{JsResultException, Json}

class PersonContactSpec extends FunSpec {

  describe("PersonContact.reads") {
    describe("単純なreads") {
      it ("単純なjson文字列においてPersonContactを返すこと") {
        val jsonString =
          """{
            | "zipCode": "100-1000",
            | "address": "Tokyo",
            | "telephoneNumber": "0123-456-7890",
            | "email": "test@example.com"
            |}""".stripMargin

        val expected = PersonContact(
          Some("100-1000"),
          Some("Tokyo"),
          Some("0123-456-7890"),
          Some("test@example.com")
        )
        val actual = Json.parse(jsonString).as[PersonContact]
        assert(expected === actual)
      }
    }

    it ("一部nullな場合においてもPersonContactを返すこと") {
      val jsonString =
        """{
          | "zipCode": "100-1000",
          | "address": "Tokyo",
          | "telephoneNumber": null
          |}""".stripMargin

      val expected = PersonContact(
        Some("100-1000"),
        Some("Tokyo"),
        None,
        None
      )
      val actual = Json.parse(jsonString).as[PersonContact]
      assert(expected === actual)
    }

    it ("zipCodeが1桁の場合はエラーとなる") {
      val jsonString =
        """{
          | "zipCode": "1"
          |}""".stripMargin

      assertThrows[JsResultException] {
        Json.parse(jsonString).as[PersonContact]
      }
    }

    it ("電話番号の正規表現に違反している場合はエラーになる") {
      val jsonString =
        """{
          | "telephoneNumber": "0123-4567-890"
          |}""".stripMargin

      val ex =
        intercept[JsResultException] {
          Json.parse(jsonString).as[PersonContact]
        }
      assert(ex.getMessage.indexOf("telephone format invalid") != -1)
    }
  }
}
