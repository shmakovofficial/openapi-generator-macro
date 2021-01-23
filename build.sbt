name := "openapi-generator-macro"

version := "0.1"

scalaVersion := "2.13.4"

scalacOptions += "-Ymacro-annotations"

// https://mvnrepository.com/artifact/org.scala-lang/scala-reflect
libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.13.4"

// https://mvnrepository.com/artifact/io.swagger.parser.v3/swagger-parser
libraryDependencies += "io.swagger.parser.v3" % "swagger-parser" % "2.0.23"

// https://mvnrepository.com/artifact/io.circe/circe-core
libraryDependencies += "io.circe" %% "circe-core" % "0.13.0" % Test

// https://mvnrepository.com/artifact/io.circe/circe-generic
libraryDependencies += "io.circe" %% "circe-generic" % "0.13.0" % Test

// https://mvnrepository.com/artifact/io.circe/circe-generic
libraryDependencies += "io.circe" %% "circe-parser" % "0.13.0" % Test

// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.3" % Test

// https://mvnrepository.com/artifact/com.softwaremill.sttp.client/core
libraryDependencies += "com.softwaremill.sttp.client" %% "core" % "2.2.9" % Test

// https://mvnrepository.com/artifact/com.softwaremill.sttp.client/circe
libraryDependencies += "com.softwaremill.sttp.client" %% "circe" % "2.2.9" % Test
