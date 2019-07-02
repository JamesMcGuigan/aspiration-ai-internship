lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.jamesmcguigan",
      scalaVersion := "2.12.7"
    )),
    name := "scala",
    mainClass in (Compile, run) := Some("module_1.StockAnalysis")
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test

// DOCS: https://nrinaudo.github.io/kantan.csv/
libraryDependencies += "com.nrinaudo" %% "kantan.csv" % "0.5.1"
libraryDependencies += "com.nrinaudo" %% "kantan.csv-java8" % "0.5.1"
//libraryDependencies += "com.nrinaudo" %% "kantan.csv-scalaz" % "0.5.1"
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "0.5.1"

