name := "test-io"

scalaVersion := "2.11.0"

version := "0.0-SNAPSHOT"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/")

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.4"
