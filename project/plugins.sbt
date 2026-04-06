resolvers += Resolver.bintrayRepo("alpeb", "sbt-plugins")

// scala-thrift plugin
addSbtPlugin("com.twitter" % "scrooge-sbt-plugin" % "24.2.0")

// scala-protobuf plugin
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.7")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.10.10"