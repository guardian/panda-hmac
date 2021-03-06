import sbt.Keys.{publishMavenStyle, scmInfo}

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
    publish / skip := false,
    // Add sonatype repository settings
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    )
  )

lazy val `play26project` = (project in file("play2-6")).settings(
  crossScalaVersions := scalaVersions,
  name := "panda-hmac-play_2-6",
  organization := "com.gu",
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play" % "2.6.0" % "provided",
    "com.typesafe.play" %% "play-ws" % "2.6.0" % "provided",
    "com.gu" %% "hmac-headers" % "1.1.2",
    "com.gu" %% "pan-domain-auth-play_2-6" % "0.7.2"
  ),
  // Add sonatype repository settings
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging
  ),
  homepage := Some(url("https://github.com/guardian/panda-hmac")),
  scmInfo := Some(ScmInfo(url("https://github.com/guardian/panda-hmac"),"git@github.com:guardian/panda-hmac.git")),
  publishMavenStyle := true,
  developers := List(
    Developer(
      id    = "GuardianEdTools",
      name  = "guardian editorial tools",
      email = "digitalcms.dev@theguardian.com",
      url   = url("https://www.theguardian.com")
    )
  ),
  licenses += ("Apache-2.0", url("https://github.com/guardian/tags-thrift-schema/blob/master/LICENSE")),
  publishConfiguration := publishConfiguration.value.withOverwrite(true)
)

lazy val `play25project` = (project in file("play2-5")).settings(
  crossScalaVersions := List(scala211ver),
  name := "panda-hmac-play_2-5",
  organization := "com.gu",
  crossPaths := true,
  autoScalaLibrary := false,
  libraryDependencies ++= Seq(
    "com.typesafe.play" % "play_2.11" % "2.4.0",
    "com.typesafe.play" % "play-ws_2.11" % "2.4.0",
    "com.gu" %% "hmac-headers" % "1.1.2",
    "com.gu" % "pan-domain-auth-play_2-5_2.11" % "0.5.1"
  ),
  // Add sonatype repository settings
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging
  ),
  homepage := Some(url("https://github.com/guardian/panda-hmac")),
  scmInfo := Some(ScmInfo(url("https://github.com/guardian/panda-hmac"),"git@github.com:guardian/panda-hmac.git")),
  developers := List(
    Developer(
      id    = "GuardianEdTools",
      name  = "guardian editorial tools",
      email = "digitalcms.dev@theguardian.com",
      url   = url("https://www.theguardian.com")
    )
  ),
  publishMavenStyle := true,
  licenses += ("Apache-2.0", url("https://github.com/guardian/tags-thrift-schema/blob/master/LICENSE")),
  publishConfiguration := publishConfiguration.value.withOverwrite(true)
)
