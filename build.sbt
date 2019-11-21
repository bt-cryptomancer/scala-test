name := "RestaurantApp"

version := "0.1"

scalaVersion := "2.13.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.6.0"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.6.0" % Test
libraryDependencies += "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.11.0"