import sbt.Keys.{publishMavenStyle, scmInfo}
import sbt.url

name := "panda-hmac"
organization := "com.gu"

val scala211ver = "2.11.8"
val scala212ver = "2.12.2"
val scala213ver = "2.13.7"

ThisBuild / description := "Wraps the panda play library to allow either panda cookie or HMAC shared secret auth"

scalacOptions ++= Seq("-feature", "-deprecation")

lazy val root = (project in file("."))
  .aggregate(play28project, play26project)
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

lazy val `play28project` = (project in file("play")).settings(
  crossScalaVersions := List(scala212ver,scala213ver),
  target := file("target/play_2-8"),
  name := "panda-hmac-play_2-8",
  organization := "com.gu",
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play" % "2.8.11" % "provided",
    "com.typesafe.play" %% "play-ws" % "2.8.11" % "provided",
    "com.gu" %% "hmac-headers" % "1.2.0",
    "com.gu" %% "pan-domain-auth-play_2-8" % "1.0.4"
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
)

lazy val `play26project` = (project in file("play")).settings(
  crossScalaVersions := List(scala211ver,scala212ver),
  target := file("target/play_2-6"),
  name := "panda-hmac-play_2-6",
  organization := "com.gu",
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play" % "2.6.0" % "provided",
    "com.typesafe.play" %% "play-ws" % "2.6.0" % "provided",
    "com.gu" %% "hmac-headers" % "1.2.0",
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
)
