import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "uk.gov.hmrc",
      scalaVersion := "2.12.13",
      version      := "0.1.0"
    )),
    name := "scalacheck-gen-json",
    libraryDependencies += scalaCheck,
    libraryDependencies += playJson,
    libraryDependencies += scRegExp,
    libraryDependencies += scalaTest % Test,
    libraryDependencies += jsonSchema % Test,
  )
