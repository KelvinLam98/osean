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
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.collection.mutable

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, db: Database) extends BaseController {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def overview = Action { implicit request: Request[AnyContent] =>
      Ok(views.html.overview())
  }

  def overviewJson = Action { implicit request =>
    val start = request.getQueryString("start").map(_.toLong).getOrElse(0L)
    val length = request.getQueryString("length").map(_.toLong).getOrElse(10L)
    val draw: Int = request.getQueryString("draw").map(_.toInt).getOrElse(0)
    val searchText = request.getQueryString("search[value]").getOrElse("")

    db.withConnection { implicit conn =>
      val total = PersonalData.countAll
      val filtered = PersonalData.countFiltered(searchText)
      val personalDetail = PersonalData.search(start, length, searchText)
      Ok(Json.obj(
        "draw" -> draw,
        "recordsTotal" -> total,
        "recordsFiltered" -> filtered,
        "data" -> personalDetail
      ))
    }
  }

//  def personalDetails(id: Long) = Action { implicit request =>
//    db.withConnection { implicit conn =>
//      PersonalData.findById(id).map { pd =>
//        Ok(views.html.personalDetailList(pd))
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
        println(form.errors.map(_.key).mkString(","))
        Redirect(routes.HomeController.createPersonalData)
          .flashing(Flash(form.data)+
            ("errors" -> form.errors.map(_.key).mkString(",")))
      },
      success = { data =>
        db.withTransaction { implicit conn =>
        val id: Long = PersonalData.insert(data)
          println("success")
        Redirect(routes.HomeController.overview)
          .flashing(Flash(Map("success" -> "post-create-currency-data-success")))
    }
    }
    )
  }

}


