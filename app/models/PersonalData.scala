package models

import javax.xml.bind.annotation.XmlType.DEFAULT
import anorm._
import java.sql.Connection
import play.api.libs.json.{Writes, Json}

case class PersonalData(id: Option[Long], name: String, age: Int, address: String)

object PersonalData {

  implicit val writes: Writes[PersonalData] = Json.writes[PersonalData]

  val DEFAULT_ID: Long = 1

  def toModel(row: Row): PersonalData = {
    PersonalData(
      row[Option[Long]]("id"),
      row[String]("name"),
      row[Int]("age"),
      row[String]("address")
    )
  }

  def insert(personalData: PersonalData)(implicit conn: Connection): Long = {
    SQL("insert into PersonalData (name, age, address) values ({name}, {age}, {address})").on(
      "name" -> personalData.name,
      "age" -> personalData.age,
      "address" -> personalData.address
    ).executeInsert().get
  }
//
//  def findById(id: Long)(implicit conn: Connection): Option[PersonalData] = {
//    SQL("select * from Currencies where id = {id}").on(
//      "id" -> id
//    ).singleOpt().map(toModel)
//  }


}










