import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "kurtlogan",
      scalaVersion := "2.12.13",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "scalacheck-json-faker",
    libraryDependencies += scalaCheck,
    libraryDependencies += playJson,
    libraryDependencies += scRegExp,
    libraryDependencies += scalaTest % Test,
    libraryDependencies += jsonSchema % Test,
  )
