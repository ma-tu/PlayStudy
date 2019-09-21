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
