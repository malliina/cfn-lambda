package com.malliina.cdk

import buildinfo.BuildInfo
import software.amazon.awscdk.Stack
import software.amazon.awscdk.pipelines.{CodePipeline, CodePipelineSource, ShellStep}
import software.amazon.awscdk.services.codecommit.Repository
import software.constructs.Construct

class PipelineStack(scope: Construct, stackName: String)
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
        .commands(list(s"npm install -g aws-cdk@$cdkVersion", "cdk synth"))
        .build()
    )
    .build()

//class AppStack(scope: Construct, id: String) extends Stack(scope, id):
