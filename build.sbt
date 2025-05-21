name := "htmltags"

organization := "com.greenfossil"

version := "1.3.0-RC1"

scalaVersion := "3.7.0"

scalacOptions ++= Seq("-feature",  "-deprecation", "-Wunused:all")

/*
 * https://github.com/scala/scala-module-dependency-sample
 */
libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0",
  "org.slf4j" % "slf4j-api" % "2.0.16",
  "ch.qos.logback" % "logback-classic" % "1.5.16" % Provided,
  "org.scalameta" %% "munit" % "1.1.1" % Test
)

//https://www.scala-sbt.org/1.x/docs/Publishing.html
ThisBuild / versionScheme := Some("early-semver")

lazy val htmltags = project in file(".")
