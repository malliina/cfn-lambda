package com.malliina.cdk

import software.amazon.awscdk.services.iam.{Effect, PolicyStatement}
import software.amazon.awscdk.{Duration, Stack}
import software.amazon.awscdk.services.lambda.{CfnParametersCode, CfnPermission, Code, FunctionAttributes, IFunction, Permission, Function as LambdaFunction, Runtime as LambdaRuntime}
import software.amazon.awscdk.services.logs.{CfnSubscriptionFilter, FilterPattern, ILogGroup, LogGroup, RetentionDays, SubscriptionFilter, SubscriptionFilterOptions}
import software.amazon.awscdk.services.logs.destinations.LambdaDestination
import software.constructs.Construct

class LambdaStack(scope: Construct, val constructId: String)
  extends Stack(scope, constructId, CDK.stackProps)
  with CDKSyntax:
  val stack = this
  val code = CfnParametersCode.Builder.create().build()
  val assetCode = Code.fromAsset("jartarget")
  val function = LambdaFunction.Builder
    .create(stack, "Function")
    .handler("com.malliina.lambda.LambdaHandler")
    .runtime(LambdaRuntime.JAVA_11)
    .code(assetCode)
    .memorySize(256)
    .timeout(Duration.seconds(60))
    .build()
  val streamLambda =
    LambdaFunction.fromFunctionName(stack, "StreamFuncNamed", "LogsToElasticsearch_search")
//  val streamPermission = Permission
//    .builder()
//    .principal(principal("logs.amazonaws.com"))
//    .action("lambda:InvokeFunction")
//    .sourceArn(function.getLogGroup.getLogGroupArn)
//    .build()
//  streamLambda.addPermission("StreamPermission", streamPermission)
//  streamLambda.addToRolePolicy(PolicyStatement.Builder.create().effect(Effect.ALLOW).build())
  val filter = function.getLogGroup.addSubscriptionFilter(
    "SubscriptionFilter",
    SubscriptionFilterOptions
      .builder()
      .destination(LambdaDestination.Builder.create(streamLambda).build())
      .filterPattern(FilterPattern.spaceDelimited("timestamp", "level", "logger", "message"))
      .build()
  )
  val cfnPerm = CfnPermission.Builder
    .create(stack, "Perm")
    .principal("logs.amazonaws.com")
    .action("lambda:InvokeFunction")
    .sourceArn(function.getLogGroup.getLogGroupArn)
    .build()
  filter.getNode.addDependency(cfnPerm)
