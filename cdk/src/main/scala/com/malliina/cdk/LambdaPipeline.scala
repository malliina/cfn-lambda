package com.malliina.cdk

import buildinfo.BuildInfo
import software.amazon.awscdk.{CfnCapabilities, DefaultStackSynthesizer, Stack}
import software.amazon.awscdk.services.codebuild.*
import software.amazon.awscdk.services.codecommit.Repository
import software.amazon.awscdk.services.codepipeline.actions.{CloudFormationCreateReplaceChangeSetAction, CloudFormationExecuteChangeSetAction, CodeBuildAction, CodeCommitSourceAction}
import software.amazon.awscdk.services.codepipeline.{Artifact, Pipeline}
import software.amazon.awscdk.services.iam.{Effect, PolicyStatement}
import software.amazon.awscdk.services.lambda.CfnParametersCode
import software.constructs.Construct

case class LambdaConf(code: CfnParametersCode, constructId: String)

class LambdaPipeline(conf: LambdaConf, scope: Construct, stackName: String)
  extends Stack(scope, stackName, CDK.stackProps)
  with CDKSyntax:
  val stack = this
  val source = Repository.Builder
    .create(stack, "Source")
    .repositoryName(getStackName)
    .description(s"Code for $getStackName.")
    .build()
  val buildEnv = BuildEnvironment
    .builder()
    .buildImage(LinuxBuildImage.STANDARD_5_0)
    .computeType(ComputeType.MEDIUM)
    .build()
  val build = PipelineProject.Builder
    .create(stack, "Build")
    .projectName(getStackName)
    .environment(buildEnv)
    .buildSpec(BuildSpec.fromSourceFilename("lambda/buildspec-function.yml"))
    .build()
  val stackOutPath = s"${conf.constructId}.template.json"
  val stackBuild = PipelineProject.Builder
    .create(stack, "StackBuild")
    .projectName(s"$getStackName-stack")
    .environment(buildEnv)
    .buildSpec(BuildSpec.fromSourceFilename("lambda/buildspec-stack.yml"))
    .environmentVariables(
      map(
        "CDK_VERSION" -> buildEnv(BuildInfo.cdkVersion),
        "LAMBDA_STACK" -> buildEnv(conf.constructId),
        "OUTPUT" -> buildEnv(stackOutPath)
      )
    )
    .build()
  // https://github.com/aws/aws-cdk/issues/11025#issuecomment-1219129002
  val cdkAssetPublishingPolicy = PolicyStatement.Builder
    .create()
    .effect(Effect.ALLOW)
    .actions(list("sts:AssumeRole"))
    .resources(
      list(
        s"arn:aws:iam::$getAccount:role/cdk-${DefaultStackSynthesizer.DEFAULT_QUALIFIER}-file-publishing-role-$getAccount-$getRegion"
      )
    )
    .build()
  stackBuild.addToRolePolicy(cdkAssetPublishingPolicy)
  val sourceOut = new Artifact()
  val buildOut = new Artifact()
  val stackBuildOut = new Artifact()
  val changeSetName = "LambdaChangeSet"
  val lambdaStackName = s"$getStackName-lambda"
  val pipeline = Pipeline.Builder
    .create(stack, "Pipeline")
    .pipelineName(getStackName)
    .stages(
      list(
        stage("Source")(
          CodeCommitSourceAction.Builder
            .create()
            .actionName("SourceAction")
            .repository(source)
            .branch("master")
            .output(sourceOut)
            .build()
        ),
        stage("Build")(
          CodeBuildAction.Builder
            .create()
            .actionName("BuildAction")
            .project(build)
            .input(sourceOut)
            .outputs(list(buildOut))
            .build(),
          CodeBuildAction.Builder
            .create()
            .actionName("StackBuildAction")
            .project(stackBuild)
            .input(sourceOut)
            .outputs(list(stackBuildOut))
            .build()
        ),
        stage("Stage")(
          CloudFormationCreateReplaceChangeSetAction.Builder
            .create()
            .actionName("StageAction")
            .changeSetName(changeSetName)
            .templatePath(stackBuildOut.atPath(stackOutPath))
            .stackName(lambdaStackName)
            .adminPermissions(true)
            .extraInputs(list(buildOut))
            .parameterOverrides(conf.code.assign(buildOut.getS3Location))
            .cfnCapabilities(
              list(
                CfnCapabilities.ANONYMOUS_IAM,
                CfnCapabilities.NAMED_IAM,
                CfnCapabilities.AUTO_EXPAND
              )
            )
            .build()
        ),
        stage("Deploy")(
          CloudFormationExecuteChangeSetAction.Builder
            .create()
            .actionName("DeployAction")
            .changeSetName(changeSetName)
            .stackName(lambdaStackName)
            .build()
        )
      )
    )
    .build()
