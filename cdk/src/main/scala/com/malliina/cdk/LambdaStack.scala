package com.malliina.cdk

import software.amazon.awscdk.{Duration, Stack}
import software.amazon.awscdk.services.lambda.{CfnParametersCode, FunctionAttributes, IFunction, Function as LambdaFunction, Runtime as LambdaRuntime}
import software.amazon.awscdk.services.logs.{CfnSubscriptionFilter, FilterPattern, ILogGroup, LogGroup, RetentionDays, SubscriptionFilter}
import software.amazon.awscdk.services.logs.destinations.LambdaDestination
import software.constructs.Construct

class LambdaStack(scope: Construct, val constructId: String)
  extends Stack(scope, constructId, CDK.stackProps):
  val stack = this
  val code = CfnParametersCode.Builder.create().build()
  val function = LambdaFunction.Builder
    .create(stack, "Function")
    .handler("com.malliina.lambda.LambdaHandler")
    .runtime(LambdaRuntime.JAVA_11)
    .code(code)
    .memorySize(256)
    .timeout(Duration.seconds(60))
    .build()
  val streamLambda = LambdaFunction.fromFunctionArn(
    stack,
    "StreamFunc",
    "arn:aws:lambda:eu-west-1:297686094835:function:LogsToElasticsearch_search"
  )
  // https://github.com/aws/aws-cdk/issues/12958
  val logGroup =
    LogGroup.fromLogGroupName(stack, "FunctionLogGroup", s"/aws/lambda/${function.getFunctionName}")
  val filter = SubscriptionFilter.Builder
    .create(stack, "Filter")
//  .logGroup(function.getLogGroup)
    .logGroup(logGroup)
    .destination(LambdaDestination.Builder.create(streamLambda).build())
    .filterPattern(FilterPattern.spaceDelimited("timestamp", "level", "logger", "message"))
    .build()
