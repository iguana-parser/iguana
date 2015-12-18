
val main = Project("iguana", file(".")).aggregate(utils, parseTrees, regex).dependsOn(utils, parseTrees, regex)

lazy val utils = ProjectRef(file("../utils"), "utils")
lazy val parseTrees = ProjectRef(file("../parse-trees"), "parse-trees")
lazy val regex = ProjectRef(file("../regex"), "regex")



name := "iguana"

organization := "iguana"

version := "0.1.0"

isSnapshot := true

scalaVersion := "2.11.7"

scalaSource in Compile := baseDirectory.value / "src"

scalaSource in Test := baseDirectory.value / "test"

javaSource in Compile := baseDirectory.value / "src"

javaSource in Test := baseDirectory.value / "test"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "junit" % "junit" % "4.11",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)
