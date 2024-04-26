name := "htmltags"

organization := "com.greenfossil"

version := "1.0.6"

scalaVersion := "3.3.1"

scalacOptions ++= Seq("-feature",  "-deprecation", "-Wunused:all")

Compile / javacOptions ++= Seq("-source", "17")

/*
 * https://github.com/scala/scala-module-dependency-sample
 */
libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0",
  "org.slf4j" % "slf4j-api" % "2.0.10",
  "ch.qos.logback" % "logback-classic" % "1.4.14" % Test,
  "org.scalameta" %% "munit" % "0.7.29" % Test
)

lazy val htmltags = project in file(".")
