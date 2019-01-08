name := "panda-hmac"
organization := "com.gu"

val scala211ver = "2.11.8"
val scala212ver = "2.12.2"
val scalaVersions = List(scala211ver,scala212ver)

description in ThisBuild := "Wraps the panda play library to allow either panda cookie or HMAC shared secret auth"

scalacOptions ++= Seq("-feature", "-deprecation")

lazy val root = (project in file("."))
  .aggregate(play26project, play25project)
  .settings(
    // crossScalaVersions must be set to Nil on the aggregating project
    crossScalaVersions := Nil,
    publish / skip := false
  )

lazy val `play26project` = (project in file(".")).settings(
  crossScalaVersions := scalaVersions,
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play" % "2.6.0" % "provided",
    "com.typesafe.play" %% "play-ws" % "2.6.0" % "provided",
    "com.gu" %% "hmac-headers" % "1.1.2",
    "com.gu" %% "pan-domain-auth-play_2-6" % "0.7.2"
  )
)

lazy val `play25project` = (project in file(".")).settings(
  crossScalaVersions := List(scala211ver),
  crossPaths := true,
  autoScalaLibrary := false,
  libraryDependencies ++= Seq(
    "com.typesafe.play" % "play_2.11" % "2.4.0",
    "com.typesafe.play" % "play-ws_2.11" % "2.4.0",
    "com.gu" %% "hmac-headers" % "1.1.2",
    "com.gu" % "pan-domain-auth-play_2-5_2.11" % "0.5.1"
  )
)

publishMavenStyle := true
bintrayOrganization := Some("guardian")
bintrayRepository := "editorial-tools"
licenses += ("Apache-2.0", url("https://github.com/guardian/tags-thrift-schema/blob/master/LICENSE"))
