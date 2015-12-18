
name := "regex"

organization := "iguana"

version := "0.1.0"

isSnapshot := true

scalaVersion := "2.11.7"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "junit" % "junit" % "4.11",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

lazy val utils = ProjectRef(file("../utils"), "utils")

val main = Project(id = "regex", base = file(".")).dependsOn(utils)
