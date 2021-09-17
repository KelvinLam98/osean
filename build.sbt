
name := """Osean"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  guice,
  jdbc,
  evolutions,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  "mysql" % "mysql-connector-java" % "5.1.41",
  "org.playframework.anorm" %% "anorm" % "2.6.10"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
