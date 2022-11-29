name := "htmltags"

organization := "com.greenfossil"

version := "1.0.0-SNAPSHOT"

scalaVersion := "3.2.0"

scalacOptions ++= Seq("-feature",  "-deprecation")

Compile / javacOptions ++= Seq("-source", "17")

/*
 * https://github.com/scala/scala-module-dependency-sample
 */
libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1",
  "org.slf4j" % "slf4j-api" % "2.0.3",
  "ch.qos.logback" % "logback-classic" % "1.4.4" % Test,
  "org.scalameta" %% "munit" % "0.7.29" % Test
)

lazy val htmltags = project in file(".")