import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.14.1"
  lazy val playJson = "com.typesafe.play" %% "play-json" % "2.8.2"
  lazy val scRegExp = "io.github.wolfendale" %% "scalacheck-gen-regexp" % "0.1.3"
  lazy val jsonSchema = "com.github.erosb" % "everit-json-schema" % "1.14.0"
}
