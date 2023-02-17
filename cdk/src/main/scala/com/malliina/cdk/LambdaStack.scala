package com.malliina.cdk

import software.amazon.awscdk.core.{Construct, Duration, Stack}
import software.amazon.awscdk.services.lambda.{CfnParametersCode, Function as LambdaFunction, Runtime as LambdaRuntime}

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
