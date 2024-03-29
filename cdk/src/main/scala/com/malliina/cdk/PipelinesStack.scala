package com.malliina.cdk

import buildinfo.BuildInfo
import software.amazon.awscdk.{Stack, Stage}
import software.amazon.awscdk.pipelines.{CodeBuildStep, CodePipeline, CodePipelineSource, ShellStep}
import software.amazon.awscdk.services.codecommit.Repository
import software.constructs.Construct

/** Uses CDK Pipelines, apparently this is modern.
  */
class PipelinesStack(scope: Construct, stackName: String)
  extends Stack(scope, stackName, CDK.stackProps)
  with CDKSyntax:
  val stack = this
  val cdkVersion = BuildInfo.cdkVersion
  val source = codeCommit(stack, "Source") { builder =>
    builder
      .repositoryName(getStackName)
      .description(s"Code for $getStackName.")
  }
  val jarTarget = "jartarget"
  val pipeline = codePipeline(stack, "Pipeline") { p =>
    p.pipelineName(getStackName)
      .synth(
        ShellStep.Builder
          .create("Synth")
          .input(CodePipelineSource.codeCommit(source, "master"))
          .commands(list("./cdk/build.sh"))
          .env(map("CDK_VERSION" -> BuildInfo.cdkVersion, "OUTPUT_DIR" -> jarTarget))
          .build()
      )
  }
  val lambdaQaApp = pipeline.addStage(LambdaStage(stack, "qa", jarTarget))
  val lambdaProdApp = pipeline.addStage(LambdaStage(stack, "prod", jarTarget))

class LambdaStage(scope: Construct, id: String, jarTarget: String) extends Stage(scope, id):
  val stack = LambdaStack(this, "LambdaStack", jarTarget)
