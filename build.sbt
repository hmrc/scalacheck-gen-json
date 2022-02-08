import Dependencies._
import sbt.Keys._
import sbt._

lazy val commonSettings = Seq(
  name := "scalacheck-gen-json",
  organization := "uk.gov.hmrc",
  majorVersion := 0,
  scalaVersion := "2.12.14",
  scalacOptions ++= List("-Xfatal-warnings"),
  isPublicArtefact := true
)

lazy val library = Project("scalacheck-gen-json", file(".")).
  settings(
    commonSettings,
    libraryDependencies += scalaCheck,
    libraryDependencies += playJson,
    libraryDependencies += scRegExp,
    libraryDependencies += scalaTest % Test,
    libraryDependencies += scalaTestPlusCheck % Test,
    libraryDependencies += jsonSchema % Test,
    libraryDependencies += flexmark % Test
  )
