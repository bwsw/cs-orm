/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "cs-entities",
    version := "4.9.3",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.0",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "org.scalatest" %% "scalatest" % "3.0.1" % "it,test",
      "com.google.guava" % "guava" % "23.0",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.8",
      "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.8.8"
    ),
    pomIncludeRepository := { _ => false },
    licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    homepage := Some(url("https://github.com/bwsw/cs-entities")),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) {
        Some("snapshots" at nexus + "content/repositories/snapshots")
      } else {
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      }
    },
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/bwsw/cs-entities"),
        "scm:git@github.com:bwsw/cs-entities.git"
      )
    ),
    developers := List(
      Developer(
        id    = "bitworks",
        name  = "Bitworks Software, Ltd.",
        email = "bitworks@bw-sw.com",
        url   = url("http://bitworks.software/")
      )
    ),
    inConfig(IntegrationTest)(Defaults.itSettings)
  )
