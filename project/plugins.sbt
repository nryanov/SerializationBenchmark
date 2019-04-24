resolvers += Resolver.bintrayRepo("alpeb", "sbt-plugins")

// scala-thrift plugin
addSbtPlugin("com.twitter" % "scrooge-sbt-plugin" % "19.4.0")

// scala-protobuf plugin
addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.20")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.8.4"