addSbtPlugin("com.eed3si9n"                             % "sbt-assembly"        % "0.15.0")
addSbtPlugin("com.typesafe.sbt"                        %% "sbt-native-packager" % "1.7.5")
addSbtPlugin("org.scalameta"                            % "sbt-scalafmt"        % "2.4.6")
addSbtPlugin("io.github.davidgregory084"                % "sbt-tpolecat"        % "0.1.13")
addSbtPlugin("ch.epfl.scala"                            % "sbt-missinglink"     % "0.3.1")
addSbtPlugin("com.thesamet"                             % "sbt-protoc"          % "1.0.0-RC4")
addSbtPlugin("com.coralogix"                            % "sbt-protodep"        % "0.0.7")
addSbtPlugin("com.geirsson"                             % "sbt-ci-release"      % "1.5.3")
libraryDependencies += "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen"    % "0.4.2"
