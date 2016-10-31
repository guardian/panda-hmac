name := "panda-hmac"
organization := "com.gu"

scalaVersion := "2.11.8"

description in ThisBuild := "Wraps the panda play library to allow either panda cookie or HMAC shared secret auth"

scalacOptions ++= Seq("-feature", "-deprecation")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.4.0" % "provided",
  "com.typesafe.play" %% "play-ws" % "2.4.0" % "provided",
  "com.gu" %% "hmac-headers" % "1.1",
  "com.gu" %% "pan-domain-auth-play_2-5" % "0.3.0"
  )

publishMavenStyle := true
bintrayOrganization := Some("guardian")
bintrayRepository := "editorial-tools"
licenses += ("Apache-2.0", url("https://github.com/guardian/tags-thrift-schema/blob/master/LICENSE"))
