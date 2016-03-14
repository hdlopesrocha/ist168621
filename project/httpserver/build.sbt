name := """httpserver"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


resolvers += (
  "Local Maven Repository" at "file:///" + Path.userHome.absolutePath + "/.m2/repository"
)

libraryDependencies += "org.json" % "json" % "20160212"
libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3"
libraryDependencies += "org.kurento" % "kurento-java" % "6.4.0"
libraryDependencies += "org.kurento" % "kurento-client" % "6.4.0"
libraryDependencies += "org.kurento" % "kurento-repository-client" % "6.4.0"
