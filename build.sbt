import android.Keys._

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

android.Plugin.androidBuild

name := "me.handshake.android"

scalaVersion := "2.11.5"

proguardCache in Android ++= Seq(
  ProguardCache("org.scaloid") % "org.scaloid"
)

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize", "-keepattributes Signature", "-printseeds target/seeds.txt", "-printusage target/usage.txt"
  , "-dontwarn scala.collection.**" // required from Scala 2.11.4
  , "-dontwarn com.facebook.**"
  , "-dontwarn com.parse.**"
  , "-dontwarn com.getpebble.**"
)

libraryDependencies += "org.scaloid" %% "scaloid" % "3.6.1-10" withSources() withJavadoc()
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.1"
libraryDependencies += "com.getpebble" % "pebblekit" % "2.6.0"

resolvers += "Sonatype OSS Public" at "https://oss.sonatype.org/content/groups/public/"


scalacOptions in Compile += "-feature"

run <<= run in Android

install <<= install in Android

retrolambdaEnable in Android := false
