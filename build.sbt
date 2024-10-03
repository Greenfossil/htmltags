name := "htmltags"

organization := "com.greenfossil"

version := "1.1.0"

scalaVersion := "3.5.0"

scalacOptions ++= Seq("-feature",  "-deprecation", "-Wunused:all")

Compile / javacOptions ++= Seq("-source", "17")

/*
 * https://github.com/scala/scala-module-dependency-sample
 */
libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0",
  "org.slf4j" % "slf4j-api" % "2.0.12",
  "ch.qos.logback" % "logback-classic" % "1.5.6" % Provided,
  "org.scalameta" %% "munit" % "1.0.0" % Test
)

//https://www.scala-sbt.org/1.x/docs/Publishing.html
ThisBuild / versionScheme := Some("early-semver")

lazy val htmltags = project in file(".")
