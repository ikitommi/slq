import sbt._
import Keys._

object SbtBuild extends Build {
  val Organization = "ikitommi"
  val Name = "slq"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.10.2"

  lazy val project = Project (
    "slq",
    file("."),
    settings = Defaults.defaultSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      libraryDependencies ++= Seq(
        "com.typesafe.slick" %% "slick" % "1.0.1",
        "org.slf4j" % "slf4j-nop" % "1.6.4" % "test",
        "com.h2database" % "h2" % "1.3.166" % "test",
        "junit" % "junit" % "4.11" % "test"
      )
    )
  )
}
