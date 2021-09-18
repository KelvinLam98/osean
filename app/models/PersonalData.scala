package models

import anorm.SqlParser.scalar

import javax.xml.bind.annotation.XmlType.DEFAULT
import anorm._

import java.sql.Connection
import play.api.libs.json.{Json, Writes}

case class PersonalData(id: Option[Long], name: String, age: Int, address: String)

object PersonalData {

  implicit val writes: Writes[PersonalData] = Json.writes[PersonalData]

  val parser: RowParser[PersonalData] = Macro.namedParser[PersonalData]

  val DEFAULT_ID: Long = 1

  def toModel(row: Row): PersonalData = {
    PersonalData(
      row[Option[Long]]("id"),
      row[String]("name"),
      row[Int]("age"),
      row[String]("address")
    )
  }

  def findAll(implicit conn: Connection): Seq[PersonalData] = {
    SQL("select * from PersonalData").on().as(parser.*)
  }

  def insert(personalData: PersonalData)(implicit conn: Connection): Long = {
    SQL("insert into PersonalData (name, age, address) values ({name}, {age}, {address})").on(
      "name" -> personalData.name,
      "age" -> personalData.age,
      "address" -> personalData.address
    ).executeInsert().get
  }

  def findById(id: Long)(implicit conn: Connection): Option[PersonalData] = {
    SQL("select * from PersonalData where id = {id}").on(
      "id" -> id
    ).as(parser.singleOpt)
  }

  def countAll(implicit conn: Connection): Long = {
    SQL("select count(*) as count from PersonalData").as(SqlParser.long("count").single)

  }

  def countFiltered(searchText: String)(implicit conn: Connection): Long = {
    val searchCriteria =
      if(searchText.isEmpty)
        ""
      else
        "where (code like {searchText} or description like {searchText})"
      SQL("select count(*) as count from PersonalData " + searchCriteria).on(
        "searchText" -> ("%" + searchText + "%")
      ).as(SqlParser.long("count").single)
  }

  def search(start: Long, count: Long, searchText: String)(implicit conn: Connection): Seq[PersonalData] = {
    val searchCriteria =
      if(searchText.isEmpty)
        ""
      else
        "where (code like {searchText} or description like {searchText})"
    SQL("select * from Currencies " + searchCriteria + " order by code Asc limit {start}, {count}").on(
      "start" -> start,
      "count" -> count,
      "searchText" -> ("%" + searchText + "%")
    ).as(parser.*)
  }

}










