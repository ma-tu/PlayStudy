package models.person

import org.scalatest.FunSpec
import play.api.libs.json.{JsNumber, JsString}

class PersonSexSpec extends FunSpec {
  describe("PersonSex.reads") {
    it ("MANが帰ってくること") {
      assert(PersonSex.MALE === JsString("M").as[PersonSex])
      assert(PersonSex.MALE === JsString("Man").as[PersonSex])
    }
    it ("WOMANが帰ってくること") {
      assert(PersonSex.WOMAN === JsString("F").as[PersonSex])
      assert(PersonSex.WOMAN === JsString("Woman").as[PersonSex])
    }
    it ("UNKNOWNが帰ってくること") {
      assert(PersonSex.UNKNOWN === JsString("X").as[PersonSex])
      assert(PersonSex.UNKNOWN === JsNumber(0).as[PersonSex])
    }
  }
}
