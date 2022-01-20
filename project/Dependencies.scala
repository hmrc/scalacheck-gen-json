import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.3"
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.14.1"
  lazy val scalaTestPlusCheck = "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0"
  lazy val playJson = "com.typesafe.play" %% "play-json" % "2.8.2"
  lazy val scRegExp = "io.github.wolfendale" %% "scalacheck-gen-regexp" % "0.1.3"
  lazy val jsonSchema = "com.github.erosb" % "everit-json-schema" % "1.14.0"
  lazy val flexmark = "com.vladsch.flexmark" % "flexmark-all" % "0.35.10"
}
