package com.malliina.cdk

import software.amazon.awscdk.{Duration, Stack}
import software.amazon.awscdk.services.lambda.{CfnParametersCode, IFunction, Function as LambdaFunction, Runtime as LambdaRuntime}
import software.amazon.awscdk.services.logs.{FilterPattern, ILogGroup, SubscriptionFilter}
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
//  val streamLambda: IFunction =
//    LambdaFunction.fromFunctionName(stack, "StreamFunction", "LogsToElasticsearch_search")
//  val filter = SubscriptionFilter.Builder
//    .create(stack, "Filter")
//    .logGroup(function.getLogGroup)
//    .destination(LambdaDestination.Builder.create(streamLambda).build())
//    .filterPattern(FilterPattern.spaceDelimited("timestamp", "level", "logger", "message"))
//    .build()
