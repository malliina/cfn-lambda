package com.malliina.cdk

import software.amazon.awscdk.pipelines.CodePipeline
import software.amazon.awscdk.{CfnOutput, Stack}
import software.amazon.awscdk.services.codebuild.{BuildEnvironmentVariable, BuildEnvironmentVariableType}
import software.amazon.awscdk.services.codecommit.Repository
import software.amazon.awscdk.services.codepipeline.{IAction, StageProps}
import software.amazon.awscdk.services.elasticbeanstalk.CfnConfigurationTemplate.ConfigurationOptionSettingProperty
import software.amazon.awscdk.services.iam.ServicePrincipal
import software.constructs.Construct
import software.amazon.jsii.Builder as CfnBuilder

import scala.jdk.CollectionConverters.{MapHasAsJava, SeqHasAsJava}

trait CDKSyntax:
  def principal(service: String) = ServicePrincipal.Builder.create(service).build()
  def list[T](xs: T*) = xs.asJava
  def map[T](kvs: (String, T)*) = Map(kvs*).asJava
  def optionSetting(namespace: String, optionName: String, value: String) =
    init(ConfigurationOptionSettingProperty.builder()) { b =>
      b.namespace(namespace).optionName(optionName).value(value)
    }
  def outputs(scope: Stack)(kvs: (String, String)*) = kvs.map { case (k, v) =>
    init(CfnOutput.Builder.create(scope, k)) { b =>
      b.exportName(k).value(v)
    }
  }
  def buildEnv(value: String) =
    init(BuildEnvironmentVariable.builder()) { b =>
      b.`type`(BuildEnvironmentVariableType.PLAINTEXT).value(value)
    }
  def stage(name: String)(actions: IAction*) =
    init(StageProps.builder()) { b =>
      b.stageName(name).actions(list(actions*))
    }
  def codeCommit(construct: Construct, id: String)(prep: Repository.Builder => Repository.Builder) =
    prep(Repository.Builder.create(construct, id)).build()

  def codePipeline(construct: Construct, id: String)(
    prep: CodePipeline.Builder => CodePipeline.Builder
  ) =
    init[CodePipeline, CodePipeline.Builder](CodePipeline.Builder.create(construct, id))(prep)

  private def init[T, B <: CfnBuilder[T]](b: B)(f: B => B): T = f(b).build()
