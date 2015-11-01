import _root_.sbt.Keys._

name := "auctions"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.11"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
