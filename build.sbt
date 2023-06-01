import sbt.Keys.{publishMavenStyle, scmInfo}
import sbt.url
import ReleaseTransformations._
import xerial.sbt.Sonatype._

name := "panda-hmac"
organization := "com.gu"

val scala212ver = "2.12.16"
val scala213ver = "2.13.8"

ThisBuild / description := "Wraps the panda play library to allow either panda cookie or HMAC shared secret auth"

scalacOptions ++= Seq("-feature", "-deprecation")

lazy val commonSettings = sonatypeSettings ++ Seq(
  organization := "com.gu",
  scalaVersion := scala212ver,
  // Add sonatype repository settings
  publishTo := sonatypePublishToBundle.value,
  homepage := Some(url("https://github.com/guardian/panda-hmac")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/guardian/panda-hmac"),
    "git@github.com:guardian/panda-hmac.git")
  ),
  publishMavenStyle := true,
  developers := List(
    Developer(
      id    = "GuardianEdTools",
      name  = "Guardian Editorial Tools",
      email = "digitalcms.dev@theguardian.com",
      url   = url("https://www.theguardian.com")
    )
  ),
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  releaseCrossBuild := true, // true if you cross-build the project for multiple Scala versions
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    // For non cross-build projects, use releaseStepCommand("publishSigned")
    releaseStepCommandAndRemaining("+publishSigned"),
    releaseStepCommand("sonatypeBundleRelease"),
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
)

lazy val `play28project` = (project in file("play")).settings(commonSettings).settings(
  name := "panda-hmac-play_2-8",
  crossScalaVersions := List(scala212ver, scala213ver),
  target := file("target/play_2-8"),
  publishArtifact := true,
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play" % "2.8.11" % "provided",
    "com.typesafe.play" %% "play-ws" % "2.8.11" % "provided",
    "com.gu" %% "hmac-headers" % "2.0.0-SNAPSHOT",
    "com.gu" %% "pan-domain-auth-play_2-8" % "1.2.0",
    "org.scalatest" %% "scalatest" % "3.2.10" % "test"
  ),
)

lazy val `play27project` = (project in file("play")).settings(commonSettings).settings(
  name := "panda-hmac-play_2-7",
  crossScalaVersions := List(scala212ver),
  target := file("target/play_2-7"),
  publishArtifact := true,
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play" % "2.7.0" % "provided",
    "com.typesafe.play" %% "play-ws" % "2.7.0" % "provided",
    "com.gu" %% "hmac-headers" % "2.0.0-SNAPSHOT",
    "com.gu" %% "pan-domain-auth-play_2-7" % "1.2.0",
    "org.scalatest" %% "scalatest" % "3.2.10" % "test"
  ),
)

lazy val root = (project in file("."))
  .aggregate(play28project, play27project)
  .settings(commonSettings)
  .settings(
    publishArtifact := false,
    publish / skip := true,
  )
