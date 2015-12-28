
name := "iguana"

organization := "iguana"

version := "0.1.0"

scalaVersion := "2.11.7"

scalaSource in Compile := baseDirectory.value / "src"

scalaSource in Test := baseDirectory.value / "test"

javaSource in Compile := baseDirectory.value / "src"

javaSource in Test := baseDirectory.value / "test"

resourceDirectory in Compile := baseDirectory.value / "src" / "resources"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "junit" % "junit" % "4.11",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)


javacOptions in (Compile, doc) ++= Seq("-source", "1.8")
javacOptions in (Compile,doc) += "-Xdoclint:none"

compileOrder in Compile := CompileOrder.Mixed


lazy val utils = if (file("../utils").exists) ProjectRef(file("../utils"), "utils")
                 else ProjectRef(uri("https://github.com/iguana-parser/utils.git"), "utils")

lazy val parseTrees = if (file("../parse-trees").exists) ProjectRef(file("../parse-trees"), "parse-trees")
                      else ProjectRef(uri("https://github.com/iguana-parser/parse-trees.git"), "parse-trees")

lazy val regex = if (file("../regex").exists) ProjectRef(file("../regex"), "regex")
                 else ProjectRef(uri("https://github.com/iguana-parser/regex.git"), "regex")

// Uncomment the following line (and comment out the next one) when generating Eclipse projects using SBT
// val main = Project("iguana", file(".")).dependsOn(utils, parseTrees, regex)

val main = Project("iguana", file(".")).aggregate(utils, parseTrees, regex).dependsOn(utils, parseTrees, regex)


