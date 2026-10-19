name := "htmltags"

organization := "com.greenfossil"

version := "1.4.1-RC1"

scalaVersion := "3.8.3"

scalacOptions ++= Seq("-feature",  "-deprecation", "-Wunused:all")

/*
 * https://github.com/scala/scala-module-dependency-sample
 */
libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.5.0",
  "org.slf4j" % "slf4j-api" % "2.0.20",
  "ch.qos.logback" % "logback-classic" % "1.6.5" % Provided,
  "org.scalameta" %% "munit" % "1.3.6" % Test
)

//https://www.scala-sbt.org/1.x/docs/Publishing.html
ThisBuild / versionScheme := Some("early-semver")

lazy val htmltags = project in file(".")
