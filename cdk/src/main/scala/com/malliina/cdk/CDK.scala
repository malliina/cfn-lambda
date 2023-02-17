package com.malliina.cdk

import software.amazon.awscdk.core.{Environment, StackProps, App as AWSApp}

object CDK:
  val stackProps =
    StackProps
      .builder()
      .env(Environment.builder().account("297686094835").region("eu-west-1").build())
      .build()

  def main(args: Array[String]): Unit =
    val app = new AWSApp()

    val lambda = LambdaStack(app, "lambda-stack")
    val lambdaPipeline =
      LambdaPipeline(LambdaConf(lambda.code, lambda.constructId), app, "lambda-pipe")

    val assembly = app.synth()
