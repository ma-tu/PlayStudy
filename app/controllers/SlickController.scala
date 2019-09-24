package controllers

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.JdbcProfile
import tables.Tables._
import tables.Tables.profile.api._

import scala.concurrent.ExecutionContext

class SlickController @Inject()(
  cc: ControllerComponents,
  protected val dbConfigProvider: DatabaseConfigProvider
)
(implicit ec: ExecutionContext)
  extends AbstractController(cc)
    with HasDatabaseConfigProvider[JdbcProfile]{

  def find(id: Long) = Action.async {
    val query = Person.filter(_.id === id.bind).map(_.name)
    db.run(query.result.headOption).map {
      case Some(name) => Ok(name)
      case _ => NotFound
    }
  }

  def list = Action.async {
    val query = Person.map{case p => p.name}
    db.run(query.result).map(_.mkString(",")).map(Ok(_))
  }
}