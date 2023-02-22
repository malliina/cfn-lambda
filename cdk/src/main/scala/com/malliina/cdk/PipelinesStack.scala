package com.malliina.cdk

import buildinfo.BuildInfo
import software.amazon.awscdk.{Stack, Stage}
import software.amazon.awscdk.pipelines.{CodeBuildStep, CodePipeline, CodePipelineSource, ShellStep}
import software.amazon.awscdk.services.codecommit.Repository
import software.constructs.Construct

class PipelinesStack(scope: Construct, stackName: String)
  extends Stack(scope, stackName, CDK.stackProps)
  with CDKSyntax:
  val stack = this
  val cdkVersion = BuildInfo.cdkVersion
  val source = Repository.Builder
    .create(stack, "Source")
    .repositoryName(getStackName)
    .description(s"Code for $getStackName.")
    .build()
  val pipeline = CodePipeline.Builder
    .create(stack, "Pipeline")
    .pipelineName(getStackName)
    .synth(
      ShellStep.Builder
        .create("Synth")
        .input(CodePipelineSource.codeCommit(source, "master"))
        .commands(list("./cdk/build.sh"))
        .build()
    )
    .build()

  val lambdaApp = pipeline.addStage(LambdaStage(stack, "qa"))

class LambdaStage(scope: Construct, id: String) extends Stage(scope, id):
  val stack = LambdaStack(this, "LambdaStack")
