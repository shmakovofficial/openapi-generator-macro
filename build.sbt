name := "openapi-generator-macro"

version := "0.1"

scalaVersion := "2.13.4"

scalacOptions += "-Ymacro-annotations"

// https://mvnrepository.com/artifact/org.scala-lang/scala-reflect
libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.13.4"

// https://mvnrepository.com/artifact/io.swagger.parser.v3/swagger-parser
libraryDependencies += "io.swagger.parser.v3" % "swagger-parser" % "2.0.23"

// https://mvnrepository.com/artifact/org.endpoints4s/algebra
libraryDependencies += "org.endpoints4s" %% "algebra" % "1.2.0"

// https://mvnrepository.com/artifact/org.endpoints4s/sttp-client
libraryDependencies += "org.endpoints4s" %% "sttp-client" % "2.0.0"

// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.3" % Test

// https://mvnrepository.com/artifact/com.softwaremill.sttp.client/core
libraryDependencies += "com.softwaremill.sttp.client" %% "core" % "2.2.9" % Test
