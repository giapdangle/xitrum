organization := "tv.cntt"

name := "xitrum"

version := "13.10.24-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked"
)

// http://www.scala-sbt.org/release/docs/Detailed-Topics/Java-Sources
// Avoid problem when Xitrum is built with Java 7 but the projects that use Xitrum
// are run with Java 6
// java.lang.UnsupportedClassVersionError: xitrum/annotation/First : Unsupported major.minor version 51.0
javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

// Put config directory in classpath for easier development (sbt console etc.)
unmanagedBase in Runtime <<= baseDirectory { base => base / "config" }

// Most Scala projects are published to Sonatype, but Sonatype is not default
// and it takes several hours to sync from Sonatype to Maven Central
resolvers += "SonatypeReleases" at "http://oss.sonatype.org/content/repositories/releases/"

// Projects using Xitrum must provide a concrete implentation of SLF4J (Logback etc.)
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.5" % "provided"

// Netty is the core of Xitrum's HTTP(S) feature
libraryDependencies += "io.netty" % "netty" % "3.7.0.Final"

// For clustering SockJS; Akka is included here
libraryDependencies += "tv.cntt" %% "glokka" % "1.2"

// Redirect Akka log to SLF4J
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.2.1"

// For scanning routes
libraryDependencies += "tv.cntt" %% "sclasner" % "1.6"

// For (de)serializing
libraryDependencies += "com.twitter" %% "chill-bijection" % "0.3.2"

// For jsEscape
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.1"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.5"

// For i18n
libraryDependencies += "tv.cntt" %% "scaposer" % "1.2"

// For compiling CoffeeScript to JavaScript
libraryDependencies += "tv.cntt" % "rhinocoffeescript" % "1.6.3"

// Hazelcast is used for distributed cache and SockJS --------------------------

// Infinispan is good but much heavier
libraryDependencies += "com.hazelcast" % "hazelcast" % "2.6.4"

// Hazelcast can be configured as cluster member, lite member, or Java client
libraryDependencies += "com.hazelcast" % "hazelcast-client" % "2.6.4"

// By default, version 2.10.0 of the libs below is used!!! ---------------------

libraryDependencies <+= scalaVersion { sv =>
  "org.scala-lang" % "scala-compiler" % sv
}

libraryDependencies <+= scalaVersion { sv =>
  "org.scala-lang" % "scala-reflect" % sv
}

libraryDependencies <+= scalaVersion { sv =>
  "org.scala-lang" % "scalap" % sv
}

// xitrum.imperatively uses Scala continuation, a compiler plugin --------------

autoCompilerPlugins := true

// https://groups.google.com/forum/?fromgroups#!topic/simple-build-tool/ReZvT14noxU
libraryDependencies <+= scalaVersion { sv =>
  compilerPlugin("org.scala-lang.plugins" % "continuations" % sv)
}

scalacOptions += "-P:continuations:enable"

//------------------------------------------------------------------------------

// Skip API doc generation to speedup "publish-local" while developing.
// Comment out this line when publishing to Sonatype.
publishArtifact in (Compile, packageDoc) := false
