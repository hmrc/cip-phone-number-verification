import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val hmrcBootstrapVersion = "5.23.2-RC2"
  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"  % hmrcBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"         % "0.64.0",
    "io.jsonwebtoken"         % "jjwt-api"                    % "0.10.8"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % hmrcBootstrapVersion  % "test, it",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"    % "0.64.0"              % Test,
    "com.vladsch.flexmark"    %  "flexmark-all"               % "0.36.8"              % "test, it",
    "org.scalatestplus"       %% "mockito-3-12"               % "3.2.10.0"            % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"          % "5.1.0"                % "it,test"
  )
}
