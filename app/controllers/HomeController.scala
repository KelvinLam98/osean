package controllers

import akka.parboiled2.RuleTrace.Optional
import models.PersonalData

import javax.inject._
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages.implicitMessagesProviderToMessages
import play.api.mvc._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import java.sql.Connection
import javax.sql.DataSource
import play.api.db.Database
import scala.concurrent.Future

import scala.collection.mutable

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, db: Database) extends BaseController {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def overview() = Action { implicit request: Request[AnyContent] =>
      Ok(views.html.overview(personalDetail.toSeq))
  }

  private var personalDetail = mutable.ArrayBuffer(
    PersonalData(Option(1), "Emily" , 25  , "Kepong"),
    PersonalData(Option(2), "John"  , 30  , "Old Klang Road"),
    PersonalData(Option(3), "Jacky" , 27  , "Cheras")
  )
//  def personalDetails(id: Long) = Action { implicit request =>
//    db.withConnection { implicit conn =>
//      PersonalData.findById(id).map { pd =>
//        Ok(views.html.overview.personalDetails(pd))
//      }.getOrElse(NotFound)
//    }
//  }

  val dataForm: Form[PersonalData] = Form(
    mapping(
      "id"      -> optional(longNumber),
      "name"    -> nonEmptyText,
      "age"     -> number(min = 0),
      "address" -> nonEmptyText
    )(PersonalData.apply)(PersonalData.unapply)
  )

  def createPersonalData = Action { implicit request =>
    val (form, errors) =
      request.flash.get("errors") match {
        case Some(errorsStr) =>
          (dataForm.bind(request.flash.data), errorsStr.split(","))
        case None =>
          (dataForm, Array.empty[String])
      }
    Ok(views.html.dataForm("create", form, errors))
  }

  def postAddData = Action { implicit request =>
    dataForm.bindFromRequest().fold(
      hasErrors = { form =>
        Redirect(routes.HomeController.createPersonalData)
          .flashing(Flash(form.data)+
            ("errors" -> form.errors.map(_.key).mkString(",")))
      },
      success = { data =>
        db.withTransaction { implicit conn =>
      val id: Long = PersonalData.insert(data)
          println("xxx")
        Redirect(routes.HomeController.overview())
          .flashing(Flash(Map("success" -> "post-create-currency-data-success")))
    }
    }
    )
  }

}


