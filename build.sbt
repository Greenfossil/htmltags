name := "htmltags"

organization := "com.greenfossil"

version := "1.0.8"

scalaVersion := "3.3.3"

scalacOptions ++= Seq("-feature",  "-deprecation", "-Wunused:all")

Compile / javacOptions ++= Seq("-source", "17")

/*
 * https://github.com/scala/scala-module-dependency-sample
 */
libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0",
  "org.slf4j" % "slf4j-api" % "2.0.12",
//  "ch.qos.logback" % "logback-core" % "1.5.6" % Provided,
  "ch.qos.logback" % "logback-classic" % "1.5.6" % Provided,
  "org.scalameta" %% "munit" % "1.0.0" % Test
)

//https://www.scala-sbt.org/1.x/docs/Publishing.html
ThisBuild / versionScheme := Some("early-semver")

lazy val htmltags = project in file(".")

//Remove logback from test jar
Test / packageBin / mappings ~= {
  _.filterNot(_._1.getName.startsWith("logback"))
}