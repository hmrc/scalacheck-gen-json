import Dependencies._

lazy val commonSettings = Seq(
  organization := "uk.gov.hmrc",
  majorVersion := 0,
  scalaVersion := "2.12.14",
  isPublicArtefact := true
)

lazy val library = (project in file(".")).
  settings(
    commonSettings,
    publish / skip := true,
    libraryDependencies += scalaCheck,
    libraryDependencies += playJson,
    libraryDependencies += scRegExp,
    libraryDependencies += scalaTest % Test,
    libraryDependencies += scalaTestPlusCheck % Test,
    libraryDependencies += jsonSchema % Test,
    libraryDependencies += flexmark % Test
  )
